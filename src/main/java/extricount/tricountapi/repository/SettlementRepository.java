package extricount.tricountapi.repository;


import extricount.tricountapi.model.ExpenseResult;
import extricount.tricountapi.model.Member;
import extricount.tricountapi.model.Settlement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class SettlementRepository {
    private final JdbcTemplate jdbcTemplate;

    public Settlement create(String name) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("settlement").usingGeneratedKeyColumns("id");

        Map<String, Object> parmas = new HashMap<>();
        parmas.put("name", name);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parmas));

        Settlement settlement = new Settlement();
        settlement.setId(key.longValue());
        settlement.setName(name);

        return settlement;
    }

    public void addParticipantToSettlement(Long settlementId, Long memberId) {
                jdbcTemplate.update("INSERT INTO settlement_participant (settlement_id, member_id) VALUES (?, ?)",
                        settlementId, memberId);
        }





    public Optional<Settlement> findById(Long id) {
        String sql = "SELECT * FROM settlement "
                + "JOIN settlement_participant ON settlement.id = settlement_participant.settlement_id "
                + "JOIN member ON settlement_participant.member_id = member.id "
                + "WHERE settlement.id = ?";
        List<Settlement> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Settlement settlement = new Settlement();
            settlement.setId(rs.getLong("settlement.id"));
            settlement.setName(rs.getString("settlement.name"));

            List<Member> participants = new ArrayList<>();
            Member participant = new Member(
                    rs.getLong("member.id"),
                    rs.getString("member.login_id"),
                    rs.getString("member.name"),
                    rs.getString("member.password")
            );
            participants.add(participant);

            settlement.setParticipants(participants);
            return settlement;
        }, id);
        return result.stream().findAny();
    }

    public List<Settlement> findAll() {
        String sql = "SELECT * FROM settlement";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Settlement settlement = new Settlement();
            settlement.setId(rs.getLong("id"));
            settlement.setName(rs.getString("name"));

            // Fetch participants for the current settlement
            Long settlementId = rs.getLong("id");
            List<Member> participants = getParticipantsForSettlement(settlementId);
            settlement.setParticipants(participants);

            return settlement;
        });
    }
    public void deleteById(Long settlementId) {
        jdbcTemplate.update("DELETE FROM settlement WHERE id = ?", settlementId);
    }

    private List<Member> getParticipantsForSettlement(Long settlementId) {
        String sql = "SELECT member.* FROM member " +
                "JOIN settlement_participant ON member.id = settlement_participant.member_id " +
                "WHERE settlement_participant.settlement_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Member participant = new Member();
            participant.setId(rs.getLong("id"));
            participant.setLoginId(rs.getString("login_id"));
            participant.setName(rs.getString("name"));
            return participant;
        }, settlementId);
    }





    public List<ExpenseResult> findExpensesWithMemberBySettlementId(Long settlementId) {
        String sql = "SELECT * " +
                "FROM settlement_participant " +
                "JOIN member ON settlement_participant.member_id = member.id " +
                "LEFT JOIN expense ON settlement_participant.member_id = expense.payer_member_id " +
                "AND settlement_participant.settlement_id = expense.settlement_id " +
                "WHERE settlement_participant.settlement_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ExpenseResult expenseResult = new ExpenseResult();
            expenseResult.setSettlementId(rs.getLong("settlement_participant.settlement_id"));
            BigDecimal amt = rs.getBigDecimal("expense.amount");
            expenseResult.setAmount(amt != null ? amt : BigDecimal.ZERO);

            Member member = new Member();
            if (rs.getLong("member.id") != 0) {
                member.setId(rs.getLong("member.id"));
                member.setLoginId(rs.getString("member.login_id"));
                member.setPassword(rs.getString("member.password"));
                member.setName(rs.getString("member.name"));

                expenseResult.setPayerMember(member);
            }

            return expenseResult;
        }, settlementId);
    }

    public boolean existsParticipant(Long settlementId, Long memberId) {
        String sql = "SELECT COUNT(*) FROM settlement_participant WHERE settlement_id = ? AND member_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, settlementId, memberId);
        return count != null && count > 0;
    }

}