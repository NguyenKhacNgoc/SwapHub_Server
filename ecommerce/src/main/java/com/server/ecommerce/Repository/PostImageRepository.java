package com.server.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.entity.ImgPost;
import com.server.ecommerce.entity.Posts;

public interface PostImageRepository extends JpaRepository<ImgPost, String> {
    List<ImgPost> findByPost(Posts post);

}
