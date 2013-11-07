package com.daou.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daou.book.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) = LOWER(:lastName)")
    public List<User> find(@Param("lastName") String lastName);

    public List<User> findByName(String lastName);
    
    public List<User> findByLastName(String lastName);
}
