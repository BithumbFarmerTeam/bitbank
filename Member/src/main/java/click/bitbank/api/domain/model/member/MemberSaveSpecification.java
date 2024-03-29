package click.bitbank.api.domain.model.member;

import click.bitbank.api.application.response.MemberSignupResponse;
import click.bitbank.api.domain.dao.factory.MemberFactory;
import click.bitbank.api.domain.repository.MemberRepository;
import click.bitbank.api.infrastructure.exception.status.AlreadyDataException;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.infrastructure.exception.status.RegistrationFailException;
import click.bitbank.api.presentation.member.request.MemberSignupRequest;
import click.bitbank.api.presentation.member.request.SocialLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberSaveSpecification {

    private final MemberRepository memberRepository;
    private final MemberFactory memberFactory;

    /**
     * 회원 중복 검사 및 계정 생성
     * @param request : 저장할 회원 정보
     * @return Mono<MemberRegistrationResponse> : 저장된 회원 정보
     */
    public Mono<MemberSignupResponse> memberExistCheckAndRegistration(MemberSignupRequest request) {

        return memberRepository.findByMemberLoginIdAndDelDateIsNull(request.getMemberLoginId())
            .hasElement()
            .flatMap(alreadyMember -> {

                if (alreadyMember) return Mono.error(new AlreadyDataException(ExceptionMessage.AlreadyDataMember.getMessage()));

                return this.signup(request)
                    .flatMap(savedMember -> Mono.just(
                        MemberSignupResponse.builder()
                            .memberId(savedMember.getMemberId())
                            .build()
                    ));
            }
        );
    }

    /**
     * 회원 계정 생성
     * @param request : 저장할 회원 정보
     * @return Mono<Member> : 저장된 회원 정보
     */
    private Mono<Member> signup(MemberSignupRequest request) {

        return memberRepository.save(
            memberFactory.memberBuilder(
                request.getMemberLoginId(),
                request.getMemberName(),
                request.getMemberPassword(),
                MemberType.N
            )
        ).switchIfEmpty(Mono.error(new RegistrationFailException(ExceptionMessage.SaveFailMember.getMessage())));
    }

    /**
     * 소셜 회원 계정 생성
     * @param request : 저장할 회원 정보
     * @return Mono<Member> : 저장된 회원 정보
     */
    public Mono<Member> socialLoginRegistration(SocialLoginRequest request) {

        return memberRepository.save(
            memberFactory.socialMemberBuilder(
                request.getSocialToken(),
                request.getMemberName(),
                MemberType.S
            )
        ).switchIfEmpty(Mono.error(new RegistrationFailException(ExceptionMessage.SaveFailMember.getMessage())));
    }
}
