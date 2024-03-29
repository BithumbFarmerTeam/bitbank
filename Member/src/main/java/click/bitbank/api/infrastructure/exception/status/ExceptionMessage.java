package click.bitbank.api.infrastructure.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {

    IsRequiredRequest("BadRequestException", "Request를 전달해주세요."),
    IsRequiredMemberId("BadRequestException", "전달된 회원 고유번호가 없습니다."),
    IsRequiredSocialToken("BadRequestException", "전달된 소셜 정보가 없습니다."),
    IsRequiredMemberLoginId("BadRequestException", "아이디를 입력해주세요"),
    IsRequiredMemberName("BadRequestException", "이름을 입력해 주세요."),
    IsRequiredMemberPassword("BadRequestException", "비밀번호를 입력해 주세요."),
    IsRequiredMemberType("BadRequestException", "회원 유형을 선택해 주세요."),

    NotFoundLoginMember("UnauthorizedException", "회원정보를 확인해주세요."),

    AlreadyDataMember("AlreadyDataException", "이미 존재하는 회원입니다."),

    SaveFailMember("RegistrationFailException", "회원 가입에 실패했습니다. 관리자에게 문의 바랍니다."),
    
    NotFoundMember("NotFoundDataException", "조회된 회원 정보가 없습니다."),

    IsRequiredAlphaNumericForMemberLoginId("BadRequestException", "아이디를 문자 혹은 숫자로 입력하세요"),
    IsRequiredAlphaForMemberName("BadRequestException", "이름을 문자로 입력하세요"),
    IsRequiredStringLengthForMemberLoginId("BadRequestException", "8자 이상의 아이디를 입력하세요"),
    IsRequiredCharNumberSpecialCharactersForMemberPassword("BadRequestException", "영문/숫자/특수문자 포함 6자 이상의 비밀번호를 입력하세요");

    private final String type;
    private final String message;
}
