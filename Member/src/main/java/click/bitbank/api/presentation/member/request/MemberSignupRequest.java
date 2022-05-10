package click.bitbank.api.presentation.member.request;

import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.presentation.shared.request.RequestVerify;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupRequest implements RequestVerify {

    private String memberLoginId; // 회원 아이디
    private String memberName; // 회원 이름
    private String memberPassword; // 회원 비밀번호

    @Override
    public void verify() {
        if (StringUtils.isBlank(memberLoginId)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberLoginId.getMessage());
        if (StringUtils.isBlank(memberName)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberName.getMessage());
        if (StringUtils.isBlank(memberPassword)) throw new BadRequestException(ExceptionMessage.IsRequiredMemberPassword.getMessage());
    }
}
