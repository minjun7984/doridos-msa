package kr.doridos.ticketservice.category.repository;

import kr.doridos.ticketservice.category.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(final String categoryName);

    @EntityGraph(attributePaths = "children")
    @Query("select c from Category c where c.parent is null")
    List<Category> findAllCategories();
}

