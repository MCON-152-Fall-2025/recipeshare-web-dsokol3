package com.mcon152.recipeshare.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping("/welcome")
    public String home() {
        return "Welcome to RecipeShare!";
    }

    @GetMapping("/welcome/hello")
    public String hello() { return "RecipeShare is up!"; }
}
