package nl.artemy.abnamroassignment2022.ingredients;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
}
