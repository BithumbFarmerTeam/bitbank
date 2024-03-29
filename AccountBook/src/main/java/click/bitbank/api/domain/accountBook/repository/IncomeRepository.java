package click.bitbank.api.domain.accountBook.repository;

import click.bitbank.api.application.response.DTO.DailyTotalDTO;
import click.bitbank.api.application.response.DTO.DonutGraphDTO;
import click.bitbank.api.application.response.DTO.WeeklyTotalDTO;

import click.bitbank.api.domain.accountBook.model.income.Income;
import click.bitbank.api.domain.accountBook.model.income.IncomeType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IncomeRepository extends ReactiveCrudRepository<Income, Integer> {


    Flux<Income> findByMemberId(int memberId);  // 사용자의 모든 데이터 조회
    Mono<Income> findByIncomeId(int accountBookId);
    Flux<Income> findByMemberIdAndIncomeInfoContaining(int memberId, String searchKeyword); // 검색어를 포함하는 사용자의 데이터 조회

    @Query("SELECT * FROM income WHERE memberId = :memberId AND DATE(incomeDate) BETWEEN :startDate AND :endDate")
    Flux<Income> findByMemberIdAndIncomeDateBetween(int memberId, String startDate, String endDate);    // 검색 기간에 해당하는 사용자의 데이터 조회

    @Query("SELECT * FROM income WHERE memberId = :memberId AND incomeInfo LIKE :searchKeyword AND DATE(incomeDate) BETWEEN :startDate AND :endDate")
    Flux<Income> findByMemberIdAndIncomeDateBetweenAndIncomeInfoContaining(int memberId, String startDate, String endDate, String searchKeyword);   // 검색어를 포함하고 검색 기간에 해당하는 사용자의 데이터 조회

    Flux<Income> findByMemberIdAndIncomeTypeIn(int memberId, List<IncomeType> incomeType);  // 수입 유형과 일치하는 사용자의 데이터 조회

    Flux<Income> findByMemberIdAndIncomeTypeInAndIncomeInfoContaining(int memberId, List<IncomeType> incomeType, String searchKeyword); // 수입 유형과 일치하고 검색어를 포함하는 사용자의 데이터 조회

    @Query("SELECT * FROM income WHERE memberId = :memberId AND (DATE(incomeDate) BETWEEN :startDate AND :endDate) AND (incomeType IN (:incomeType))")
    Flux<Income> findByMemberIdAndIncomeDateBetweenAndIncomeTypeIn(int memberId, List<IncomeType> incomeType, String startDate, String endDate);    // 검색 기간, 수입 유형에 해당하는 사용자의 데이터 조회

    @Query("SELECT * FROM income WHERE memberId = :memberId AND incomeType IN (:incomeType) AND incomeInfo LIKE :searchKeyword AND (DATE(incomeDate) BETWEEN :startDate AND :endDate)")
    Flux<Income> findByMemberIdAndIncomeDateBetweenAndIncomeInfoContainingAndIncomeTypeIn(int memberId, List<IncomeType> incomeType, String startDate, String endDate, String searchKeyword);   // 수입 유형과 검색 기간에 해당하고 검색어를 포함하는 사용자의 데이터 조회
    
    // 특정 월
    @Query(
        "SELECT SUM(incomeMoney) " +
            "FROM income " +
            "WHERE " +
            "    memberId = :memberId " +
            "    AND DATE_FORMAT(incomeDate, '%Y-%c') = CONCAT(DATE_FORMAT(NOW(), '%Y-'), :month)"
    )
    Mono<Long> monthlyTotal(@Param("memberId") int memberId, @Param("month") int month);
    
    // 주차 별
    @Query(
        "SELECT " +
            "    WEEK(incomeDate, 2) AS `week`," +
            "    SUM(incomeMoney) AS `total` " +
            "FROM income " +
            "WHERE " +
            "    memberId = :memberId " +
            "    AND DATE_FORMAT(incomeDate, '%Y-%c') = CONCAT(DATE_FORMAT(NOW(), '%Y-'), :month)" +
            "GROUP BY `week`" +
            "ORDER BY `week`"
    )
    Flux<WeeklyTotalDTO> weeklyTotal(@Param("memberId") int memberId, @Param("month") int month);
    
    // 카테고리 별
    @Query(
        "SELECT " +
            "    ROW_NUMBER() OVER(ORDER BY SUM(incomeMoney) DESC) AS `id`," +
            "    incomeType AS `name`," +
            "    SUM(incomeMoney) AS `quantity`, " +
            "    ROUND((SUM(incomeMoney) * 100) / SUM(SUM(incomeMoney)) OVER(), 0)  AS `percentage` " +
            "FROM income " +
            "WHERE " +
            "    memberId = :memberId " +
            "    AND DATE_FORMAT(incomeDate, '%Y-%c') = CONCAT(DATE_FORMAT(NOW(), '%Y-'), :month)" +
            "GROUP BY `name`" +
            "ORDER BY `quantity` DESC"
    )
    Flux<DonutGraphDTO> categoryTotal(@Param("memberId") int memberId, @Param("month") int month);
    
    // 일자 별
    @Query(
        "SELECT " +
            "    DATE_FORMAT(incomeDate, '%m.%d') AS `day`," +
            "    SUM(incomeMoney) AS `total` " +
            "FROM income " +
            "WHERE " +
            "    memberId = :memberId " +
            "    AND DATE_FORMAT(incomeDate, '%Y-%c') = CONCAT(DATE_FORMAT(NOW(), '%Y-'), :month)" +
            "GROUP BY `day`" +
            "ORDER BY `day`"
    )
    Flux<DailyTotalDTO> dailyTotal(@Param("memberId") int memberId, @Param("month") int month);
}
