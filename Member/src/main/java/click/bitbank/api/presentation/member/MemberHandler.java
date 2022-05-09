package click.bitbank.api.presentation.member;

import click.bitbank.api.application.member.MemberApplicationService;
import click.bitbank.api.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.application.response.MemberInfoResponse;
import click.bitbank.api.application.response.MemberSignupResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
public class MemberHandler {

    private final MemberApplicationService memberApplicationService;
    private final KafkaProducerService kafkaProducerService;

    /**
     * 일반 회원 가입
     * @param request : 가입할 회원 정보
     * @return Mono<ServerResponse> : 등록한 회원 정보
     */
    public Mono<ServerResponse> signup(ServerRequest request) {

        Mono<MemberSignupResponse> response = memberApplicationService.signup(request)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSuccess(memberRegistrationResponse -> {
                // Kafka Message 발행
                kafkaProducerService.sendMessage(
                    String.format("%s Event >>> %s(memberId: %d) 생성",
                        request.path(),
                        memberRegistrationResponse.getMemberId()
                    )
                );
            });

        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response, MemberSignupResponse.class);
    }

    /**
     * 로그인
     * @param request : 로그인 정보
     * @return Mono<ServerResponse> : 권한 인증 정보
     */
    public Mono<ServerResponse> login(ServerRequest request) {

        Mono<MemberLoginResponse> response = memberApplicationService.login(request)
                .subscribeOn(Schedulers.boundedElastic());

        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response, MemberLoginResponse.class);
    }

    /**
     * 회원 정보 조회
     * @param request : 조회할 회원 정보
     * @return Mono<ServerResponse> : 조회된 회원 정보
     */
    public Mono<ServerResponse> findMemberInfo(ServerRequest request) {

        Mono<MemberInfoResponse> response = memberApplicationService.findMemberInfo(request)
                .subscribeOn(Schedulers.boundedElastic());

        return  ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response, MemberInfoResponse.class);
    }

}
