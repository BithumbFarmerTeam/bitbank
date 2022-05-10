package click.bitbank.api.domain.dao.factory;

import click.bitbank.api.domain.dao.factory.MemberFactory;
import click.bitbank.api.domain.model.member.Member;
import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import click.bitbank.api.domain.model.member.MemberType;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.application.member.MemberSha256;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberFactoryImpl implements MemberFactory {

    /**
     * 회원 정보 세팅
     * @param memberLoginId : 아이디
     * @param memberName : 이름
     * @param memberPassword : 비밀번호
     * @return Member : 회원 정보
     */
    @Override
    public Member memberBuilder(String memberLoginId, String memberName, String memberPassword) {
        if (StringUtils.isBlank(memberLoginId)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberLoginId.getMessage());
        if (StringUtils.isBlank(memberName)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberName.getMessage());
        if (StringUtils.isBlank(memberPassword)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberPassword.getMessage());

        return Member.builder()
            .memberLoginId(memberLoginId)
            .memberName(memberName)
            .memberPassword(MemberSha256.encrypt(memberPassword))
            .build();
    }

    /**
     * 초기 사이트 운영자 데이터 세팅
     * @return List<Member> : 운영자 목록
     */
    @Override
    public List<Member> adminSetUpListBuilder() {

        String[] memberNameArray = new String[] { "강보경", "권설아", "정승훈", "홍준성" };
        return Arrays.stream(memberNameArray)
            .map(s -> this.memberBuilder("BitBankManager", s, "BitBankManager"))
            .collect(Collectors.toList());
    }
}
