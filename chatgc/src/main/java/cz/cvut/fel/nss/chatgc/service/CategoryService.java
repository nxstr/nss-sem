package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher publisher;
    private final ChatService chatService;

    @Transactional
    public void persist(Category category){
        categoryRepository.save(category);
        publisher.publishEvent(new CategoryEvent("persist", category));
    }

    @Transactional
    public void update(Category category){
        categoryRepository.save(category);
        publisher.publishEvent(new CategoryEvent("update", category));
    }

    @Transactional
    public void updateCategoryFromDto(CategoryDto categoryDto, Integer id){
        if (findById(id)!=null) {
            Category category = findById(id);

            category.setName(categoryDto.getName());
            publisher.publishEvent(new CategoryEvent("changeCatIntoRole", category));


            for(Chat c: chatService.findAllByCategoryId(category.getId())){
                for(Category cat: c.getCategories()){
                    if(cat.getId().equals(category.getId())){
                        cat = category;
                        break;
                    }
                }
                chatService.update(c);
            }

            update(category);
        }
    }


    @Transactional
    public void delete(Category category){
        for(Chat c: chatService.findAllByCategoryId(category.getId())){
            Set<Category> cats = c.getCategories();
            cats.remove(category);
            c.setCategories(cats);
            chatService.update(c);
        }
        publisher.publishEvent(new CategoryEvent("delete", category));
        categoryRepository.delete(category);

    }

//    @Transactional
//    public void deleteAll(){
//        for(Category c: categoryRepository.findAll()){
//            publisher.publishEvent(new CategoryEvent("delete", c));
//        }
//        categoryRepository.deleteAll();
//    }

    public Category findById(Integer id){
        return categoryRepository.findById(id).orElse(null);
    }

    public Category findByName(String name){
        return categoryRepository.findByName(name);
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Boolean notExists(String catName){
        return Objects.isNull(categoryRepository.findByName(catName));
    }
}
