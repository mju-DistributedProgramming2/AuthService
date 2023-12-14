package com.omnm.auth.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.omnm.auth.commons.Constants;
import com.omnm.auth.model.LoginRequest;
import com.omnm.auth.repository.AuthTokenDAO;
import com.omnm.auth.repository.CustomerDAO;
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
		Optional<LoginRequest> customer_optional = customerDAO.findById(user_id);
        if(!customer_optional.isPresent()) {throw new UsernameNotFoundException(user_id + " 아이디가 존재하지 않습니다.");}
        LoginRequest customer = customer_optional.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        System.out.println("Email " + customer.getCustomerId() + " nickname " + customer.getPassword());
        customer.setPassword(customer.encoderPassword(customer.getPassword()));
        return new User(customer.getCustomerId(), customer.getPassword(), authorities);
    }
	
	public Object getCustomerInCustomerService(String username) {
		String url = Constants.CUSTOMER_SERVICE_BASE_URL + Constants.CUSTOMER_SERVICE_GET_CUSTOMER_URL + username;
		RestTemplate template = new RestTemplate();
        URI uri = UriComponentsBuilder
                .fromUriString(Constants.CUSTOMER_SERVICE_BASE_URL) //http://localhost에 호출
                .path(Constants.CUSTOMER_SERVICE_GET_CUSTOMER_URL+"customer")
                .queryParam("customerId", username)  // query parameter가 필요한 경우 이와 같이 사용
                .encode()
                .build()
                .toUri();
        ResponseEntity<String> result = template.getForEntity(uri, String.class);
		return result.getBody();
	}
}
