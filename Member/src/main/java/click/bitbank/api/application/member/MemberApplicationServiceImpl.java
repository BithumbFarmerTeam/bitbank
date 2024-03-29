package click.bitbank.api.application.member;

import click.bitbank.api.application.response.AlarmCountResponse;
import click.bitbank.api.application.response.AlarmListResponse;
import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.application.response.MemberSignupResponse;
import click.bitbank.api.domain.model.alarm.AlarmFindSpecification;
import click.bitbank.api.domain.model.member.*;
import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.infrastructure.kafka.KafkaProducerService;
import click.bitbank.api.presentation.member.request.*;
import click.bitbank.api.presentation.shared.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApplicationServiceImpl implements MemberApplicationService {

    private final MemberSaveSpecification memberSaveSpecification;
    private final MemberLoginSpecification memberLoginSpecification;
    private final MemberFindSpecification memberFindSpecification;
    private final MemberDeleteSpecification memberDeleteSpecification;
    private final MemberUpdateSpecification memberUpdateSpecification;
    private final AlarmFindSpecification alarmFindSpecification;
    private final KafkaProducerService kafkaProducerService;

    /**
     * 회원 계정 생성
     * @param serverRequest : MemberSignupRequest
     * @return Mono<MemberRegistrationResponse> : 저장된 회원 정보
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberSignupResponse> signup(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberSignupRequest.class).flatMap(
            request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());

                request.verify(); // Request 유효성 검사

                return memberSaveSpecification.memberExistCheckAndRegistration(request); // 회원 계정 생성
            }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 소셜 회원 로그인
     * @param serverRequest : SocialLoginRequest
     * @return Mono<MemberLoginResponse> : 저장된 회원 정보
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberLoginResponse> socialLogin(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(SocialLoginRequest.class).flatMap(
            request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());
                request.verify(); // Request 유효성 검사
                return memberLoginSpecification.memberExistCheckAndSocialLogin(request);
            }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 일반 회원 로그인
     * @param serverRequest : MemberLoginRequest
     * @return Mono<MemberLoginResponse> : 저장된 회원 정보
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberLoginResponse> login(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberLoginRequest.class).flatMap(
                request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());
                    request.verify(); // Request 유효성 검사
                    return memberLoginSpecification.memberExistCheckAndLogin(request);
                }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 로그아웃
     * @param serverRequest : MemberIdRequest
     * @return Mono<SuccessResponse> : 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<SuccessResponse> logout(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberIdRequest.class)
            .flatMap(request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());
                request.verify(); // Request 유효성 검사

                return memberLoginSpecification.memberExistCheckAndLogout(request.getMemberId());
            }
        ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 회원 정보 수정
     * @param serverRequest : MemberModificationRequest
     * @return Mono<SuccessResponse> : 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<SuccessResponse> modification(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(MemberModificationRequest.class)
            .flatMap(request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());
                    request.verify(); // Request 유효성 검사

                    return memberUpdateSpecification.memberExistCheckAndModification(request);
                }
            ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 회원 탈퇴
     * @param serverRequest : MemberIdRequest
     * @return Mono<SuccessResponse> : 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<SuccessResponse> delete(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MemberIdRequest.class)
            .flatMap(request -> {
                kafkaProducerService.sendRequestTopic(serverRequest, request.toString());
                    request.verify(); // Request 유효성 검사

                    return memberDeleteSpecification.memberExistCheckAndDelete(request.getMemberId());
                }
            ).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())))
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 회원 고유번호 추출
     * @param serverRequest : Params
     * @return int : 회원 고유번호
     */
    private int getMemberIdByRequest(ServerRequest serverRequest) {

        // 회원 고유번호 추출
        return Integer.parseInt(serverRequest.queryParam("memberId")
            .orElseThrow(() -> new BadRequestException(ExceptionMessage.IsRequiredMemberId.getMessage())));
    }

    /**
     * 읽지 않은 알림 갯수 조회
     * @param serverRequest : Params
     * @return Mono<AlarmCountResponse> : 읽지 않은 알림 갯수
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Mono<AlarmCountResponse> findAlarmCount(ServerRequest serverRequest) {

        // 회원 고유번호 추출
        int memberId = this.getMemberIdByRequest(serverRequest);
        kafkaProducerService.sendRequestTopic(serverRequest, serverRequest.queryParams().toSingleValueMap().toString()); // Kafka Request Topic 발행

        return memberFindSpecification.membmerExistVerify(memberId) // 회원 정보 검증
            .flatMap(member ->
                alarmFindSpecification.unreadAlarmCountByMember(member.getMemberId())
            )
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 읽지 않은 알림 목록 조회
     * @param serverRequest : Params
     * @return Mono<AlarmListResponse> : 읽지 않은 알림 목록
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<AlarmListResponse> findAlarmList(ServerRequest serverRequest) {
        kafkaProducerService.sendRequestTopic(serverRequest, serverRequest.queryParams().toSingleValueMap().toString());
        // 회원 고유번호 추출
        int memberId = this.getMemberIdByRequest(serverRequest);

        return memberFindSpecification.membmerExistVerify(memberId) // 회원 정보 검증
            .flatMap(member ->
                alarmFindSpecification.unreadAlarmListByMember(member.getMemberId())
            )
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

    /**
     * 회원 검증
     * @param serverRequest : Params
     * @return Mono<SuccessResponse> : 검증 성공 반환
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Mono<SuccessResponse> existVerify(ServerRequest serverRequest) {
        kafkaProducerService.sendRequestTopic(serverRequest, serverRequest.queryParams().toSingleValueMap().toString());
        // 회원 고유번호 추출
        int memberId = this.getMemberIdByRequest(serverRequest);

        return memberFindSpecification.membmerExistVerify(memberId) // 검증 처리
            .flatMap(member -> Mono.just(new SuccessResponse())) // 검증 성공 반환
            .doOnSuccess(response -> kafkaProducerService.sendResponseTopic(serverRequest, response.toString()));
    }

}
