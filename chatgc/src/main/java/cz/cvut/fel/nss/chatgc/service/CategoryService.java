package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;

import java.util.List;

public interface CategoryService {
    void persist(Category category);
    void update(Category category);
    void updateCategoryFromDto(CategoryDto categoryDto, Integer id);
    void delete(Category category);
    Category findById(Integer id);
    List<Category> findAll();
    void setCategoriesToChat(Chat chat, List<CategoryDto> cats);
    Boolean notExists(String catName);
}
