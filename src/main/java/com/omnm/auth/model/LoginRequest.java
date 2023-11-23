package com.omnm.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name="customer")
public class LoginRequest{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	String username;	
	
	@ApiModelProperty(value = "비밀번호")
	String password;
	
	public String encoderPassword(String password) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		
		this.password = bCryptPasswordEncoder.encode(password);
		
		System.out.println("암호화 된 패스워드 !! " + this.password);
		
		return this.password;
	}
	
	public LoginRequest() {
		super();
		
		this.username = "";
		this.password = "";
	}
	
	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
