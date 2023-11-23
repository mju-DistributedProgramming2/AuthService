package com.mju.shop.auth.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mju.shop.auth.commons.Constants;
import com.mju.shop.auth.model.LoginRequest;
import com.mju.shop.auth.repository.AuthTokenDAO;
import com.mju.shop.auth.repository.CustomerDAO;

@Service
public class AuthService implements UserDetailsService {

	@Autowired
	CustomerDAO customerDAO;
	
	@Autowired
	AuthTokenDAO customerTokenDAO;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Override
    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException {
		System.out.println("loadUserByUsername !! ");
//		Optional<Customer> customer_optional = customerDAO.findById(user_id);
		Optional<LoginRequest> customer_optional = customerDAO.findById(user_id);
        if(!customer_optional.isPresent()) {
        	System.out.println("존재하지 않는 사용자 !");
            throw new UsernameNotFoundException(user_id + " 아이디가 존재하지 않습니다.");
        }
        System.out.println("존재하는 사용자 ! ");
        LoginRequest customer = customer_optional.get();
        
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        System.out.println("Email " + customer.getUsername() + " nickname " + customer.getPassword());

        customer.setPassword(customer.encoderPassword(customer.getPassword()));
//        authorities.add(new SimpleGrantedAuthority(customer.getDtype()));
        
        return new User(customer.getUsername().toString(), customer.getPassword(), authorities);
//        return customer;
    }
	
	
	
	public Object getCustomerInCustomerService(String username) {
		System.out.println("Customer 정보 받아오기! (CustomerService 호출)");
		String url = Constants.CUSTOMER_SERVICE_BASE_URL + Constants.CUSTOMER_SERVICE_GET_CUSTOMER_URL + username;
		
		RestTemplate template = new RestTemplate();
        URI uri = UriComponentsBuilder
                .fromUriString(Constants.CUSTOMER_SERVICE_BASE_URL) //http://localhost에 호출
                .path(Constants.CUSTOMER_SERVICE_GET_CUSTOMER_URL)
                .queryParam("id", username)  // query parameter가 필요한 경우 이와 같이 사용
                .encode()
                .build()
                .toUri();
		
        ResponseEntity<String> result = template.getForEntity(uri, String.class);
        // entity로 데이터를 가져오겠다(Get)~~
        System.out.println(result.getStatusCode());
        System.out.println(result.getBody());
        
		return result.getBody();
		
//		return null;
	}
	

}