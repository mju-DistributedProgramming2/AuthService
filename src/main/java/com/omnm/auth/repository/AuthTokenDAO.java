package com.omnm.auth.repository;


import com.omnm.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AuthTokenDAO extends JpaRepository<RefreshToken, Integer>{

	@Query("Select r from RefreshToken r Where r.userId = :userId")
	RefreshToken findByUserId(String userId);
	@Modifying
	@Query("delete from RefreshToken r where r.userId = :userId")
	void deleteByUserId(String userId);
	@Modifying
	@Query("delete from RefreshToken r where r.token = :token")
	void deleteByRefreshToken(String token);

	
}