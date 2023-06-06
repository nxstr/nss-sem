package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher publisher;

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
    public void delete(Category category){
        categoryRepository.delete(category);
        publisher.publishEvent(new CategoryEvent("delete", category));
    }

    @Transactional
    public void deleteAll(){
        for(Category c: categoryRepository.findAll()){
            publisher.publishEvent(new CategoryEvent("delete", c));
        }
        categoryRepository.deleteAll();
    }

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
