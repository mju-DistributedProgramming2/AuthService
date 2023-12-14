package com.omnm.auth.controller;

import java.util.HashMap;

import com.omnm.auth.service.AuthService;
import com.omnm.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController // @CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	AuthService authService;
	
	@Autowired
	TokenService tokenService;

	@PostMapping("/validation/token/access")
	public ResponseEntity<String> validateAccessToken(@RequestBody HashMap<String, String> param) {
		return tokenService.validateAccessToken(param.get("token"));
	}
	
	@PostMapping("/validation/token/refresh")
	public ResponseEntity<String> validateRefreshToken(@RequestBody HashMap<String, String> param) {
		return tokenService.validateRefreshToken(param.get("token"), "user-agent"); // param.get("browser")
	}
	@PostMapping("/initialization/token")
	public ResponseEntity<String> initializationRefreshToken(String token) {
		tokenService.initializationRefreshToken(token);
		return null;
	}
}
