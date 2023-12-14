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
        Claims claims = Jwts.claims().setSubject("access_token");
        claims.put("user_id", userId);
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + ACCESS_EXPIRED_TIME)
                )
                .setIssuedAt(new java.util.Date())
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .setIssuer("gachiviewer.com")
                .compact();
    	int i = jwt.lastIndexOf('.');
		String withoutSignature = jwt.substring(0, i+1);
		Jwt<Header,Claims> temp = Jwts.parser().parseClaimsJwt(withoutSignature);
        return jwt;
    }
    public String createJwtRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject("refresh_token");
        claims.put("value", UUID.randomUUID());
        claims.put("user_id", userId);
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setExpiration(
                        new Date(System.currentTimeMillis() + REFRESH_EXPIRED_TIME)
                )
                .setIssuedAt(new java.util.Date())
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();
        return jwt;
    }
    public String getUserId(String token) {
        return getClaimsFromJwtToken(token).get("user_id").toString();
    }

    public String getRefreshTokenValue(String token) {
        String result = getClaimsFromJwtToken(token).get("value").toString();
    	return result;
    }
    public String getRefreshTokenUserId(String token) {
        String result = getClaimsFromJwtToken(token).get("user_id").toString();
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
        	Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            if(checkExpiredTime(token)) {
            	throw new Exception();
            }
        } catch (SignatureException  | MalformedJwtException | // 쪼개기
                UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException jwtException) {
            throw jwtException;
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
    public java.util.Date getExpiredTime(String token) {
    	System.out.println(getClaimFromToken(token, Claims::getExpiration));
		return getClaimFromToken(token, Claims::getExpiration);
	}
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    	final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }



}