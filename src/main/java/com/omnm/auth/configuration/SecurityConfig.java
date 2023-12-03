package com.omnm.auth.configuration;

import com.omnm.auth.Handler.CustomLogoutHandler;
import com.omnm.auth.filter.LoginAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.omnm.auth.provider.JwtTokenProvider;
import com.omnm.auth.service.AuthService;
import com.omnm.auth.service.TokenService;

//import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private AuthService customerService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private TokenService tokenService;
    private AuthService authService;
//    private CustomLogoutHandler customLogoutHandler;

    
    public SecurityConfig(AuthService customerService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, 
    		TokenService tokenService, AuthService authService){ // 
        this.customerService = customerService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
        this.authService = authService;
//        this.customLogoutHandler = customLogoutHandler;
    }
    
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	System.out.println("configure");
        auth.userDetailsService(customerService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		// Custom Login Authentication 필터를 사용함
		LoginAuthenticationFilter loginAuthenticationFilter =
                new LoginAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider, tokenService, authService);
        loginAuthenticationFilter.setFilterProcessesUrl("/login");
//        loginAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());

        http.csrf().disable();
        http.logout().logoutSuccessHandler(new CustomLogoutHandler(tokenService, jwtTokenProvider));
        
        // session을 사용하지 않기 때문에 session 설정을 stateless로 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        // Spring Security만 사용할 시 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정하지만,
    	// 현재는 APi Gateway에서 JWT로 검증을 하기 때문에 모든 Url에서 접근을 허용
//        http.authorizeRequests().antMatchers("/auth/**").permitAll().anyRequest().authenticated();
        http.authorizeRequests().anyRequest().permitAll();

        http.addFilterBefore(loginAuthenticationFilter, LoginAuthenticationFilter.class);
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}