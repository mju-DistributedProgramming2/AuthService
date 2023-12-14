package com.omnm.auth.Handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.omnm.auth.provider.JwtTokenProvider;
import com.omnm.auth.service.TokenService;

@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {
	
	private TokenService tokenService;
	private JwtTokenProvider jwtTokenProvider;
	
	public CustomLogoutHandler(TokenService tokenService, JwtTokenProvider jwtTokenProvider) {
		this.tokenService = tokenService;
		this.jwtTokenProvider = jwtTokenProvider;
	}
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		this.tokenService.logout(request.getHeader("refresh-token"));
	}

}
