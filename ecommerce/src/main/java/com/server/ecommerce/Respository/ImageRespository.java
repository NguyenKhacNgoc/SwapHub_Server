package com.server.ecommerce.Respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Images;
import com.server.ecommerce.Entity.Posts;

public interface ImageRespository extends JpaRepository<Images, Long> {
    List<Images> findByPost(Posts post);

}
