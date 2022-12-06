package nl.artemy.abnamroassignment2022.ingredients;

import lombok.RequiredArgsConstructor;
import nl.artemy.abnamroassignment2022.openapi.api.IngredientsApi;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IngredientController implements IngredientsApi {

    private final IngredientService ingredientService;

    @Override
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }
}
