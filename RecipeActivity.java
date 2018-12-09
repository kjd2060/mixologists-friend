package com.example.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class RecipeActivity extends AppCompatActivity {

    RecipeData recipe;
    int currentStep = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_layout);

        Gson gson = new Gson();
        String recipeAsString = getIntent().getStringExtra("RecipeAsString");
        recipe = gson.fromJson(recipeAsString, RecipeData.class);
        TextView recipeName = findViewById(R.id.recipeName);
        recipeName.setText(recipe.name);
        LinearLayout topLayout = findViewById(R.id.topLinearLayout);
        for(int i = 0; i < recipe.ingredients.size(); i++){
            RecipeData.Ingredient ingredient = recipe.ingredients.get(i);
            TextView ingredientTextView = new TextView(this);
            if(!ingredient.amount.isEmpty() && !ingredient.description.isEmpty()){
                ingredientTextView.setText(ingredient.amount + " " + ingredient.name + " (" +ingredient.description + ")");
            }
            else if(!ingredient.amount.isEmpty()){
                ingredientTextView.setText(ingredient.amount + " " + ingredient.name);
            }
            else if(!ingredient.description.isEmpty()){
                ingredientTextView.setText(ingredient.name + " (" +ingredient.description + ")");
            }
            else {
                ingredientTextView.setText(ingredient.name);
            }
            topLayout.addView(ingredientTextView);
        }

        LinearLayout bottomLayout = findViewById(R.id.bottomLinearLayout);
        for(int i = 0; i < recipe.steps.size(); i++){
            RecipeData.Step step = recipe.steps.get(i);
            TextView stepTextView = new TextView(this);
            stepTextView.setText("Step " + (i+1) + ": " + step.details);
            bottomLayout.addView(stepTextView);
            currentStep++;
        }
    }

    public void onDeleteClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Recipe");

        // Set up the input
        final TextView input = new TextView(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setText("Are you sure you want to delete this recipe?");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void onAddIngredientClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Ingredient");

        // Set up the input
        final EditText ingredientName = new EditText(this);
        final EditText ingredientAmount = new EditText(this);
        final EditText ingredientDescription = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        ingredientName.setHint("Ingredient name");
        ingredientAmount.setHint("Ingredient amount");
        ingredientDescription.setHint("Ingredient description");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(ingredientName);
        ll.addView(ingredientAmount);
        ll.addView(ingredientDescription);
        builder.setView(ll);
        final TextView tv = new TextView(this);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recipe.AddIngredient(ingredientName.getText().toString(), ingredientAmount.getText().toString(), ingredientDescription.getText().toString());
                LinearLayout topLayout = findViewById(R.id.topLinearLayout);
                tv.setText(ingredientAmount.getText().toString() + " " + ingredientName.getText().toString() + " (" + ingredientDescription.getText().toString()+")");
                topLayout.addView(tv);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void onAddStepClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Step");

        // Set up the input
        final EditText step = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        step.setHint("Step details");
        builder.setView(step);
        final TextView tv = new TextView(this);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recipe.AddStep(step.getText().toString());
                LinearLayout bottomLayout = findViewById(R.id.bottomLinearLayout);
                currentStep++;
                tv.setText("Step " + currentStep + ": " + step.getText().toString());
                bottomLayout.addView(tv);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
