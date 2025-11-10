package com.mcon152.recipeshare.service;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.repository.RecipeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Assignment: Implement all TODOs using Mockito features covered in class:
 *  - @Mock, @InjectMocks, @Captor, @ExtendWith(MockitoExtension.class)
 *  - Stubbing: thenReturn / thenAnswer / thenThrow
 *  - Verifications: verify(...), times/never/atLeast..., verifyNoMoreInteractions
 *  - InOrder (where meaningful)
 *  - Void stubbing: doNothing / doThrow (use deleteById for this)
 *  - Matchers: any(), eq(), argThat()
 *  - ArgumentCaptor
 *  - (Optional) Spy demo if you introduce a small helper in tests
 *
 * NOTE: This is a pure unit test. Do NOT start a Spring context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService (Mockito) — Assignment Skeleton")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService; // CUT implements RecipeService

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    // --- Helpers for sample data ---

    private Recipe newRecipeNoId() {
        return new Recipe(
                null,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    private Recipe savedRecipe(long id) {
        return new Recipe(
                id,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    // ------------------ addRecipe ------------------

    @Nested
    @DisplayName("addRecipe(Recipe)")
    class AddRecipe {

        @Test
        @DisplayName("returns saved entity (thenReturn) and calls repository.save once")
        void returnsSaved_andSavesOnce() {
            // TODO:
            // 1) when(recipeRepository.save(...)).thenReturn(savedRecipe(1L))
            // 2) call recipeService.addRecipe(newRecipeNoId())
            // 3) assert non-null id and fields
            // 4) verify(recipeRepository).save(any(Recipe.class)); verifyNoMoreInteractions(recipeRepository)

            //See code below as an example answer

            Recipe input = newRecipeNoId();
            Recipe saved = savedRecipe(1L);

            when(recipeRepository.save(any(Recipe.class))).thenReturn(saved);

            Recipe out = recipeService.addRecipe(input);
            assertEquals(1L, out.getId());
            assertEquals(saved, out);

            verify(recipeRepository).save(any(Recipe.class));
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("assigns ID dynamically (thenAnswer) and captures argument")
        void assignsId_thenAnswer_andCaptures() {
            // TODO:
            // 1) Use thenAnswer to return a new Recipe with id=1L, copying fields from arg
            // 2) capture the arg with ArgumentCaptor and assert title, id==null pre-save

            //See code below as an example answer

            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> {
                Recipe r = inv.getArgument(0);
                return new Recipe(1L, r.getTitle(), r.getDescription(),
                        r.getIngredients(), r.getInstructions(), r.getServings());
            });

            Recipe out = recipeService.addRecipe(newRecipeNoId());
            assertEquals(1L, out.getId());

            verify(recipeRepository).save(recipeCaptor.capture());
            Recipe sent = recipeCaptor.getValue();
            assertNull(sent.getId()); // before persistence
            assertEquals("Chocolate Cake", sent.getTitle());
        }

        @Test
        @DisplayName("propagates repository failure (thenThrow)")
        void propagatesRepositoryFailure() {
            // TODO:
            // when(recipeRepository.save(any())).thenThrow(new IllegalStateException("DB down"))
            // assertThrows on recipeService.addRecipe(...)

            when(recipeRepository.save(any(Recipe.class)))
                    .thenThrow(new IllegalStateException("DB down"));

            assertThrows(IllegalStateException.class, () -> {
                recipeService.addRecipe(newRecipeNoId());
            });
        }
    }

    // ------------------ getAllRecipes ------------------

    @Nested
    @DisplayName("getAllRecipes()")
    class GetAllRecipes {

        @Test
        @DisplayName("returns list from repository")
        void returnsList() {
            // TODO:
            // when(recipeRepository.findAll()).thenReturn(List.of(...))
            // assert same size/content; verify(findAll)

            Recipe r1 = savedRecipe(1L);
            Recipe r2 = savedRecipe(2L);
            when(recipeRepository.findAll()).thenReturn(List.of(r1, r2));

            List<Recipe> result = recipeService.getAllRecipes();

            assertEquals(2, result.size());
            assertTrue(result.contains(r1));
            assertTrue(result.contains(r2));
            verify(recipeRepository).findAll();
        }
    }

    // ------------------ getRecipeById ------------------

    @Nested
    @DisplayName("getRecipeById(long)")
    class GetById {

        @Test
        @DisplayName("returns Optional.present when found")
        void present() {
            // TODO: stub findById(1L)->Optional.of(savedRecipe(1L)), assert present

            long id = 1L;
            Recipe saved = savedRecipe(id);
            when(recipeRepository.findById(id)).thenReturn(Optional.of(saved));

            Optional<Recipe> result = recipeService.getRecipeById(id);

            assertTrue(result.isPresent());
            assertEquals(saved, result.get());
            verify(recipeRepository).findById(id);
        }

        @Test
        @DisplayName("returns Optional.empty when missing")
        void empty() {
            // TODO: stub Optional.empty, assert empty

            long id = 999L;
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Recipe> result = recipeService.getRecipeById(id);

            assertTrue(result.isEmpty());
            verify(recipeRepository).findById(id);
        }
    }

    // ------------------ deleteRecipe ------------------

    @Nested
    @DisplayName("deleteRecipe(long)")
    class DeleteRecipe {

        @Test
        @DisplayName("returns true when entity existed")
        void returnsTrue_whenExists() {
            // TODO:
            // when(recipeRepository.existsById(id)).thenReturn(true)
            // doNothing().when(recipeRepository).deleteById(id)
            // assert true; verify order: existsById -> deleteById

            long id = 1L;
            when(recipeRepository.existsById(id)).thenReturn(true);
            doNothing().when(recipeRepository).deleteById(id);

            boolean result = recipeService.deleteRecipe(id);

            assertTrue(result);
            InOrder inOrder = inOrder(recipeRepository);
            inOrder.verify(recipeRepository).existsById(id);
            inOrder.verify(recipeRepository).deleteById(id);
        }

        @Test
        @DisplayName("returns false when missing (never deletes)")
        void returnsFalse_whenMissing() {
            // TODO: existsById -> false; assert false; verify deleteById never called

            long id = 999L;
            when(recipeRepository.existsById(id)).thenReturn(false);

            boolean result = recipeService.deleteRecipe(id);

            assertFalse(result);
            verify(recipeRepository).existsById(id);
            verify(recipeRepository, never()).deleteById(id);
        }

        @Test
        @DisplayName("propagates delete error (doThrow)")
        void propagatesDeleteError() {
            // TODO: existsById -> true; doThrow(...) on deleteById; assertThrows

            long id = 1L;
            when(recipeRepository.existsById(id)).thenReturn(true);
            doThrow(new RuntimeException("Delete failed"))
                    .when(recipeRepository).deleteById(id);

            assertThrows(RuntimeException.class, () -> {
                recipeService.deleteRecipe(id);
            });
        }
    }

    // ------------------ updateRecipe ------------------

    @Nested
    @DisplayName("updateRecipe(long, Recipe)")
    class UpdateRecipe {

        @Test
        @DisplayName("returns updated entity when exists")
        void returnsUpdated_whenExists() {
            // TODO:
            // findById -> present(existing)
            // save(...) -> updatedSaved
            // assert Optional.present & fields updated
            // capture arg and assert values

            long id = 1L;
            Recipe existing = savedRecipe(id);
            // Create updated recipe with DIFFERENT servings to properly test the field is updated
            Recipe updated = new Recipe(id, "Updated Title", "Updated Desc", 
                    "new ingredients", "new instructions", 12); // Changed servings from 8 to 12

            when(recipeRepository.findById(id)).thenReturn(Optional.of(existing));
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

            Optional<Recipe> result = recipeService.updateRecipe(id, updated);

            assertTrue(result.isPresent());
            Recipe resultRecipe = result.get();
            assertEquals("Updated Title", resultRecipe.getTitle());
            assertEquals("Updated Desc", resultRecipe.getDescription());
            assertEquals(12, resultRecipe.getServings()); // Verify servings was actually updated

            verify(recipeRepository).findById(id);
            verify(recipeRepository).save(recipeCaptor.capture());
            Recipe captured = recipeCaptor.getValue();
            assertEquals("Updated Title", captured.getTitle());
            assertEquals(12, captured.getServings()); // Verify servings in captured argument
        }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called

            long id = 999L;
            Recipe updated = new Recipe(id, "Title", "Desc", "ing", "inst", 4);
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Recipe> result = recipeService.updateRecipe(id, updated);

            assertTrue(result.isEmpty());
            verify(recipeRepository).findById(id);
            verify(recipeRepository, never()).save(any(Recipe.class));
        }
    }

    // ------------------ patchRecipe ------------------

    @Nested
    @DisplayName("patchRecipe(long, Recipe)")
    class PatchRecipe {

        @Test
        @DisplayName("applies only non-null fields (argThat)")
        void appliesNonNullFields_only() {
            // TODO:
            // findById -> present(existing)
            // provide partial with only title set
            // repository.save returns the modified entity (use thenAnswer echo)
            // verify save(argThat(...)) to ensure unchanged fields remain as-is

            long id = 1L;
            Recipe existing = savedRecipe(id);
            Recipe partial = new Recipe(null, "Patched Title", null, null, null, null);

            when(recipeRepository.findById(id)).thenReturn(Optional.of(existing));
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

            Optional<Recipe> result = recipeService.patchRecipe(id, partial);

            assertTrue(result.isPresent());
            Recipe resultRecipe = result.get();
            assertEquals("Patched Title", resultRecipe.getTitle());
            assertEquals("Moist chocolate cake", resultRecipe.getDescription()); // unchanged
            assertEquals(8, resultRecipe.getServings()); // unchanged - verify servings preserved

            verify(recipeRepository).save(argThat(r -> 
                r.getTitle().equals("Patched Title") &&
                r.getDescription().equals("Moist chocolate cake") &&
                r.getServings() == 8
            ));
        }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called

            long id = 999L;
            Recipe partial = new Recipe(null, "Title", null, null, null, null);
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Recipe> result = recipeService.patchRecipe(id, partial);

            assertTrue(result.isEmpty());
            verify(recipeRepository).findById(id);
            verify(recipeRepository, never()).save(any(Recipe.class));
        }
    }

    // ------------------ extra practice ------------------

    @Nested
    @DisplayName("Advanced stubbing & verification")
    class Advanced {

        @Test
        @DisplayName("consecutive stubs on existsById (true, false)")
        void consecutiveStubs_existsById() {
            // TODO: when(existsById(1L)).thenReturn(true, false); verify two calls and no more

            long id = 1L;
            when(recipeRepository.existsById(id)).thenReturn(true, false);

            boolean first = recipeRepository.existsById(id);
            boolean second = recipeRepository.existsById(id);

            assertTrue(first);
            assertFalse(second);
            verify(recipeRepository, times(2)).existsById(id);
        }
    }
}
