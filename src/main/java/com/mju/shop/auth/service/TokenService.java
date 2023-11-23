package com.mju.shop.auth.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mju.shop.auth.model.RefreshToken;
import com.mju.shop.auth.provider.JwtTokenProvider;
import com.mju.shop.auth.repository.AuthTokenDAO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TokenService {
//    private CustomerRepository customerRepository;
//    private RefreshTokenRedisRepository refreshTokenRedisRepository;
	
	@Autowired
	AuthTokenDAO customerTokenDAO;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void updateRefreshToken(String userId, String refreshToken, String deviceId) {
    	RefreshToken customerToken = new RefreshToken(userId, refreshToken, deviceId);
    	customerTokenDAO.save(customerToken);
    }

    // 만료기한 검증도 필요
	public ResponseEntity<String> validateAccessToken(String token) {
		if(jwtTokenProvider.validateToken(token)) {
			return new ResponseEntity<>(jwtTokenProvider.getUserId(token), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Faild validation Access Token", HttpStatus.FORBIDDEN); // 거부 (인증이 안 되니까)
		}
	}
	
	public ResponseEntity<String> validateRefreshToken(String token, String browser) {
		if(this.compareRefreshToken(jwtTokenProvider.getUserId(token), jwtTokenProvider.getRefreshTokenValue(token), browser)) {
			System.out.println("success validation refreshToken");
			HashMap<String, String> response = new HashMap<>();
			String accessToken = jwtTokenProvider.createJwtAccessToken(jwtTokenProvider.getRefreshTokenUserId(token));
			response.put("access-token", accessToken);
			response.put("userId", jwtTokenProvider.getUserId(accessToken));
			ObjectMapper objectMapper = new ObjectMapper();
			String json = "";

			try {
				json = objectMapper.writeValueAsString(response);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>(json, HttpStatus.OK);
		} else {
			System.out.println("failed validation refreshToken");
			return new ResponseEntity<>("Faild validation Refresh Token", HttpStatus.FORBIDDEN);
		}
	}
	
	private boolean compareRefreshToken(String userId, String token, String browser) {
		System.out.println("RefreshTokenService===============");
		System.out.println("userId : " + userId + "/ browser : " + browser);
		RefreshToken refreshToken = customerTokenDAO.findByUserId(userId);
		if(refreshToken == null) return false;
		System.out.println("token : " + token);
		System.out.println("refreshToken : " + jwtTokenProvider.getRefreshTokenValue(refreshToken.getToken()));
		
		if(token.equals(jwtTokenProvider.getRefreshTokenValue(refreshToken.getToken()))) {
			return true;
		}
		return false;
	}
//	
//	private boolean compareRefreshToken(String userId, String token_value, String browser) {
//		System.out.println("RefreshTokenService===============");
//		System.out.println("userId : " + userId + "/ browser : " + browser);
//		RefreshToken refreshToken = customerTokenDAO.findByUserIdANDBrowser(userId, browser);
//		
//		if(refreshToken == null) return false;
//		
//		if(token_value.equals(refreshToken.getToken())) {
//			return true;
//		}
//		return false;
//	}
	
	@Transactional(readOnly = false)
	public void initializationRefreshToken(String token) {
		this.customerTokenDAO.deleteByUserId(jwtTokenProvider.getUserId(token));
	}

	@Transactional(readOnly = false)
	public void logout(String token) {
		this.customerTokenDAO.deleteByRefreshToken(token);
	}
}