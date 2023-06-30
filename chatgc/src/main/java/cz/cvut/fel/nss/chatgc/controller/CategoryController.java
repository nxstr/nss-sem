package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private Visitor v;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates new category.
     *
     * @param categoryDto CategoryDto contains data that will be saved
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/api/categories/new", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryDto.accept(v);
        if (categoryService.notExists(categoryDto.getName())) {
            categoryService.persist(category);
            LOG.info("Category {} successfully created", categoryDto.getName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            LOG.info("category name is not unique: {}", categoryDto.getName());
            return new ResponseEntity<>("category name is not unique", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes category by id.
     *
     * @param id Integer id of category
     * @return ResponseEntity<String>
     */
    @DeleteMapping(value = "/api/categories/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.delete(categoryService.findById(id));
            LOG.info("Category {} successfully deleted", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException e) {
            LOG.info("category does not exist: {}", id);
            return new ResponseEntity<>("category does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates category by id.
     *
     * @param id          Integer id of category
     * @param categoryDto CategoryDto has data that will be saved
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "/api/categories/update/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        try {
            categoryService.updateCategoryFromDto(categoryDto, id);
            LOG.info("Category {}({}) successfully updated", categoryDto.getName(), categoryDto.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ExistsException e) {
            LOG.info(e.getMessage() + ": {}({})", categoryDto.getName(), categoryDto.getId());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            LOG.info("category does not exist: {}({})", categoryDto.getName(), categoryDto.getId());
            return new ResponseEntity<>("category does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Finds all categories. Uses cached values.
     *
     * @return List<CategoryDto>
     */
    @GetMapping(value = "/api/categories")
    public List<CategoryDto> getCategoryList() {
        List<CategoryDto> cats = new ArrayList<>();
        for (Category c : categoryService.findAll()) {
            CategoryDto dto = c.accept(v);
            cats.add(dto);
        }
        return cats;
    }
}
