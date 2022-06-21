package com.example.simplehabits.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplehabits.CatModal;
import com.example.simplehabits.DBHandler;
import com.example.simplehabits.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.common.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    // creating variables for our edittext, button and dbhandler
    private EditText catCurrencyEdt;
    private ArrayList<CatModal> catModalArrayList;
    private Button addHabitButton;
    private DBHandler dbHandler;
    private String[] categoryNames, categoryCurrencies, categorySentiments;
    private Spinner choose_category, choose_sentiment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // initializing all our variables.
        catCurrencyEdt = root.findViewById(R.id.idEdtCourseDuration);
        addHabitButton = root.findViewById(R.id.idBtnAddCourse);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(getActivity());

        // Read the categories from the database and save info
        catModalArrayList = dbHandler.readCategories();

        categoryNames = new String[catModalArrayList.size()];
        categoryCurrencies = new String[catModalArrayList.size()];
        categorySentiments = new String[catModalArrayList.size()];

        for(int i = 0; i < catModalArrayList.size(); i++) {
            categoryNames[i] = catModalArrayList.get(i).getCatName();
            categoryCurrencies[i] = catModalArrayList.get(i).getCatCurrency();
            categorySentiments[i] = catModalArrayList.get(i).getCatSentiment();
        }

        //get the spinner from the xml.
        choose_category = root.findViewById(R.id.choose_category);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryNames);
        //set the spinners adapter to the previously created one.
        choose_category.setAdapter(adapter);

        //get the spinner from the xml.
        choose_sentiment = root.findViewById(R.id.choose_sentiment);
        String[] sentiments = new String[]{"Good", "Neither here nor there", "Bad"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sentiments);
        //set the spinners adapter to the previously created one.
        choose_sentiment.setAdapter(adapter2);

        choose_category.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Nothing selected on spinner: ", "FALSE");
                        refreshSentimentSpinner(root);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("Nothing selected on spinner: ", "TRUE");
                    }
                });

        catCurrencyEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshSentimentSpinner(root);
            }
        });

        // below line is to add on click listener for our add course button.
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard after clicking
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addHabitButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                // below line is to get data from all edit text fields.
                String catName = choose_category.getSelectedItem().toString();
                String catCurrency = catCurrencyEdt.getText().toString();
                String catSentiment = choose_sentiment.getSelectedItem().toString();
                Log.d("catName", catName);
                Log.d("catCurrency", catCurrency);
                Log.d("catSentiment", catSentiment);

                // validating if the text fields are empty or not.
                if (catName.isEmpty() || catCurrency.isEmpty() || catSentiment.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewHabit(catName, catCurrency, catSentiment);

                // after adding the data we are displaying a toast message.
                Toast.makeText(getActivity(), "Habit has been added.", Toast.LENGTH_SHORT).show();
                choose_category.clearFocus();
                catCurrencyEdt.setText("");
                choose_sentiment.clearFocus();
            }
        });

        refreshSentimentSpinner(root);
        return root;
    }

    public void refreshSentimentSpinner(View root) {
        choose_sentiment = root.findViewById(R.id.choose_sentiment);
        String[] sentiments = new String[]{"Good", "Neither here nor there", "Bad"};
        String chosenCategory = choose_category.getSelectedItem().toString();
        int index = Arrays.asList(categoryNames).indexOf(chosenCategory);
        String chosenCategorySentiment = categorySentiments[index];
        Log.d("chosenCategorySentiment", chosenCategorySentiment);

        for(int i = 0; i < sentiments.length; i ++) {
            if (chosenCategorySentiment.equals(sentiments[i])) {
                String temp = sentiments[0];
                sentiments[0] = chosenCategorySentiment;
                sentiments[i] = temp;
                break;
            }
        }

        Log.d("sentiments[0]: ", sentiments[0]);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sentiments);
        //set the spinners adapter to the previously created one.
        choose_sentiment.setAdapter(adapter2);
    }
}