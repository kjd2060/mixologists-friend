package com.example.myfirstapp;

import java.util.ArrayList;

public class RecipeData {

    public class Step{
        public String details;

        public Step(String details){
            this.details = details;
        }
    }

    public class Ingredient{
        public String name, amount, description;
        public Ingredient(String name, String amount, String description){
            this.name = name;
            this.amount = amount;
            this.description = description;
        }
    }
    public String name;
    public int serves;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Step> steps;

    public RecipeData(){
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public void AddIngredient(String name, String amount, String description){
        ingredients.add(new Ingredient(name, amount, description));
    }

    public void PrintIngredients(){
        for(Ingredient i : this.ingredients){
            System.out.println(i.name + " " + i.amount + " " + i.description);
        }
    }

    public void AddStep(String details){
        steps.add(new Step(details));
    }

    public void PrintSteps(){
        for(Step step : this.steps){
            System.out.println("Step: " + step.details);
        }
    }
}



