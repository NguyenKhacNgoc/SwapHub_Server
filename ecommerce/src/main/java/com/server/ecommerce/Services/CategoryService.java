package com.server.ecommerce.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.ecommerce.Entity.Category;
import com.server.ecommerce.Exception.AppException;
import com.server.ecommerce.Exception.ErrorCode;
import com.server.ecommerce.Repository.CategoryRepository;

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

    public List<String> getAllNameCategory() {
        List<Category> categories = categoryRepository.findAll();
        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("Tất cả");
        for (Category category : categories) {
            String Strcategory = category.getName();
            categoriesName.add(Strcategory);
        }
        return categoriesName;
    }

}
