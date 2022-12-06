package nl.artemy.abnamroassignment2022.ingredients;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;

public class IngredientMapper {
    public static IngredientDto mapEntityToDto(final IngredientEntity ingredientEntity) {
        return new IngredientDto()
                .id(ingredientEntity.getId())
                .name(ingredientEntity.getName())
                .unit(ingredientEntity.getUnit())
                .amount(ingredientEntity.getAmount());
    }
}
