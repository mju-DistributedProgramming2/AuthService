package com.omnm.auth.provider;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
    
    private static final long ACCESS_EXPIRED_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_EXPIRED_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    private String SECRET = "아무거나해도되나아무거나진짜되나";

    public String createJwtAccessToken(String userId) {
    	
    	System.out.println("Access 토큰 생성 시작!(JwtTokenProvider)");

        Claims claims = Jwts.claims().setSubject("access_token");
        claims.put("user_id", userId);
//        claims.put("uuid", UUID.randomUUID().toString());

        
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + ACCESS_EXPIRED_TIME)
                )
//                .setIssuedAt(date)
                .setIssuedAt(new java.util.Date())
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .setIssuer("gachiviewer.com")
                .compact();
        
    	System.out.println("Access 토큰 생성 성공!(JwtTokenProvider) : " + jwt);

        
    	
    	int i = jwt.lastIndexOf('.');
		String withoutSignature = jwt.substring(0, i+1);
		Jwt<Header,Claims> temp = Jwts.parser().parseClaimsJwt(withoutSignature);
		String testName = (String) temp.getBody().get("user_id");
		System.out.println("test : " + testName);
    	
        return jwt;
    }

    public String createJwtRefreshToken(String userId) {
    	
        Claims claims = Jwts.claims().setSubject("refresh_token");
        claims.put("value", UUID.randomUUID());
        claims.put("user_id", userId);

        System.out.println(new Date(System.currentTimeMillis() + REFRESH_EXPIRED_TIME));
        
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + REFRESH_EXPIRED_TIME)
                )
//                .setIssuedAt(date)
                .setIssuedAt(new java.util.Date())
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();
        
        
        System.out.println("Refresh 토큰 생성 성공!(JwtTokenProvider) : " + jwt);
        
        return jwt;
    }

    public String getUserId(String token) {
        return getClaimsFromJwtToken(token).get("user_id").toString();
    }

    public String getRefreshTokenValue(String token) {
        String result = getClaimsFromJwtToken(token).get("value").toString();
        
        System.out.println("원래 토큰 정보 : " + token);
        System.out.println("DB 저장 토큰 정보 : " + result);
        
    	return result;
    }
    
    public String getRefreshTokenUserId(String token) {
        String result = getClaimsFromJwtToken(token).get("user_id").toString();
        
        System.out.println("원래 토큰 정보 : " + token);
        System.out.println("DB 저장 토큰 정보 : " + result);
        
    	return result;
    }

    private Claims getClaimsFromJwtToken(String token) {
    	try {
    		return Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    	} catch (ExpiredJwtException e) {
    		return e.getClaims();
    	}
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaimsFromJwtToken(token).get("roles");
    }

    
    
    public boolean validateToken(String token) {
        try { // expiredTime이 그냥 마감되는 날짜인지 아님 시작일에 더하는 숫자인지 알아야됨
//        	System.out.println("현재시간 정보 : " + new Date(System.currentTimeMillis()).getTime());
//        	System.out.println("expired 정보 : " + getExpiredTime(token));
//        	System.out.println("빼기빼기 : " + (new Date(System.currentTimeMillis()).getTime() - getExpiredTime(token).getTime()));
            Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            
            if(checkExpiredTime(token)) {
            	throw new Exception();
            }
        } catch (SignatureException  | MalformedJwtException | // 쪼개기
                UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException jwtException) {
            throw jwtException;
//        	return false;
        } catch (Exception e) {
		}
        
        return true;
    }
    
    public boolean checkExpiredTime(String token) {
    	if((new Date(System.currentTimeMillis()).getTime() - getExpiredTime(token).getTime()) > 30000) {
        	return false;
        }
    	return true;
    }
    
    // 만료기한 받아오는 코드
    public java.util.Date getExpiredTime(String token) {
    	System.out.println(getClaimFromToken(token, Claims::getExpiration));
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    	final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }



}