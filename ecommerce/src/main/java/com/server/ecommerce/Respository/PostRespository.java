package com.server.ecommerce.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Posts;

public interface PostRespository extends JpaRepository<Posts, Long> {

}
