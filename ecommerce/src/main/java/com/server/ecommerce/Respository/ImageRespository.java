package com.server.ecommerce.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Images;

public interface ImageRespository extends JpaRepository<Images, Long> {

}
