package click.bitbank.api.domain.accountBook.DAO;

import click.bitbank.api.domain.accountBook.model.AccountBook;
import click.bitbank.api.domain.accountBook.model.AccountBookType;
import click.bitbank.api.domain.accountBook.model.expenditure.Expenditure;
import click.bitbank.api.domain.accountBook.model.expenditure.ExpenditureType;
import click.bitbank.api.domain.accountBook.model.income.Income;
import click.bitbank.api.domain.accountBook.model.income.IncomeType;
import click.bitbank.api.domain.accountBook.model.transfer.Transfer;
import click.bitbank.api.domain.accountBook.model.transfer.TransferType;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class AccountBookFactoryImpl implements AccountBookFactory {

    @Override
    public AccountBook builder(AccountBookType accountBookType, LocalDateTime accountBookDate, String accountBookInfo, BigInteger accountBookMoney) {
        return AccountBook.builder()
            .accountBookDate(accountBookDate)
            .accountBookInfo(accountBookInfo)
            .accountBookMoney(accountBookMoney)
            .build();
    }

    @Override
    public Income incomeBuilder(String incomeInfo, LocalDateTime incomeDate, IncomeType incomeType, BigInteger incomeMoney, int memberId) {
        return Income.builder()
            .incomeInfo(incomeInfo)
            .incomeDate(incomeDate)
            .incomeType(incomeType)
            .incomeMoney(incomeMoney)
            .memberId(memberId)
            .build();
    }

    @Override
    public Expenditure expenditureBuilder(String expenditureInfo, LocalDateTime expenditureDate, ExpenditureType expenditureType, BigInteger expenditureMoney, int memberId) {
        return Expenditure.builder()
            .expenditureInfo(expenditureInfo)
            .expenditureDate(expenditureDate)
            .expenditureType(expenditureType)
            .expenditureMoney(expenditureMoney)
            .memberId(memberId)
            .build();
    }

    @Override
    public Transfer transferBuilder(String transferInfo, LocalDateTime transferDate,TransferType transferType, BigInteger transferMoney, int memberId) {
        return Transfer.builder()
            .transferInfo(transferInfo)
            .transferDate(transferDate)
            .transferType(transferType)
            .transferMoney(transferMoney)
            .memberId(memberId)
            .build();
    }
}
