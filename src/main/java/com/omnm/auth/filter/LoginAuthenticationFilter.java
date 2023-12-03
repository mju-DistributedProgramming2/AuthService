package com.omnm.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.omnm.auth.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnm.auth.provider.JwtTokenProvider;
import com.omnm.auth.service.TokenService;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private TokenService tokenService;
    private AuthService authService;
    
    public LoginAuthenticationFilter(AuthenticationManager authenticationManager,
    		JwtTokenProvider jwtTokenProvider,
    		TokenService tokenService,
    		AuthService authService) {
    	this.authenticationManager = authenticationManager;
    	this.jwtTokenProvider = jwtTokenProvider;
    	this.tokenService = tokenService;
    	this.authService = authService;
    }
    

    // login 리퀘스트 패스로 오는 요청을 판단
    // json(form-data)로 받으려면 커스텀으로 하나 만들어서 objectmapper.readvalue로 해야하는데 이렇게 안됨
    	// -> x-www-form-urlencoded로 하면 넘어오긴 함
    	// --> 프론트에서 전송할 때 x-www-form-urlencoded로 전송할 것!!!
        // key : username / password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = null;
     
        System.out.println(request.getHeader("user-agent"));


            String user_id = obtainUsername(request);
            String password = obtainPassword(request);
            System.out.println(user_id + " !!@@ " + password);

            // UsernamePasswordAuthenticationToken을 통해
            // loadUserByUsername 메소드에서 로그인 판별
            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(credential.getUsername(), credential.getPassword())
                    new UsernamePasswordAuthenticationToken(user_id, password)
            );
            System.out.println("new authenticion success!");


        return authentication;
//        return null;
    }

    // 로그인 성공 이후 토큰 생성
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        System.out.println("로그인 성공!");
    	
        User user = (User) authResult.getPrincipal();
        
//        List<String> roles = user.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());

        String userId = user.getUsername();
        
        				
		// response body에 넣어줄 access token 및 expired time 생성
        String accessToken = jwtTokenProvider.createJwtAccessToken(userId);
		// 쿠키에 넣어줄 refresh token 생성
        String refreshToken = jwtTokenProvider.createJwtRefreshToken(userId);
//        Date expiredTime_refreshToken = jwtTokenProvider.getExpiredTime(refreshToken);
				
        System.out.println("토큰 생성 완료! (filter)");
		System.out.println("새로 발행된 refresh token 값 갱신  userId : " + userId + "  jwtTokenProvider.getRefreshTokenId(refreshToken) : " +  jwtTokenProvider.getRefreshTokenValue(refreshToken));
//        String userId = "";
//		tokenService.updateRefreshToken(userId, jwtTokenProvider.getRefreshTokenValue(refreshToken)); // request.getHeader("browser")
		String deviceId = UUID.randomUUID().toString();
		tokenService.updateRefreshToken(userId, refreshToken, deviceId); // request.getHeader("browser")
        
        // body 설정
		Map<String, Object> tokens = new HashMap<>(); 
		tokens.put("customer", new ObjectMapper().writeValueAsString(authService.getCustomerInCustomerService(user.getUsername())));
		tokens.put("diviceId", deviceId);
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
        
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
    
}
