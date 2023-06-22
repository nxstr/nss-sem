package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.DiscriminatorValue;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/api/categories/new", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity createCategory(@RequestBody CategoryDto categoryDto){
            Category category = new Category(categoryDto.getName());
            if (categoryService.notExists(categoryDto.getName())) {
                categoryService.persist(category);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/api/categories/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity deleteCategory(@PathVariable Integer id){
            if (categoryService.findById(id)!=null) {
                categoryService.delete(categoryService.findById(id));
                return new ResponseEntity<>(HttpStatus.OK);
            }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping(value = "/api/categories/update/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto){
            if (categoryService.findById(id)!=null) {
                categoryService.updateCategoryFromDto(categoryDto, id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping(value ="/api/categories")
    @PreAuthorize("hasAuthority('admin')")
    public List<CategoryDto> getCategoryList(){
        List<CategoryDto> cats = new ArrayList<>();
            for(Category c: categoryService.findAll()){
                CategoryDto dto = new CategoryDto();
                dto.setName(c.getName());
                dto.setId(c.getId());
                cats.add(dto);
            }
        return cats;
    }
}
