package com.lionproject24.fruitshop.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
@RequiredArgsConstructor
// Spring Security가 "인증 실패 했을 때 이 클래스를 호출해라"라고 정해둔 규칙
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    // commecve() 메서드 - 인증 실패 시 진입점
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException{

        // Filter가 미리 심어둔 "왜 실패했는지" 사유 코드를 꺼냄 (없으면 null)
        String exception = (String)request.getAttribute("exception");

        // 사유 코드가 없다면(=Filter)를 거치지 않은 다른 종류의 인증 실패라면), 최소한 로그는 남김
        if(exception==null) {
            log.error("Commence Occurred :: " + authenticationException.getMessage());
        }

        // 요청 종류에 따라 응답 방식을 다르게 처리
        if(isRestRequest(request)){
            // API(REST) 요청이면 -> Json 에러 응답
            handleRestResponse(exception,request,response);
        }else {
            // 화면(페이지) 요청이면 -> 로그인 페이지로 리다이렉트
            handlePageResponse(exception, request, response, authenticationException);
        }
    }
    private void handleRestResponse(String execption, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{

        log.error("Page Request = commence : {}",execption);

        // 문자열 코드("EXPIRED_TOKEN")를 enum 객체로 변환
        ErrorCode code = ErrorCode.findByCode(execption);

        if (code == ErrorCode.UNKNOWN_ERROR && execption == null){
            log.error("Rest request - :: 인증예외 발생!!");
        }

        // 응답 형식을 JSON으로 지정
        response.setContentType("application/json;charset=UTF-8");
        // HTTP 상태코드를 401(인증 안 됨)로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 코드와 메시지를 담을 Map 준비
        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("code",code.name());
        errorInfo.put("message",code.getMessage());

        // Map을 JSON 문자열로 변환
        String responseJson = objectMapper.writeValueAsString(errorInfo);
        // 최종적으로 그 JSON 문자열을 클라이언트에게 응답으로 씀
        response.getWriter().print(responseJson);

    }

    // 페이지 요청 일 때 - 로그인 페이지로 리다이렉트
    private void handlePageResponse(String execption, HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException)
            throws IOException, ServletException{

        if (execption == null){
            log.error("Page Request - commence : {}",execption);
        }else{
            log.error("Page Request - commence : {}", authenticationException.getMessage());
        }

        // 로그인 페이지 주소로 이동시킴
        response.sendRedirect("/loginform");
    }
    // 이 요청이 API(REST) 요청인지 판단하는 메서드 - 새로 추가!
    private boolean isRestRequest(HttpServletRequest request){
        // 자바 스크립트가 관례적으로 API 요청 시 붙이는 헤더 확인
        String header = request.getHeader("X-Requested-With");
        // 요청 주소 확인
        String uri = request.getRequestURI();
        // 주소가 "/api/"로 시작하거나 "/error"면 API 요청으로 간주
        boolean isApi = uri.startsWith("/api/") || uri.equals("/error");
        //  헤더 조건 또는 주소 조건, 둘 중 하나라도 맞으면 true
        return "XMLHttpRequest".equals(header) || isApi;
    }
}

