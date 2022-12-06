package nl.artemy.abnamroassignment2022;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.model.db.RecipeEntity;
import nl.artemy.abnamroassignment2022.openapi.model.UnitDto;
import nl.artemy.abnamroassignment2022.recipes.RecipeRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class ApplicationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource
    public void shouldFindRecipesWithFilter(final String arguments,
                                            final List<ResultMatcher> assertions) throws Exception {
        final var testRecipe1 = RecipeEntity.builder()
                .name("Apple pie")
                .vegetarian(true)
                .servings(5)
                .instructions("Cook pie")
                .build();
        testRecipe1.setIngredients(List.of(
                IngredientEntity.builder()
                        .name("apple")
                        .recipe(testRecipe1)
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("flour")
                        .recipe(testRecipe1)
                        .amount(3)
                        .unit(UnitDto.TABLESPOON)
                        .build()
        ));
        final var testRecipe2 = RecipeEntity.builder()
                .name("Shepherd's pie")
                .vegetarian(false)
                .servings(4)
                .instructions("Bake Shepherd's pie")
                .build();
        testRecipe2.setIngredients(List.of(
                IngredientEntity.builder()
                        .name("meat")
                        .recipe(testRecipe2)
                        .amount(400)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("potato")
                        .recipe(testRecipe2)
                        .amount(800)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("onion")
                        .recipe(testRecipe2)
                        .amount(1)
                        .unit(UnitDto.PIECE)
                        .build()
        ));
        recipeRepository.saveAll(List.of(testRecipe1, testRecipe2));
        this.mockMvc.perform(get("/recipes" + arguments)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(assertions.toArray(ResultMatcher[]::new));
    }

    private static List<Arguments> shouldFindRecipesWithFilter() {
        return List.of(
                Arguments.of(StringUtils.EMPTY,
                        List.of(
                                jsonPath("$.length()", is(2)),
                                jsonPath("$.[0].name", is("Apple pie")),
                                jsonPath("$.[1].name", is("Shepherd's pie"))
                        )),
                Arguments.of("?vegetarian=true",
                        List.of(
                                jsonPath("$.length()", is(1)),
                                jsonPath("$.[0].name", is("Apple pie")),
                                jsonPath("$.[0].ingredients.length()", is(2))
                        )),
                Arguments.of("?vegetarian=false",
                        List.of(
                                jsonPath("$.length()", is(1)),
                                jsonPath("$.[0].name", is("Shepherd's pie")),
                                jsonPath("$.[0].ingredients.length()", is(3))
                        )),
                Arguments.of("?includeIngredients=apple,meat",
                        List.of(
                                jsonPath("$.length()", is(2)),
                                jsonPath("$.[0].name", is("Apple pie")),
                                jsonPath("$.[1].name", is("Shepherd's pie"))
                        )),
                Arguments.of("?excludeIngredients=meat,flour",
                        List.of(
                                jsonPath("$.length()", is(0))
                        )),
                Arguments.of("?vegetarian=true&includeIngredients=meat",
                        List.of(
                                jsonPath("$.length()", is(0))
                        )),
                Arguments.of("?vegetarian=false&includeIngredients=apple",
                        List.of(
                                jsonPath("$.length()", is(0))
                        )),
                Arguments.of("?servings=4",
                        List.of(
                                jsonPath("$.length()", is(1)),
                                jsonPath("$.[0].name", is("Shepherd's pie"))
                        )),
                Arguments.of("?servings=5&excludeIngredients=apple",
                        List.of(
                                jsonPath("$.length()", is(0))
                        )),
                Arguments.of("?instructions=bake",
                        List.of(
                                jsonPath("$.length()", is(1)),
                                jsonPath("$.[0].name", is("Shepherd's pie"))
                        )),
                Arguments.of("?vegetarian=true&instructions=bake",
                        List.of(
                                jsonPath("$.length()", is(0))
                        ))
        );
    }


    @Test
    public void shouldFindRecipeById() throws Exception {
        final var testRecipe = RecipeEntity.builder()
                .name("Apple pie")
                .vegetarian(true)
                .servings(4)
                .instructions("Bake pie")
                .build();
        testRecipe.setIngredients(List.of(
                IngredientEntity.builder()
                        .name("apple")
                        .recipe(testRecipe)
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("flour")
                        .recipe(testRecipe)
                        .amount(3)
                        .unit(UnitDto.TABLESPOON)
                        .build()
        ));
        recipeRepository.save(testRecipe);
        this.mockMvc.perform(get("/recipes/" + testRecipe.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Apple pie")))
                .andExpect(jsonPath("$.instructions", is("Bake pie")))
                .andExpect(jsonPath("$.servings", is(4)))
                .andExpect(jsonPath("$.ingredients.length()", is(2)));
    }

    @Test
    public void shouldSaveRecipe() throws Exception {
        this.mockMvc.perform(
                        post("/recipes")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                           "name": "Apple Pie",
                                           "vegetarian": true,
                                           "instructions": "Bake pie in the oven",
                                           "servings": 4,
                                           "ingredients": [
                                             {
                                               "name": "apple",
                                               "amount": 700,
                                               "unit": "GRAM"
                                             },
                                             {
                                               "name": "flour",
                                               "amount": 3,
                                               "unit": "TABLESPOON"
                                             }
                                           ]
                                         }"""))
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION));

        final var recipes = recipeRepository.findAll();
        assertThat(recipes).isNotEmpty();
        assertThat(recipes.get(0).getName()).isEqualTo("Apple Pie");
        assertThat(recipes.get(0).getVegetarian()).isEqualTo(true);
        assertThat(recipes.get(0).getInstructions()).isEqualTo("Bake pie in the oven");
        assertThat(recipes.get(0).getServings()).isEqualTo(4);
        assertThat(recipes.get(0).getIngredients()).hasSize(2);
        assertThat(recipes.get(0).getIngredients().get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "recipe")
                .isEqualTo(IngredientEntity.builder()
                        .name("apple")
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build());
        assertThat(recipes.get(0).getIngredients().get(1))
                .usingRecursiveComparison()
                .ignoringFields("id", "recipe")
                .isEqualTo(IngredientEntity.builder()
                        .name("flour")
                        .amount(3)
                        .unit(UnitDto.TABLESPOON)
                        .build());
    }

    @Test
    public void shouldUpdateRecipe() throws Exception {
        final var testRecipe = RecipeEntity.builder()
                .name("Apple pie")
                .vegetarian(true)
                .servings(4)
                .ingredients(new ArrayList<>())
                .instructions("Bake pie")
                .build();
        testRecipe.getIngredients().addAll(List.of(
                IngredientEntity.builder()
                        .name("apple")
                        .recipe(testRecipe)
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("flour")
                        .recipe(testRecipe)
                        .amount(3)
                        .unit(UnitDto.TABLESPOON)
                        .build()
        ));
        recipeRepository.save(testRecipe);
        this.mockMvc.perform(put("/recipes/" + testRecipe.getId())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                   "id": %d,
                                   "name": "Apple pie: Best Recipe",
                                   "vegetarian": true,
                                   "instructions": "Bake pie for 30 minutes at 220 degrees",
                                   "servings": 8,
                                   "ingredients": [
                                     {
                                       "name": "apple",
                                       "amount": 1000,
                                       "unit": "GRAM"
                                     },
                                     {
                                       "name": "flour",
                                       "amount": 6,
                                       "unit": "TABLESPOON"
                                     },
                                     {
                                       "name": "sugar",
                                       "amount": 6,
                                       "unit": "TABLESPOON"
                                     }
                                   ]
                                 }""".formatted(testRecipe.getId())))
                .andExpect(status().isOk());

        final var recipes = recipeRepository.findAll();
        assertThat(recipes).isNotEmpty();
        assertThat(recipes.get(0).getName()).isEqualTo("Apple pie: Best Recipe");
        assertThat(recipes.get(0).getVegetarian()).isEqualTo(true);
        assertThat(recipes.get(0).getInstructions()).isEqualTo("Bake pie for 30 minutes at 220 degrees");
        assertThat(recipes.get(0).getServings()).isEqualTo(8);
        assertThat(recipes.get(0).getIngredients()).hasSize(3);
        assertThat(recipes.get(0).getIngredients().get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "recipe")
                .isEqualTo(IngredientEntity.builder()
                        .name("apple")
                        .amount(1000)
                        .unit(UnitDto.GRAM)
                        .build());
        assertThat(recipes.get(0).getIngredients().get(1))
                .usingRecursiveComparison()
                .ignoringFields("id", "recipe")
                .isEqualTo(IngredientEntity.builder()
                        .name("flour")
                        .amount(6)
                        .unit(UnitDto.TABLESPOON)
                        .build());
        assertThat(recipes.get(0).getIngredients().get(2))
                .usingRecursiveComparison()
                .ignoringFields("id", "recipe")
                .isEqualTo(IngredientEntity.builder()
                        .name("sugar")
                        .amount(6)
                        .unit(UnitDto.TABLESPOON)
                        .build());
    }

    @Test
    public void shouldDeleteRecipe() throws Exception {
        final var testRecipe = RecipeEntity.builder()
                .name("Apple pie")
                .vegetarian(true)
                .servings(4)
                .ingredients(new ArrayList<>())
                .instructions("Bake pie")
                .build();
        testRecipe.getIngredients().add(
                IngredientEntity.builder()
                        .name("apple")
                        .recipe(testRecipe)
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build()
        );
        recipeRepository.save(testRecipe);
        this.mockMvc.perform(delete("/recipes/" + testRecipe.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final var recipes = recipeRepository.findAll();
        assertThat(recipes).isEmpty();
    }

    @Test
    public void shouldFindAllIngredients() throws Exception {
        final var testRecipe = RecipeEntity.builder()
                .name("Apple pie")
                .vegetarian(true)
                .servings(4)
                .instructions("Bake pie")
                .build();
        testRecipe.setIngredients(List.of(
                IngredientEntity.builder()
                        .name("apple")
                        .recipe(testRecipe)
                        .amount(700)
                        .unit(UnitDto.GRAM)
                        .build(),
                IngredientEntity.builder()
                        .name("flour")
                        .recipe(testRecipe)
                        .amount(3)
                        .unit(UnitDto.TABLESPOON)
                        .build()
        ));
        recipeRepository.save(testRecipe);
        this.mockMvc.perform(get("/ingredients")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }
}
