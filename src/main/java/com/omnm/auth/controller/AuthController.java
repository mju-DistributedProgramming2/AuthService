package com.omnm.auth.controller;

import java.util.HashMap;

import com.omnm.auth.service.AuthService;
import com.omnm.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController // @CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	AuthService authService;
	
	@Autowired
	TokenService tokenService;

	

	
	
//	@GetMapping("/test/mailutils")
//	public int test_sendMail() {
//		MailUtils mailUtils = new MailUtils("sang_hyuk_kim@naver.com", "title", "content");
//		try {
//			mailUtils.sendMail();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return 1;
//	}
	
	@PostMapping("/validation/token/access")
	@ApiOperation("엑세스 토큰 검증")
	public ResponseEntity<String> validateAccessToken(@RequestBody HashMap<String, String> param) {
		System.out.println("검증할 access token : " + param.get("token"));

		return tokenService.validateAccessToken(param.get("token"));
	}
	
	@PostMapping("/validation/token/refresh")
	@ApiOperation("리프레시 토큰 검증")
//	public int validateToken(@CookieValue("refresh-token") String token) {
	public ResponseEntity<String> validateRefreshToken(@RequestBody HashMap<String, String> param) { // , String browser
		System.out.println("검증할 refresh token : " + param.get("token"));
		return tokenService.validateRefreshToken(param.get("token"), "user-agent"); // param.get("browser")
	}
	
	// 단순히 토큰만 지우는 게 아닌 관련된 모든 토큰(다중 로그인) 지워야함
	// -> 조작된 토큰이면 우리 디비에 없을텐데 그러면  
	@PostMapping("/initialization/token")
	@ApiOperation("토큰 탈취 의심 모든 토큰 삭제")
	public ResponseEntity<String> initializationRefreshToken(String token) {
//		String token = param.get("token");
		tokenService.initializationRefreshToken(token);
		
		return null;
	}
	
}
