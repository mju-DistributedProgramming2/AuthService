package com.mju.shop.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mju.shop.auth.model.RefreshToken;


public interface AuthTokenDAO extends JpaRepository<RefreshToken, Integer>{

//	@Query("Select r from RefreshToken r Where r.userId = :userId AND r.browser = :browser")
//	RefreshToken findByUserIdANDBrowser(String userId, String browser);
	
	@Query("Select r from RefreshToken r Where r.userId = :userId")
	RefreshToken findByUserId(String userId);
	
	@Modifying
	@Query("delete from RefreshToken r where r.userId = :userId")
	void deleteByUserId(String userId);

	@Modifying
	@Query("delete from RefreshToken r where r.token = :token")
	void deleteByRefreshToken(String token);

	
}