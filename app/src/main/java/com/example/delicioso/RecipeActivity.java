package com.example.delicioso;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.StorageReference;

public class RecipeActivity extends AppCompatActivity {
    TextView name;
    TextView ingredients;
    TextView preparation;
    TextView servings;
    RatingBar difficulty;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe);
        name = findViewById(R.id.nameText);
        ingredients = findViewById(R.id.ingredientsText);
    }


    public void returnToPreviousScreen(View view) {
        finish();
    }
}

