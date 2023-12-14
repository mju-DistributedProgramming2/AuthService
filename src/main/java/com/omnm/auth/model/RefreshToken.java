package com.omnm.auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
	
	@Id
	private String userId;
	private String token;
	private String deviceId;
	
	
	public RefreshToken() {
		super();
		
		this.userId = "";
		this.token = "";
		this.deviceId = "";
	}
	
	public RefreshToken(String userId, String token, String deviceId) {
		this.userId = userId;
		this.token = token;
		this.deviceId = deviceId;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	
}
