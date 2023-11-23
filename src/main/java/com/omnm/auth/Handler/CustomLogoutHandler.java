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
		
		System.out.println("logoutHandler ----------------------------------");
		
		System.out.println(request.getHeader("refresh-token"));
		
//		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
//        StringBuilder builder = new StringBuilder();
//        String buffer;
//        while ((buffer = input.readLine()) != null) {
//            if (builder.length() > 0) {
//                builder.append("\n");
//            }
//            builder.append(buffer);
//        }
////        
//        System.out.println("inputstream : " + builder.toString());
//        
//        JSONParser parser = new JSONParser();
//        JSONObject json = null;
//		try {
//			json = (JSONObject)parser.parse(builder.toString());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("refreshToken : " + request.getParameter("refresh-token"));
//		for(Object string : request.getParameterMap().keySet().toArray()) {
//			System.out.println("parameter : " + string);
//		}
//		System.out.println(json.get("token"));
		this.tokenService.logout(request.getHeader("refresh-token"));
//		this.tokenService.logout(jwtTokenProvider.getRefreshTokenValue(json.get("token")+""));
		
	}

}
