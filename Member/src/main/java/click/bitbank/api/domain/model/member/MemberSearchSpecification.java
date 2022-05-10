package click.bitbank.api.domain.model.member;

import click.bitbank.api.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import click.bitbank.api.application.response.MemberInfoResponse;

@Component
@RequiredArgsConstructor
public class MemberSearchSpecification {

    private final MemberRepository memberRepository;

    public Mono<MemberInfoResponse> getMemberInfo(Integer memberId) {

        return memberRepository.findById(memberId)
                .map(m -> new MemberInfoResponse( m.getMemberId(), m.getMemberType()));
    }
}
