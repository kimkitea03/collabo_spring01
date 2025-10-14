package com.coffee.handler;

import com.coffee.entity.Member;
import com.coffee.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
AuthenticationSuccessHandler 인터페이스
    스프링 시큐리티에서 로그인에 성공을 했을 때 실행하고자 하는 동작을
    개발자가 직접 정의할 수 있도록 해주는 인터페이스
    우리는 로그인 성공시 클라이언트에 JSON 형식으로 회원 정보를 반환하도록 하겠습니다.
 */

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private MemberService memberService ;

    @Autowired // setter 메소드를 이용한 객체 주입
    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override // 이 메소드는 로그인 성공시 자동 실행이 됩니다.(콜백 메소드)
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // authentication는 인증객체라고 하며, 로그인 성공시의 정보가 포함되어 있습니다.

        //클리이언트에 대한 응답을 JSON 타입으로 지정(ut-8 타입 인코딩 포함)
        response.setContentType("application/json;charset=UTF-8");

        User user = (User) authentication.getPrincipal();
        String email = user.getUsername(); // 우리가 사용한 username은 사실 email입니다.
        Member member = memberService.findByEmail(email);

        if (member == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"사용자 정보를 찾을 수 없습니다.\"}");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("message", "success");
        data.put("member", member);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            response.getWriter().write(mapper.writeValueAsString(data));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"JSON 변환 실패\"}");
        }
    }
}
