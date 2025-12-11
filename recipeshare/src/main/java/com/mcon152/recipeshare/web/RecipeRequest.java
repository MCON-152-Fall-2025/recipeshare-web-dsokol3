package com.mcon152.recipeshare.web;

public class RecipeRequest {
    private String type; // BASIC, VEGETARIAN, DESSERT, DAIRY
    private String title;
    private String description;
    private String ingredients;
    private String instructions;
    private Integer servings;

    public RecipeRequest() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Backwards-compatible accessors (some code/tests expect "name")
    public String getName() { return title; }
    public void setName(String name) { this.title = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }
}

