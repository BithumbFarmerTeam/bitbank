package click.bitbank.api.domain.dao.factory;

import click.bitbank.api.domain.model.member.Member;
import click.bitbank.api.domain.model.member.MemberType;

import java.util.List;

public interface MemberFactory {

    Member memberBuilder(String memberLoginId, String memberName, String memberPassword); // 회원 정보 세팅
    List<Member> adminSetUpListBuilder(); // 초기 사이트 운영자 데이터 세팅
}
