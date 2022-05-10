package click.bitbank.api.domain.model.member;

import click.bitbank.api.domain.repository.MemberRepository;
import click.bitbank.api.infrastructure.exception.status.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.presentation.member.request.MemberLoginRequest;
import click.bitbank.api.application.member.MemberSha256;
import click.bitbank.api.infrastructure.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
public class MemberLoginSpecification {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Value("${jwt.accessExpires}")
    private String accessExpiresString;

    @Value("${jwt.refreshExpires}")
    private String refreshExpiresString;

    public Mono<MemberLoginResponse> memberExistCheckAndLogin(MemberLoginRequest request) {

        Mono<Member> member = memberRepository.findByMemberNameAndMemberPassword(request.getMemberName(), MemberSha256.encrypt(request.getMemberPassword()));
        return member
                .hasElement()
                .flatMap(hasMember -> {
                    if (!hasMember) return Mono.error(new UnauthorizedException(ExceptionMessage.NotFoundLoginMember.getMessage()));

                    return MakeMemberLoginResponse(member);
                });
    }

    private Mono<MemberLoginResponse> MakeMemberLoginResponse(Mono<Member> member) {
        return member.flatMap(m -> {
            String accessToken = jwtProvider.createJwtToken(m, Long.parseLong(accessExpiresString));
            String refreshToken = jwtProvider.createJwtToken(m, Long.parseLong(refreshExpiresString));
            return Mono.just(new MemberLoginResponse(m.getMemberId(), accessToken, refreshToken));
        });
    }
}
