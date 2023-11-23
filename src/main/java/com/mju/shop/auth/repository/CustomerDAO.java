package com.mju.shop.auth.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mju.shop.auth.model.LoginRequest;


public interface CustomerDAO extends JpaRepository<LoginRequest, String>{
	
}