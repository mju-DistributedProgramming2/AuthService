package com.omnm.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// registry.addMapping("/**").allowedOrigins("http://localhost:18080");
		// registry.addMapping("/**").allowedOrigins("*"); // => not avaliable
		registry.addMapping("/**"); // 이게 모든 주소, 모든 방식에 대해, 전역 CORS 허용 설정
	}
	
}
