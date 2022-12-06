package nl.artemy.abnamroassignment2022.ingredients;

import lombok.RequiredArgsConstructor;
import nl.artemy.abnamroassignment2022.openapi.model.IngredientDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(IngredientMapper::mapEntityToDto)
                .toList();
    }
}
