package extricount.tricountapi.interceptor;

import extricount.tricountapi.MemberContext;
import extricount.tricountapi.TricountApiConst;
import extricount.tricountapi.exception.AuthenticationException;
import extricount.tricountapi.model.Member;
import extricount.tricountapi.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new AuthenticationException("로그인이 필요합니다.");
        }
        Map<String, Cookie> cookieMap = new HashMap<>();
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie);
        }

        Cookie loginCookie = cookieMap.get(TricountApiConst.LOGIN_MEMBER_COOKIE);
        if(loginCookie == null) {
            throw new AuthenticationException("로그인이 필요합니다.");
        }

        try {
            Member member = memberService.findMemberById(Long.valueOf(loginCookie.getValue()));
            MemberContext.setMember(member);
        } catch (Exception e) {
            throw new AuthenticationException("회원정보를 찾을 수 없습니다.");
        }

        return true;
    }
}
