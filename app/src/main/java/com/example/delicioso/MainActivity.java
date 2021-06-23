package com.example.delicioso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ArrayList<HomeRecipeListItem> easiestRecipes = new ArrayList<>();
    ArrayList<HomeRecipeListItem> hardestRecipes = new ArrayList<>();
    Chip search;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        getEasiestRecipes();
        getHardestRecipes();
    }

    private void getEasiestRecipes() {
        db.collection("Recipes")
                .orderBy("Difficulty")
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String path = document.getString("Photo_link");
                                easiestRecipes.add(new HomeRecipeListItem(document.getString("Name"), path));
                            }
                            LinearLayout linearLayout;
                            linearLayout = findViewById(R.id.easiestRecipesLinearLayout);
                            addToView(easiestRecipes, linearLayout);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getHardestRecipes() {
        db.collection("Recipes")
                .orderBy("Difficulty", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String path = document.getString("Photo_link");
                                hardestRecipes.add(new HomeRecipeListItem(document.getString("Name"), path));
                            }
                            LinearLayout linearLayout;
                            linearLayout = findViewById(R.id.hardestRecipesLinearLayout);
                            addToView(hardestRecipes, linearLayout);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addToView(ArrayList<HomeRecipeListItem> recipesList, LinearLayout linearLayout) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (int i = 0; i < recipesList.size(); i++) {
            View view = layoutInflater.inflate(R.layout.home_page_item, linearLayout, false);
            TextView name = view.findViewById(R.id.RecipeName);
            ImageView photo = view.findViewById(R.id.Photo);
            name.setText(recipesList.get(i).getName());

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference ref = storage.getReferenceFromUrl(recipesList.get(i).getPathToPhoto());
            ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    photo.setImageBitmap(bitmap);
                }
            });

            int finalI = i;
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                    intent.putExtra("RecipeName", recipesList.get(finalI).getName());
                    startActivity(intent);
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                    intent.putExtra("RecipeName", recipesList.get(finalI).getName());
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }
}

