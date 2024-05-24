package extricount.tricountapi.repository;

import extricount.tricountapi.model.Member;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("login_id", member.getLoginId());
        params.put("password", member.getPassword());
        params.put("name", member.getName());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
        member.setId(key.longValue());


        return member;
    }

    public Optional<Member> findByLoginId(String loginId, String password) {
        String sql = "SELECT * FROM member WHERE login_id = ? AND password = ?";
        List<Member> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Member(
                        rs.getLong("id"),
                        rs.getString("login_id"),
                        rs.getString("password"),
                        rs.getString("name")
                ),
                loginId, password);
        return result.stream().findFirst();
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        List<Member> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Member(
                        rs.getLong("id"),
                        rs.getString("login_id"),
                        rs.getString("password"),
                        rs.getString("name")
                ),
                id);
        return result.stream().findFirst();
    }
}