package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Searching for category by its name.
     * @param name means category name
     * @return Category
     */

    Category findByName(String name);
}
