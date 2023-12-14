package com.omnm.auth.repository;

import com.omnm.auth.model.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerDAO extends JpaRepository<LoginRequest, String>{
}