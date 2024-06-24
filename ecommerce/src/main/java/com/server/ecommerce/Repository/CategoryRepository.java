package com.server.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
