package click.bitbank.api.domain.repository;

import click.bitbank.api.domain.model.member.Member;
import click.bitbank.api.domain.model.member.MemberType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, Integer> {

    Mono<Member> findByMemberLoginIdAndMemberName(String memberLoginId, String memberName);
    Mono<Member> findByMemberNameAndMemberPassword(String memberName, String memberPassword);



}
