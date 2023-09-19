package com.server.ecommerce.Respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.User;

public interface PostRespository extends JpaRepository<Posts, Long> {
    List<Posts> findByUser(User user);

}
