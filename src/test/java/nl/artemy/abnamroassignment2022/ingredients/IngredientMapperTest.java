package nl.artemy.abnamroassignment2022.ingredients;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientMapperTest {
    @Test
    void entityToDtoTest() {
        final var testIngredient = IngredientEntity.builder()
                .id(123L)
                .name("Potato")
                .build();

        final var expectedIngredient = new IngredientDto()
                .id(123L)
                .name("Potato");

        assertThat(IngredientMapper.mapEntityToDto(testIngredient)).isEqualTo(expectedIngredient);
    }
}
