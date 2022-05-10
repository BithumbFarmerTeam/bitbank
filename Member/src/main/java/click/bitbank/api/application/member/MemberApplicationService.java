package click.bitbank.api.application.member;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import click.bitbank.api.application.response.MemberInfoResponse;
import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.application.response.MemberSignupResponse;

public interface MemberApplicationService {

    Mono<MemberSignupResponse> signup(ServerRequest request); // 회원 가입

    Mono<MemberLoginResponse> login(ServerRequest request); // 로그인

    Mono<MemberInfoResponse> findMemberInfo(ServerRequest request); // 회원 정보 조회

}