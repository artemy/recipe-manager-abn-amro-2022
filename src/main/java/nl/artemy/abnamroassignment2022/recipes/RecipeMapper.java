package nl.artemy.abnamroassignment2022.recipes;

import nl.artemy.abnamroassignment2022.ingredients.IngredientMapper;
import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.model.db.RecipeEntity;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;
import nl.artemy.abnamroassignment2022.openapi.model.NewRecipeDto;
import nl.artemy.abnamroassignment2022.openapi.model.RecipeDto;

public class RecipeMapper {
    static RecipeEntity mapDtoToEntity(final NewRecipeDto newRecipeDto) {
        return RecipeEntity.builder()
                .name(newRecipeDto.getName())
                .vegetarian(newRecipeDto.getVegetarian())
                .servings(newRecipeDto.getServings())
                .instructions(newRecipeDto.getInstructions()).build();
    }

    static IngredientEntity mapDtoToEntity(final IngredientDto ingredientDto,
                                           final RecipeEntity recipe) {
        return IngredientEntity.builder()
                .recipe(recipe)
                .name(ingredientDto.getName())
                .unit(ingredientDto.getUnit())
                .amount(ingredientDto.getAmount())
                .build();
    }

    public static RecipeDto mapEntityToDto(final RecipeEntity recipeEntity) {
        return new RecipeDto()
                .id(recipeEntity.getId())
                .name(recipeEntity.getName())
                .vegetarian(recipeEntity.getVegetarian())
                .servings(recipeEntity.getServings())
                .ingredients(recipeEntity.getIngredients().stream()
                        .map(IngredientMapper::mapEntityToDto)
                        .toList())
                .instructions(recipeEntity.getInstructions());
    }
}
