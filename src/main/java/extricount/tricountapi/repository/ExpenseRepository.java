package extricount.tricountapi.repository;

import extricount.tricountapi.model.Expense;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ExpenseRepository {


    private final JdbcTemplate jdbcTemplate;

    public Expense save(Expense expense) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("expense").usingGeneratedKeyColumns("id");

        Map<String, Object> parmas = new HashMap<>();
        parmas.put("name", expense.getName());
        parmas.put("settlement_id", expense.getSettlementId());
        parmas.put("payer_member_id", expense.getPayerMemberId());
        parmas.put("amount", expense.getAmount());
        parmas.put("expense_date_time", expense.getExpenseDateTime());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parmas));
        expense.setId(key.longValue());

        return expense;
    }

    public List<Expense> findAllBySettlementId(Long settlementId) {
        String sql = "SELECT * FROM expense WHERE settlement_id = ?";
        return jdbcTemplate.query(sql, new Object[]{settlementId}, (rs, rowNum) -> {
            Expense expense = new Expense();
            expense.setId(rs.getLong("id"));
            expense.setName(rs.getString("name"));
            expense.setSettlementId(rs.getLong("settlement_id"));
            expense.setPayerMemberId(rs.getLong("payer_member_id"));
            expense.setAmount(rs.getBigDecimal("amount"));
            expense.setExpenseDateTime(rs.getObject("expense_date_time", LocalDateTime.class));
            return expense;
        });
    }
    public void deleteById(Long expenseId) {
        jdbcTemplate.update("DELETE FROM expense WHERE id = ?", expenseId);
    }
}
