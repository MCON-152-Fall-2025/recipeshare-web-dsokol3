package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.service.RecipeFactory;
import com.mcon152.recipeshare.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Create a new recipe.
     */
    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody RecipeRequest recipeRequest) {
        MDC.put("recipeName", recipeRequest.getTitle());
        logger.info("Entering controller: POST /api/recipes");
        logger.debug("Recipe add request summary: name={}, type={}", recipeRequest.getTitle(), recipeRequest.getType());

        try {
            Recipe toSave = RecipeFactory.createFromRequest(recipeRequest);
            Recipe saved = recipeService.addRecipe(toSave);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            logger.info("Created new recipe with id {}", saved.getId());
            return ResponseEntity.created(location).body(saved);

        } catch (Exception e) {
            logger.error("Error creating recipe", e);
            return ResponseEntity.internalServerError().build();

        } finally {
            MDC.clear();
        }
    }

    /**
     * Retrieve all recipes.
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        logger.info("Entering controller: GET /api/recipes");

        List<Recipe> all = recipeService.getAllRecipes();
        logger.info("Retrieved {} recipes", all == null ? 0 : all.size());

        return ResponseEntity.ok(all);
    }

    /**
     * Retrieve a recipe by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable long id) {
        logger.info("Entering controller: GET /api/recipes/{}", id);

        var opt = recipeService.getRecipeById(id);
        if (opt.isPresent()) {
            logger.info("Found recipe with id {}", id);
            return ResponseEntity.ok(opt.get());
        } else {
            logger.warn("Recipe not found with id {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a recipe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        MDC.put("recipeName", "DELETE id=" + id);
        logger.info("Entering controller: DELETE /api/recipes/{}", id);

        try {
            boolean deleted = recipeService.deleteRecipe(id);

            if (deleted) {
                logger.info("Deleted recipe with id {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Attempted to delete non-existing recipe with id {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Error deleting recipe with id {}", id, e);
            return ResponseEntity.internalServerError().build();

        } finally {
            MDC.clear();
        }
    }

    /**
     * Replace a recipe (full update).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable long id,
                                               @RequestBody RecipeRequest updatedRequest) {

        MDC.put("recipeName", updatedRequest.getTitle());
        logger.info("Entering controller: PUT /api/recipes/{}", id);
        logger.debug("Recipe update request summary: name={}, type={}",
                updatedRequest.getTitle(), updatedRequest.getType());

        try {
            Recipe updatedRecipe = RecipeFactory.createFromRequest(updatedRequest);
            var opt = recipeService.updateRecipe(id, updatedRecipe);

            if (opt.isPresent()) {
                logger.info("Updated recipe with id {}", id);
                return ResponseEntity.ok(opt.get());
            } else {
                logger.warn("Recipe not found for update with id {}", id);
                return ResponseEntity.notFound().build();
            }

        } finally {
            MDC.clear();
        }
    }

    /**
     * Partial update.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> patchRecipe(@PathVariable long id,
                                              @RequestBody RecipeRequest partialRequest) {

        MDC.put("recipeName", partialRequest.getTitle());
        logger.info("Entering controller: PATCH /api/recipes/{}", id);
        logger.debug("Recipe patch request summary: name={}, type={}",
                partialRequest.getTitle(), partialRequest.getType());

        try {
            Recipe partialRecipe = RecipeFactory.createFromRequest(partialRequest);
            var opt = recipeService.patchRecipe(id, partialRecipe);

            if (opt.isPresent()) {
                logger.info("Patched recipe with id {}", id);
                return ResponseEntity.ok(opt.get());
            } else {
                logger.warn("Recipe not found for patch with id {}", id);
                return ResponseEntity.notFound().build();
            }

        } finally {
            MDC.clear();
        }
    }
}
