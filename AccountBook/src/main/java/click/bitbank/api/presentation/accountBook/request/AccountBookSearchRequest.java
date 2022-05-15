package click.bitbank.api.presentation.accountBook.request;

import click.bitbank.api.domain.accountBook.ExpenditureType;
import click.bitbank.api.domain.accountBook.IncomeType;
import click.bitbank.api.domain.accountBook.SearchDateType;
import click.bitbank.api.domain.accountBook.TransferType;
import click.bitbank.api.infrastructure.exception.status.BadRequestException;
import click.bitbank.api.infrastructure.exception.status.ExceptionMessage;
import click.bitbank.api.presentation.shared.request.RequestVerify;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountBookSearchRequest implements RequestVerify {

    Integer memberId;

    String searchKeyword;   // 검색어

    SearchDateType searchDateType;  // 기간

    String searchStartDate; // 검색 시작일

    String searchEndDate;   // 검색 종료일

    List<ExpenditureType> expenditureType;    // 지출 유형

    List<IncomeType> incomeType;  // 수입 유형

    List<TransferType> transferType;  // 이체 유형

    public String getSearchKeywordAddPercent() {
        return '%' + searchKeyword + '%';
    }

    public void setSearchStartDate(String searchStartDate) {
        this.searchStartDate = searchStartDate;
    }

    public void setSearchEndDate(String searchEndDate) {
        this.searchEndDate = searchEndDate;
    }

    @Override
    public void verify() {
        if (memberId == null) throw new BadRequestException(ExceptionMessage.IsRequiredMemberId.getMessage());
        if (searchDateType == null && !searchStartDate.isBlank() && !searchEndDate.isBlank()) throw new BadRequestException(ExceptionMessage.IsRequiredSearchDateType.getMessage());
    }
}