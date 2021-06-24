package com.example.delicioso;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity {
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayAdapter<String> namesArray;
    ArrayAdapter<String> servingsArray;
    ListView searchView;
    RadioButton name;
    RadioButton serving;
    boolean ifServing = false;
    ImageView home;
    EditText search;
    public static final String NAME_WORD = "Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        serving = findViewById(R.id.serving);
        name = findViewById(R.id.name);
        home = findViewById(R.id.homeButton);
        searchView = findViewById(R.id.searchListView);
        search = findViewById(R.id.SearchTextList);

        search.setEnabled(false);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setAdapter(namesArray);
                search.setEnabled(true);
                search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                ifServing=false;
                goToSearchedRecipeDescription();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
            }
        });

        serving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.getText().clear();
                search.setEnabled(false);
                queryServingsFromFirebase();
                getStringFromXML();
            }
        });

        name.performClick();

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(search.getText().length()>=3) {
                    String start = search.getText().toString();
                    start = start.substring(0, 1).toUpperCase() + start.substring(1);
                    String end = search.getText().toString() + "\uf8ff";
                    end = end.substring(0, 1).toUpperCase() + end.substring(1);
                    db.collection("Recipes")
                            .orderBy(NAME_WORD)
                            .startAt(start)
                            .endAt(end)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        List<String> titles = new ArrayList<>();
                                        for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            titles.add(document.getString(NAME_WORD));
                                        }
                                        namesArray =  new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,titles);
                                        searchView.setAdapter(namesArray);
                                    }
                                }
                            });
                } else {
                    namesArray = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1,
                            new ArrayList<>());
                    searchView.setAdapter(namesArray);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


    }

    @Override
    public void onBackPressed(){
        if(ifServing){
            serving.performClick();
            ifServing = false;
        }
        else{
            finish();
        }
    }

    private void getStringFromXML() {
        String[] servingsFromXML = getResources().getStringArray(R.array.servings);
        servingsArray = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, servingsFromXML);
        searchView.setAdapter(servingsArray);
    }

    private void queryServingsFromFirebase() {
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ifServing=true;
                String selectedServing = (String) parent.getItemAtPosition(position);
                System.out.println(Integer.valueOf(selectedServing));
                db.collection("Recipes")
                        .whereEqualTo("Servings", Integer.valueOf(selectedServing))
                        //.whereArrayContains("Servings", Integer.valueOf(selectedServing))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    List<String> names = new ArrayList<>();
                                    for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        names.add(document.getString(NAME_WORD));
                                    }
                                    servingsArray = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                                    searchView.setAdapter(servingsArray);
                                    goToSearchedRecipeDescription();
                                }
                            }
                        });
            }
        });
    }

    private void goToSearchedRecipeDescription() {
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchActivity.this, RecipeActivity.class);
                intent.putExtra("RecipeName", selectedItem);
                startActivity(intent);
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent( SearchActivity.this, LoginActivity.class));
    }
}
