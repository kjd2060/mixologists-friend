package com.example.myfirstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{
    MyRecyclerViewAdapter adapter;
    ArrayList<String> recipeNames;
    ArrayList<RecipeData> Recipes;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("hello world");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recipeNames = new ArrayList<>();
        Recipes = new ArrayList<>();

        ParseXML();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvRecipes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter(this, recipeNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void onNewRecipeClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Recipe");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                insertNewRecipe();
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

    @Override
    public void onItemClick(View view, int position) {
        String recipeName = adapter.getItem(position);
        String ret = "";
        Gson gson = new Gson();
        String recipeAsString = "";
        for(RecipeData recipe : Recipes){
            if(recipe.name.equals(recipeName)){
                ret = recipe.name + " has " + recipe.ingredients.size() + " ingredients and " + recipe.steps.size() + " steps.  Row number was ";
                recipeAsString = gson.toJson(recipe);
            }
        }


        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
        intent.putExtra("RecipeAsString", recipeAsString);
        startActivity(intent);
        System.out.println(ret);
        // Toast.makeText(this, "You clicked " + ret + position, Toast.LENGTH_SHORT).show();
    }

    private void ParseXML(){
        XmlPullParserFactory parserFactory;
        try{
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("recipes.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        ArrayList<RecipeData> recipes = new ArrayList<>();
        int eventType = parser.getEventType();
        RecipeData currentRecipe = null;

        while(eventType != XmlPullParser.END_DOCUMENT){
            String eltName = null;
            switch(eventType){
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if("recipe".equals(eltName)){
                        currentRecipe = new RecipeData();
                        currentRecipe.name = parser.getAttributeValue(0);
                    }
                    else if(currentRecipe != null){
                        if("ingredient".equals(eltName)){
                            currentRecipe.AddIngredient(parser.getAttributeValue(0), parser.getAttributeValue(1), parser.getAttributeValue(2));
                        }
                        else if("step".equals(eltName)){
                            currentRecipe.AddStep(parser.getAttributeValue(0));
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    eltName = parser.getName();
                    if("recipe".equals(eltName)){
                        recipes.add(currentRecipe);
                    }
                    break;
                default:
                    break;
                }

                eventType = parser.next();
             }

            printRecipes(recipes);
        }

    private void printRecipes(ArrayList<RecipeData> recipes){
        for(RecipeData recipe : recipes){
            recipeNames.add(recipe.name);
            Recipes.add(recipe);
        }
    }

    private void insertNewRecipe(){
        RecipeData newRecipe = new RecipeData();
        newRecipe.name = m_Text;
        Recipes.add(newRecipe);
        recipeNames.add(newRecipe.name);
        adapter.notifyItemInserted(recipeNames.size()-1);
    }
}
