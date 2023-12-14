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

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = null;
        String user_id = obtainUsername(request);
        String password = obtainPassword(request);
        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user_id, password));
        return authentication;
    }
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();
        String userId = user.getUsername();
        String accessToken = jwtTokenProvider.createJwtAccessToken(userId);
        String refreshToken = jwtTokenProvider.createJwtRefreshToken(userId);
		String deviceId = UUID.randomUUID().toString();
		tokenService.updateRefreshToken(userId, refreshToken, deviceId); // request.getHeader("browser")
        Map<String, Object> tokens = new HashMap<>();
		tokens.put("customer", new ObjectMapper().writeValueAsString(authService.getCustomerInCustomerService(user.getUsername())));
		tokens.put("diviceId", deviceId);
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
