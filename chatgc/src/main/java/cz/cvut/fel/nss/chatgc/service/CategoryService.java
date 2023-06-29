package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;

import java.util.List;

/**
 * Represents Category Service.
 */

public interface CategoryService {

    /**
     * Creates new category.
     * @param category entity that will be saved
     */
    void persist(Category category);

    /**
     * Updates category.
     * @param category entity that will be updated
     */
    void update(Category category);

    /**
     *Converts categoryDto to category entity for update call.
     * @param categoryDto represents categoryDTO
     * @param id id of updating category
     */
    void updateCategoryFromDto(CategoryDto categoryDto, Integer id);

    /**
     * Deletes category.
     * @param category entity that will be deleted
     */
    void delete(Category category);

    /**
     * Finds category by its id.
     * @param id Id of category
     * @return Category
     */
    Category findById(Integer id);

    /**
     * Finds all Categories.
     * @return List<Category>
     */
    List<Category> findAll();

    /**
     * Sets list of categories to chat.
     * @param chat represents chat that will contain list of categories
     * @param cats list of categoryDTO that will be converted and added to chat
     */
    void setCategoriesToChat(Chat chat, List<CategoryDto> cats);

    /**
     * Checks if category with catName already exist.
     * @param catName name of category
     * @return Boolean
     */
    Boolean notExists(String catName);
}
