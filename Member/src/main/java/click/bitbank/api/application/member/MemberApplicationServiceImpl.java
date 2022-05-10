package click.bitbank.api.application.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import click.bitbank.api.application.response.MemberInfoResponse;
import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.application.response.MemberSignupResponse;
import click.bitbank.api.domain.model.member.MemberLoginSpecification;
import click.bitbank.api.domain.model.member.MemberSaveSpecification;
import click.bitbank.api.domain.model.member.MemberSearchSpecification;
import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.presentation.member.request.MemberLoginRequest;
import click.bitbank.api.presentation.member.request.MemberSignupRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApplicationServiceImpl implements MemberApplicationService {

    private final MemberSaveSpecification memberSaveSpecification;
    private final MemberSearchSpecification memberSearchSpecification;

    private final MemberLoginSpecification memberLoginSpecification;

    /**
     * 회원 계정 생성
     * @param serverRequest : 전달된 Request
     * @return Mono<MemberRegistrationResponse> : 저장된 회원 정보
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberSignupResponse> signup(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberSignupRequest.class).flatMap(
            request -> {
                request.verify(); // Request 유효성 검사

                return memberSaveSpecification.memberExistCheckAndRegistration(request); // 회원 계정 생성
            }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Mono<MemberLoginResponse> login(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberLoginRequest.class).flatMap(
                request -> {
                    request.verify(); // Request 유효성 검사
                    return memberLoginSpecification.memberExistCheckAndLogin(request);
                }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Mono<MemberInfoResponse> findMemberInfo(ServerRequest request) {

        String memberIdStr = request.pathVariable("memberId");
        if (StringUtils.isBlank(memberIdStr)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberId.getMessage());

        int memberId = Integer.parseInt(memberIdStr);

        return memberSearchSpecification.getMemberInfo(memberId);
    }

}
