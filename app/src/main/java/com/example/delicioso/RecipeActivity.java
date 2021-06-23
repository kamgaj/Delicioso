package com.example.delicioso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {
    TextView name;
    TextView ingredients;
    TextView preparation;
    TextView servings;
    TextView difficulty;
    ImageView photo;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe);
        name = findViewById(R.id.nameText);
        ingredients = findViewById(R.id.ingredientsText);
        preparation = findViewById(R.id.preparationText);
        servings = findViewById(R.id.servingsText);
        difficulty = findViewById(R.id.difficultyText);
        photo = findViewById(R.id.recipePhoto);
        String recipeNameFromIntent = getIntent().getStringExtra("RecipeName");
        getRecipeFromFirebase(recipeNameFromIntent);
    }


    public void returnToPreviousScreen(View view) {
        finish();
    }

    private void getRecipeFromFirebase(String recipeName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> ingredientsList = new ArrayList<>();
        List<String> preparationStepsList = new ArrayList<>();

        db.collection("Recipes")
                .whereEqualTo("Name", recipeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                name.setText(document.getString("Name"));

                                String servingsString = "Liczba porcji: " + document.get("Servings");
                                servings.setText(servingsString);

                                String difficultyString = "Trudność: " + document.get("Difficulty") + "/10";
                                difficulty.setText(difficultyString);

                                ingredientsList.addAll((Collection<? extends String>) document.get("Ingredients"));
                                for(String str : ingredientsList) {
                                    stringBuilder.append(str).append("\n");
                                }
                                ingredients.setText(stringBuilder.toString());
                                stringBuilder.setLength(0);

                                preparationStepsList.addAll((Collection<? extends String>) document.get("Preparation"));
                                for(String str : preparationStepsList) {
                                    stringBuilder.append(str).append("\n");
                                }
                                preparation.setText(stringBuilder.toString());

                                String pathToPhoto = document.getString("Photo_link");
                                ref = storage.getReferenceFromUrl(pathToPhoto);
                                ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        createImageFromBitmap(bitmap);
                                        photo.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void createImageFromBitmap(Bitmap bitmap) {
        String fileName = "Photo";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

