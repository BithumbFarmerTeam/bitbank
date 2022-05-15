package click.bitbank.api.application.accountBook;

import click.bitbank.api.domain.accountBook.MemberSpecification;
import click.bitbank.api.domain.accountBook.model.Income;
import click.bitbank.api.domain.service.AccountBookSearchService;
import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.presentation.accountBook.request.AccountBookSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBookApplicationServiceImpl implements AccountBookApplicationService {

    private final MemberSpecification memberSpecification;

    private final AccountBookSearchService accountBookSearchService;

    /**
     * 가계부 목록 검색
     *
     * @param serverRequest : 전달된 Request
     * @return Mono<AccountBookSearchResponse> : 저장된 가계부 정보
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Mono<List<Income>> accountBookSearch(ServerRequest serverRequest) {
        log.info("-----------------  여긴 서비스 시작");
        return serverRequest.bodyToMono(AccountBookSearchRequest.class).flatMap(
                request -> {
                    request.verify();
                    log.info(String.valueOf(request));
                    return memberSpecification.memberExistenceCheck(request.getMemberId())
                            .flatMap(m -> {

                                return accountBookSearchService.makeAccountBookSearchByDail(request).log();
                            }).log();
                }).switchIfEmpty(Mono.error(new BadRequestException(ExceptionMessage.IsRequiredRequest.getMessage())));
    }

}