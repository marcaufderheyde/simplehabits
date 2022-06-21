package com.example.simplehabits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.simplehabits.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private Spinner choose_sentiment, choose_category_update_delete, choose_sentiment_update;
    private EditText choose_category_name, choose_category_currency, update_category_name, update_category_currency;
    private DBHandler dbHandler;
    private String[] categoryCurrencies, categoryNames, categorySentiments, categories;
    private ArrayList<CatModal> catModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final FloatingActionButton habitsButton = findViewById(R.id.habits_btn);
        final Button addCategoryButton = findViewById(R.id.idBtnAddCat);
        final Button editCategoryButton = findViewById(R.id.idBtnUpdateCat);
        final Button deleteCategoryButton = findViewById(R.id.idBtnDeleteCat);
        // below line is to add on click listener for our add course button.
        habitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                //((MainActivity) getActivity()).startActivity(intent);
                startActivity(intent);
            }
        });

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(Settings.this);

        populateFields(dbHandler);

        // Listener for add category button
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard after clicking
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addCategoryButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                // below line is to get data from all edit text fields.
                String catName = choose_category_name.getText().toString();
                String catCurrency = choose_category_currency.getText().toString();
                String catSentiment = choose_sentiment.getSelectedItem().toString();


                // validating if the text fields are empty or not.
                if (catName.isEmpty() || catCurrency.isEmpty() || catSentiment.isEmpty()) {
                    Toast.makeText(Settings.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // We don't want SQL injections
                if (catName.contains("'") || catCurrency.contains("'") || catSentiment.contains("'")) {
                    Toast.makeText(Settings.this, "We don't allow apostrophe's", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewCategory(catName, catCurrency, catSentiment);

                // after adding the data we are displaying a toast message.
                Toast.makeText(Settings.this, "Category has been added.", Toast.LENGTH_SHORT).show();
                choose_category_name.setText("");
                choose_category_currency.setText("");
                choose_sentiment.clearFocus();

                // We've changed the database so refresh our UI
                populateFields(dbHandler);
            }
        });

        // Listener for edit category button
        editCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard after clicking
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCategoryButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                // below line is to get data from all edit text fields.
                String catName = choose_category_update_delete.getSelectedItem().toString();
                String newCatName = update_category_name.getText().toString();
                String newCatCurrency = update_category_currency.getText().toString();
                String newCatSentiment = choose_sentiment_update.getSelectedItem().toString();

                // validating if the text fields are empty or not.
                if (catName.isEmpty() || newCatCurrency.isEmpty() || newCatSentiment.isEmpty() || newCatSentiment.isEmpty()) {
                    Toast.makeText(Settings.this, "Please select all details for update...", Toast.LENGTH_SHORT).show();
                    return;
                }

                // We don't want SQL injections
                if (newCatCurrency.contains("'") || newCatName.contains("'") || newCatSentiment.contains("'")) {
                    Toast.makeText(Settings.this, "We don't allow apostrophe's", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.updateCategory(catName, newCatName, newCatCurrency, newCatSentiment);

                choose_category_update_delete.clearFocus();
                Toast.makeText(Settings.this, "Attempted to update category in database", Toast.LENGTH_SHORT).show();
                update_category_name.setText("");
                update_category_currency.setText("");
                choose_sentiment_update.clearFocus();

                // We've changed the database so refresh our UI
                populateFields(dbHandler);
            }
        });

        // Listener for delete category button
        deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard after clicking
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(deleteCategoryButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                // below line is to get data from all edit text fields.
                String catName = choose_category_update_delete.getSelectedItem().toString();

                // validating if the text fields are empty or not.
                if (catName.isEmpty()) {
                    Toast.makeText(Settings.this, "Please select a category to delete...", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.deleteCategory(catName);

                // after adding the data we are displaying a toast message.
                Toast.makeText(Settings.this, "Category has been deleted.", Toast.LENGTH_SHORT).show();
                update_category_name.setText("");
                update_category_currency.setText("");
                choose_sentiment_update.clearFocus();

                // We've changed the database so refresh our UI
                populateFields(dbHandler);
            }
        });
    }

    // This sets up the values of the spinners from the database
    public void populateFields(DBHandler dbHandler){
        // Read the categories from the database and save info
        catModalArrayList = dbHandler.readCategories();

        // Set the available sentiments for adding categories
        // For adding a category
        choose_sentiment = findViewById(R.id.choose_cat_default_sentiment);
        choose_category_name = findViewById(R.id.idEdtCatName);
        choose_category_currency = findViewById(R.id.idEdtCatCurrency);

        // For updating and deleting a category
        choose_category_update_delete = findViewById(R.id.choose_category_update_delete);
        choose_sentiment_update = findViewById(R.id.choose_cat_default_sentiment_update);
        update_category_name = findViewById(R.id.idUpdateCatName);
        update_category_currency = findViewById(R.id.idUpdateCatCurrency);

        categoryNames = new String[catModalArrayList.size()];
        categoryCurrencies = new String[catModalArrayList.size()];
        categorySentiments = new String[catModalArrayList.size()];
        categories = new String[catModalArrayList.size()];

        for(int i = 0; i < catModalArrayList.size(); i++) {
            categoryNames[i] = catModalArrayList.get(i).getCatName();
            categoryCurrencies[i] = catModalArrayList.get(i).getCatCurrency();
            categorySentiments[i] = catModalArrayList.get(i).getCatSentiment();
            categories[i] = (catModalArrayList.get(i).getCatName() + " -- " +
                    catModalArrayList.get(i).getCatCurrency() + " -- "
                    + catModalArrayList.get(i).getCatSentiment());
        }

        String[] sentiments = new String[]{"Good", "Neither here nor there", "Bad"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter adapter1 = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        ArrayAdapter adapter2 = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, sentiments);
        //set the spinners adapter to the previously created one.
        choose_category_update_delete.setAdapter(adapter1);
        choose_sentiment.setAdapter(adapter2);
        choose_sentiment_update.setAdapter(adapter2);
    }
}
