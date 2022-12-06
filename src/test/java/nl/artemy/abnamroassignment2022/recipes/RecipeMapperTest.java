package nl.artemy.abnamroassignment2022.recipes;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.model.db.RecipeEntity;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;
import nl.artemy.abnamroassignment2022.openapi.model.NewRecipeDto;
import nl.artemy.abnamroassignment2022.openapi.model.RecipeDto;
import nl.artemy.abnamroassignment2022.openapi.model.UnitDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeMapperTest {

    @Test
    void mapNewRecipeDtoToEntity() {
        final var testRecipe = new NewRecipeDto()
                .name("Mashed potatoes")
                .instructions("Cook potatoes and mash 'em")
                .servings(4);

        final var expectedRecipe = RecipeEntity.builder()
                .name("Mashed potatoes")
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .build();

        assertThat(RecipeMapper.mapDtoToEntity(testRecipe))
                .usingRecursiveComparison()
                .ignoringFields("ingredients")
                .isEqualTo(expectedRecipe);
    }

    @Test
    void testMapRecipeDtoToEntity() {
        final var testIngredient = new IngredientDto()
                .id(123L)
                .name("potato")
                .unit(UnitDto.PIECE)
                .amount(4);

        final var testRecipeEntity = RecipeEntity.builder()
                .name("Mashed potatoes")
                .vegetarian(true)
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .build();

        final var expectedIngredient = IngredientEntity.builder()
                .name("potato")
                .recipe(testRecipeEntity)
                .unit(testIngredient.getUnit())
                .amount(testIngredient.getAmount())
                .build();

        assertThat(RecipeMapper.mapDtoToEntity(testIngredient, testRecipeEntity))
                .usingRecursiveComparison()
                .isEqualTo(expectedIngredient);
    }

    @Test
    void mapRecipeEntityToDto() {
        final var testRecipeEntity = RecipeEntity.builder()
                .name("Mashed potatoes")
                .vegetarian(true)
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .ingredients(List.of(IngredientEntity.builder()
                        .id(123L)
                        .name("Potato")
                        .amount(5)
                        .unit(UnitDto.PIECE)
                        .build()))
                .build();

        final var expectedRecipe = new RecipeDto()
                .name("Mashed potatoes")
                .vegetarian(true)
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .ingredients(List.of(new IngredientDto()
                        .id(123L)
                        .name("Potato")
                        .amount(5)
                        .unit(UnitDto.PIECE)));

        assertThat(RecipeMapper.mapEntityToDto(testRecipeEntity))
                .usingRecursiveComparison()
                .isEqualTo(expectedRecipe);
    }

    @Test
    void testMapEntityToDto() {
        final var testRecipeEntity = RecipeEntity.builder()
                .id(123L)
                .name("Mashed potatoes")
                .vegetarian(true)
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .ingredients(List.of(IngredientEntity.builder()
                        .id(123L)
                        .name("Potato")
                        .amount(5)
                        .unit(UnitDto.PIECE)
                        .build()))
                .build();

        final var expectedRecipeIngredient = new RecipeDto()
                .id(123L)
                .name("Mashed potatoes")
                .vegetarian(true)
                .instructions("Cook potatoes and mash 'em")
                .servings(4)
                .ingredients(List.of(
                        new IngredientDto()
                                .id(123L)
                                .name("Potato")
                                .amount(5)
                                .unit(UnitDto.PIECE)
                ));

        assertThat(RecipeMapper.mapEntityToDto(testRecipeEntity))
                .usingRecursiveComparison()
                .isEqualTo(expectedRecipeIngredient);
    }
}
