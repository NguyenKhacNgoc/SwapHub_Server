package com.server.ecommerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.Posts;

public interface PostImageRepository extends JpaRepository<ImgPost, String> {
    List<ImgPost> findByPost(Posts post);

}
