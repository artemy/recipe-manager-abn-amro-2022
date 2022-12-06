package nl.artemy.abnamroassignment2022.recipes;

import lombok.RequiredArgsConstructor;
import nl.artemy.abnamroassignment2022.openapi.api.RecipesApi;
import nl.artemy.abnamroassignment2022.openapi.model.NewRecipeDto;
import nl.artemy.abnamroassignment2022.openapi.model.RecipeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecipeController implements RecipesApi {

    private final RecipeService recipeService;

    @Override
    public ResponseEntity<RecipeDto> createRecipe(final NewRecipeDto newRecipeDto) {
        final var recipe = recipeService.createRecipe(newRecipeDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{recipeId}").
                        buildAndExpand(recipe.getId()).toUri()
        ).body(recipe);
    }

    @Override
    public ResponseEntity<List<RecipeDto>> getAllRecipes(final Boolean vegetarian,
                                                         final List<String> ingredientsToInclude,
                                                         final List<String> ingredientsToExclude,
                                                         final String instructions,
                                                         final Integer servings) {
        return ResponseEntity.ok(
                recipeService.getAllRecipes(
                        servings,
                        vegetarian,
                        instructions,
                        ingredientsToInclude,
                        ingredientsToExclude));
    }

    @Override
    public ResponseEntity<RecipeDto> getRecipeById(final Long recipeId) {
        return ResponseEntity.ok(recipeService.getById(recipeId));
    }

    @Override
    public ResponseEntity<RecipeDto> updateRecipe(final Long recipeId, final RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, recipeDto));
    }

    @Override
    public ResponseEntity<Void> deleteRecipe(final Long recipeId) {
        recipeService.deleteById(recipeId);
        return ResponseEntity.noContent().build();
    }
}
