package nl.artemy.abnamroassignment2022.recipes;

import lombok.RequiredArgsConstructor;
import nl.artemy.abnamroassignment2022.model.db.RecipeEntity;
import nl.artemy.abnamroassignment2022.openapi.model.NewRecipeDto;
import nl.artemy.abnamroassignment2022.openapi.model.RecipeDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public List<RecipeDto> getAllRecipes(final Integer servings,
                                         final Boolean vegetarian,
                                         final String instructions,
                                         final List<String> ingredientIdsToInclude,
                                         final List<String> ingredientIdsToExclude) {
        return recipeRepository.findAll(
                RecipeSpecification
                        .searchRecipes(servings,
                                vegetarian,
                                instructions,
                                ingredientIdsToInclude,
                                ingredientIdsToExclude))
                .stream()
                .map(RecipeMapper::mapEntityToDto)
                .toList();
    }

    public RecipeDto getById(final Long id) {
        final var recipeEntity = recipeRepository.getReferenceById(id);
        return RecipeMapper.mapEntityToDto(recipeEntity);
    }

    public RecipeDto createRecipe(final NewRecipeDto newRecipeDto) {
        final RecipeEntity recipeEntity = RecipeMapper.mapDtoToEntity(newRecipeDto);

        final var recipeIngredients = newRecipeDto.getIngredients().stream()
                .map(recipeIngredientDto -> RecipeMapper.mapDtoToEntity(recipeIngredientDto, recipeEntity)).toList();
        recipeEntity.setIngredients(recipeIngredients);
        recipeRepository.save(recipeEntity);

        return RecipeMapper.mapEntityToDto(recipeEntity);
    }

    public RecipeDto updateRecipe(final Long recipeId, final RecipeDto recipeDto) {
        final var recipeToUpdate = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient with id %d does not exist".formatted(recipeId)));
        recipeToUpdate.setName(recipeDto.getName());
        recipeToUpdate.setServings(recipeDto.getServings());
        recipeToUpdate.setInstructions(recipeDto.getInstructions());

        final var recipeIngredients = recipeDto.getIngredients().stream()
                .map(recipeIngredientDto -> RecipeMapper.mapDtoToEntity(recipeIngredientDto, recipeToUpdate)).collect(Collectors.toList());
        recipeToUpdate.getIngredients().clear();
        recipeToUpdate.getIngredients().addAll(recipeIngredients);
        final var updatedRecipe = recipeRepository.save(recipeToUpdate);

        return RecipeMapper.mapEntityToDto(updatedRecipe);
    }

    public void deleteById(final Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }
}
