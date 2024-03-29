package click.bitbank.api.domain.model.member;

import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.domain.repository.MemberRepository;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.infrastructure.exception.status.NotFoundDataException;
import click.bitbank.api.infrastructure.exception.status.UnauthorizedException;
import click.bitbank.api.infrastructure.jwt.JwtProvider;
import click.bitbank.api.infrastructure.util.MemberSha256;
import click.bitbank.api.presentation.member.request.MemberLoginRequest;
import click.bitbank.api.presentation.member.request.SocialLoginRequest;
import click.bitbank.api.presentation.shared.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberLoginSpecification {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final MemberSaveSpecification memberSaveSpecification;

    @Value("${jwt.accessExpires}")
    private String accessExpiresString;

    @Value("${jwt.refreshExpires}")
    private String refreshExpiresString;

    /**
     * 소셜 로그인 처리
     * @param request : 일반 회원 로그인 정보
     * @return Mono<MemberLoginResponse> : 권한 정보
     */
    public Mono<MemberLoginResponse> memberExistCheckAndSocialLogin(SocialLoginRequest request) {

        Mono<Member> member = memberRepository.findBySocialTokenAndDelDateIsNull(request.getSocialToken());
        return member
            .hasElement()
            .flatMap(hasMember -> {
                if (!hasMember) { // 초기 로그인의 경우 회원 정보 생성 후 로그인 처리
                    return this.makeMemberLoginResponse(memberSaveSpecification.socialLoginRegistration(request));
                }

                return this.makeMemberLoginResponse(member);
            });
    }

    /**
     * 일반 회원 로그인 처리
     * @param request : 일반 회원 로그인 정보
     * @return Mono<MemberLoginResponse> : 권한 정보
     */
    public Mono<MemberLoginResponse> memberExistCheckAndLogin(MemberLoginRequest request) {

        Mono<Member> member = memberRepository.findByMemberLoginIdAndMemberPasswordAndDelDateIsNull(request.getMemberLoginId(), MemberSha256.encrypt(request.getMemberPassword()));
        return member
            .hasElement()
            .flatMap(hasMember -> {
                if (!hasMember) return Mono.error(new UnauthorizedException(ExceptionMessage.NotFoundLoginMember.getMessage()));

                return this.makeMemberLoginResponse(member);
            });
    }

    /**
     * 회원 권한 정보 구성 (JWT 발급)
     * @param member 회원 정보
     * @return Mono<MemberLoginResponse> : 권한 정보
     */
    private Mono<MemberLoginResponse> makeMemberLoginResponse(Mono<Member> member) {
        return member.flatMap(m -> {
            String accessToken = jwtProvider.createJwtToken(m, Long.parseLong(accessExpiresString));
            String refreshToken = jwtProvider.createJwtToken(m, Long.parseLong(refreshExpiresString));

            m.setRefreshToken(refreshToken);

            return updateRefreshTokenByMemberLogin(m)
                .flatMap(i ->
                    Mono.just(MemberLoginResponse.builder()
                        .memberId(m.getMemberId())
                        .memberName(m.getMemberName())
                        .memberType(m.getMemberType())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                );
        });
    }

    private Mono<Member> updateRefreshTokenByMemberLogin(Member member) {
        return memberRepository.save(member);
    }

    /**
     * 로그아웃 처리
     * @param memberId : 회원 고유번호
     * @return Mono<SuccessResponse> : 성공 여부
     */
    public Mono<SuccessResponse> memberExistCheckAndLogout(int memberId) {

        return memberRepository.findById(memberId)
            .flatMap(member -> {
                member.logout();
                return memberRepository.save(member);
            })
            .flatMap(member -> Mono.just(new SuccessResponse()))
            .switchIfEmpty(Mono.error(new NotFoundDataException(ExceptionMessage.NotFoundMember.getMessage())));
    }
}
