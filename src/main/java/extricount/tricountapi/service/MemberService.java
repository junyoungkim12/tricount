package extricount.tricountapi.service;

import extricount.tricountapi.model.Member;
import extricount.tricountapi.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signup(Member member){
        log.info("회원가입 성공");

        return memberRepository.save(member);

    }

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId, password)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호를 잘못 입력했습니다"));
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호를 잘못 입력했습니다"));
    }
}
