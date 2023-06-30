package cz.cvut.fel.nss.chatgc.service.impl;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
import cz.cvut.fel.nss.chatgc.events.ChatEvent;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.repository.CategoryRepository;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "categories")
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher publisher;
    private final ChatService chatService;

    @Transactional
    @CacheEvict(allEntries = true)
    public void persist(Category category) {
        categoryRepository.save(category);
        publisher.publishEvent(new CategoryEvent("persist", category));
    }

    @Transactional
    public void update(Category category) {
        categoryRepository.save(category);
        publisher.publishEvent(new CategoryEvent("update", category));
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void updateCategoryFromDto(CategoryDto categoryDto, Integer id) {
        if (findById(id) != null) {
            Category category = findById(id);
            if (!notExists(categoryDto.getName())) {
                throw new ExistsException("category name is not unique");
            }
            category.setName(categoryDto.getName());
            publisher.publishEvent(new CategoryEvent("changeCatIntoRole", category));


            for (Chat c : chatService.findAllByCategoryId(category.getId())) {
                for (Category cat : c.getCategories()) {
                    if (cat.getId().equals(category.getId())) {
                        break;
                    }
                }
                chatService.update(c);
            }

            update(category);
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Category category) {
        for (Chat c : chatService.findAllByCategoryId(category.getId())) {
            Set<Category> cats = c.getCategories();
            cats.remove(category);
            c.setCategories(cats);
            chatService.update(c);
        }
        publisher.publishEvent(new CategoryEvent("delete", category));
        categoryRepository.delete(category);

    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Cacheable()
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Boolean notExists(String catName) {
        return Objects.isNull(categoryRepository.findByName(catName));
    }

    @Transactional
    public void setCategoriesToChat(Chat chat, List<CategoryDto> cats) {
        Set<Category> catsForAdd = new HashSet<>();
        for (CategoryDto c : cats) {
            Category category = findById(c.getId());
            catsForAdd.add(category);
        }
        chat.setCategories(catsForAdd);
        chatService.update(chat);
        publisher.publishEvent(new ChatEvent("update", chat));
    }
}
