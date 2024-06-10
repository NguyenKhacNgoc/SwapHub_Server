package com.server.ecommerce.Respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.Posts;

public interface ImageRespository extends JpaRepository<ImgPost, Long> {
    List<ImgPost> findByPost(Posts post);

}
