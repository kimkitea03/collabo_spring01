package com.coffee.config;

import com.coffee.handler.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // 이 파일은 설정용 파일로 사용되며, 여기에 명시된 내용들을 객체 형태로 만들어 줍니다.
@EnableWebSecurity // 웹 페이지 관련된 시큐리티 설정을 활성화 해주는 어노테이션 입니다.
public class SecurityConfig {
    // 클래스 객체를 http에서는 자바 빈이라고 하고 스프링에서는 빈이라고 함
    @Bean // SecurityFilterChain는 http 요청이 들어올 때 보안과 관련된 필터들이 해야할 일을 명시해주는 필터 체인입니다.
    //예) 로그인/로그아웃/cors 활성화, csrf 설정 등등
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //명시된 항목들은 로그인이 필요 없이 무조건 허용합니다.
        String[] permitAllowed = {"/","/member/signup","/member/login","/product"
                ,"/product/list","/cart/**","/order/**","/fruit/**","/element/**","/images/"};

        //반드시 로그인이 필요한 목록들 입니다.
        String[] neededAuthenticated = {"/product/detail/"};

        //HttpSeurity : 개발자가 코드를 직접 작성하여 보안 정책을 설정할 수 있도록 도와주는 객체입니다.
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(Customizer.withDefaults()) // CORS 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAllowed).permitAll()// 이 경로는 무조건 접속 허용
                        .requestMatchers(neededAuthenticated).authenticated()// 그 외 경로는 인증(로그인)이 필요
                        .anyRequest().authenticated()
                );

        //handler()는 이 문서의 하단에 정리되어 있습니다.
        http.formLogin(form -> form
                .loginProcessingUrl("/member/login") // React에서 로그인시 요청할 url
                .usernameParameter("email") // 로그인시 id역할을 할 컬럼명
                .passwordParameter("password") // 비밀 번호 컬럼명
                .permitAll() // 누구든지 접근 허용
                .successHandler(handler()) // 로그인 성공시 수행할 동작을 여기에 명시
        );

        // 참고 : .logoutSuccessHandler()도 있음.
        http.logout(logout->logout
                .logoutUrl("/member/logout") // 로그 아웃시 이동할 url
                .permitAll() // 누구든지 접근 허용
        );

        return http.build();
    }

    @Bean // CorsConfigurationSource는 다른 도메인(origin)에서 자원 요청시 브라우저가 허용 여부를 검사해주는 보안 정책입니다.
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 리액트 주소
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUt","PATCH","DELETE","OPTIONS")); // 허용할 메소드 목록
        // 클라이언트가 서버에 요청시 모든 요청 정보를 허용하겠습니다.
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);// 쿠키, 세션 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean // PasswordEncoder : 비밀 번호 암호화를 해주는 인터페이스입니다.
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean // 개발자가 정의한 로그인 성공 핸들러
    public CustomLoginSuccessHandler handler(){
        return new CustomLoginSuccessHandler();
    }
}
