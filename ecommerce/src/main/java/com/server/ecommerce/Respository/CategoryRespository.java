package com.server.ecommerce.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Category;

public interface CategoryRespository extends JpaRepository<Category, Long> {

}
