package com.example.delicioso;

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
}
