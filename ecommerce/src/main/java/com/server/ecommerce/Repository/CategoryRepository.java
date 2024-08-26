package com.server.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
