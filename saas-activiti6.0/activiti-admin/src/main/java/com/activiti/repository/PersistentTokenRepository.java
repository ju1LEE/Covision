package com.activiti.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.activiti.domain.PersistentToken;

public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByUser(String user);

    List<PersistentToken> findByTokenDateBefore(Date date);
    
    @Modifying
    Long deleteByTokenDateBefore(Date date);

}