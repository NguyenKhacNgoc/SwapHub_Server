package com.server.ecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.ecommerce.entity.Category;
import com.server.ecommerce.exception.AppException;
import com.server.ecommerce.exception.ErrorCode;
import com.server.ecommerce.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXIST);
        }
        return categories;
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
    }

}
