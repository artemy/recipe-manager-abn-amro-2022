package nl.artemy.abnamroassignment2022.recipes;

import nl.artemy.abnamroassignment2022.model.db.IngredientEntity;
import nl.artemy.abnamroassignment2022.model.db.RecipeEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;

public enum RecipeSpecification {
    ;

    public static Specification<RecipeEntity> searchRecipes(
            final Integer servings,
            final Boolean vegetarian,
            final String instructions,
            final List<String> includeIngredients,
            final List<String> excludeIngredients) {
        return (recipe, cq, cb) -> {
            cq.orderBy(cb.asc(recipe.get("name")));
            Predicate predicate = cb.conjunction();

            if (servings != null) {
                predicate = cb.and(predicate, cb.equal(recipe.get("servings"), servings));
            }

            if (vegetarian != null) {
                predicate = cb.and(predicate, cb.equal(recipe.get("vegetarian"), vegetarian));
            }

            if (instructions != null && !instructions.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(recipe.get("instructions")), "%" + instructions.toLowerCase() + "%"));
            }

            if (!CollectionUtils.isEmpty(includeIngredients)) {
                final Subquery<String> subquery = ingredientNameSubquery(cq.subquery(String.class), recipe, includeIngredients);
                predicate = cb.and(predicate, recipe.get("id").in(subquery));
            }

            if (!CollectionUtils.isEmpty(excludeIngredients)) {
                final Subquery<String> subquery = ingredientNameSubquery(cq.subquery(String.class), recipe, excludeIngredients);
                predicate = cb.and(predicate, recipe.get("id").in(subquery).not());
            }

            return predicate;
        };
    }

    private static Subquery<String> ingredientNameSubquery(final Subquery<String> subquery, final Root<RecipeEntity> root, final List<String> excludeIngredients) {
        final Root<RecipeEntity> subRoot = subquery.correlate(root);
        final Join<RecipeEntity, IngredientEntity> join = subRoot.join("ingredients");
        return subquery.select(subRoot.get("id")).where(join.get("name").in(excludeIngredients));
    }

}
