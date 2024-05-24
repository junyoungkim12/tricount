package extricount.tricountapi.controller;

import extricount.tricountapi.TricountApiConst;
import extricount.tricountapi.request.LoginRequest;
import extricount.tricountapi.service.MemberService;
import extricount.tricountapi.model.Member;
import extricount.tricountapi.request.SignupRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Member> signup(@RequestBody SignupRequest signupRequest) {
        Member member = Member.builder()
                .loginId(signupRequest.getUserId())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName())
                .build();

        log.info("회원가입 성공");
        return new ResponseEntity<>(memberService.signup(member), HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login
            (@RequestBody LoginRequest loginRequest,
             HttpServletResponse response
            ){
        Member member = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        if (member != null) {
            Cookie cookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, String.valueOf(member.getId()));
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);


        return ResponseEntity.ok(member.getName() + "님 환영합니다.");

        } else {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
    }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}
