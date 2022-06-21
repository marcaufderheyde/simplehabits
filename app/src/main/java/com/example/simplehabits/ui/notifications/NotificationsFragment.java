package com.example.simplehabits.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplehabits.CatModal;
import com.example.simplehabits.DBHandler;
import com.example.simplehabits.MainActivity;
import com.example.simplehabits.R;
import com.example.simplehabits.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private ArrayList<ArrayList<CatModal>> habModalArrayList;
    private DBHandler dbHandler;
    private String[] habitNames, habitCurrencies, habitSentiments, habitTimestamps;
    private EditText days_back_input;
    private Button days_back_btn;
    private FloatingActionButton settings_btn;
    private GraphView graph;
    private RadioButton timeGraph, barGraph;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        settings_btn = root.findViewById(R.id.settings_btn);
        days_back_btn = root.findViewById(R.id.show_data_btn);
        days_back_input = root.findViewById(R.id.days_back_input);
        timeGraph = root.findViewById(R.id.timeGraph);
        barGraph = root.findViewById(R.id.barGraph);


        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        days_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshGraph(root);
            }
        });

        timeGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshGraph(root);
            }
        });

        barGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshGraph(root);
            }
        });

        return root;
    }

    private void refreshGraph(View root) {
        // Hide the keyboard after clicking
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(days_back_btn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        if(days_back_input.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
            return;
        }
        int days = Integer.valueOf(days_back_input.getText().toString());
        dbHandler = new DBHandler(getActivity());

        habModalArrayList = dbHandler.readXDaysHabits(days);

        DataPoint[] gHabits = new DataPoint[habModalArrayList.size()];
        DataPoint[] mHabits = new DataPoint[habModalArrayList.size()];
        DataPoint[] bHabits = new DataPoint[habModalArrayList.size()];

        int goodHabits = 0;
        int midHabits = 0;
        int badHabits = 0;
        int totalGoodHabits = 0;
        int totalMidHabits = 0;
        int totalBadHabits = 0;

        for(int i = 0; i < habModalArrayList.size(); i++) {
            ArrayList<CatModal> dayHabits = habModalArrayList.get(i);
            for(int j = 0; j < dayHabits.size(); j++) {
                Log.d("FOUND: ", dayHabits.get(j).getCatName());
                Log.d("FOUND: ", dayHabits.get(j).getCatCurrency());
                Log.d("FOUND: ", dayHabits.get(j).getCatSentiment());
                Log.d("FOUND: ", dayHabits.get(j).getTimestamp());
                if (dayHabits.get(j).getCatSentiment().equals("Good")) {
                    goodHabits += 1;
                } else if (dayHabits.get(j).getCatSentiment().equals("Neither here nor there")) {
                    midHabits += 1;
                } else if (dayHabits.get(j).getCatSentiment().equals("Bad")) {
                    badHabits += 1;
                }
            }

            gHabits[i] = new DataPoint(i,goodHabits);
            mHabits[i] = new DataPoint(i,midHabits);
            bHabits[i] = new DataPoint(i,badHabits);
            totalGoodHabits += goodHabits;
            totalMidHabits += midHabits;
            totalBadHabits += badHabits;
            goodHabits = 0;
            midHabits = 0;
            badHabits = 0;
        }

        graph = (GraphView) root.findViewById(R.id.graph);

        RadioGroup graphType = root.findViewById(R.id.graphType);

        // get selected radio button from radioGroup
        int selectedId = graphType.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) root.findViewById(selectedId);

        Toast.makeText(getActivity(),
                radioButton.getText(), Toast.LENGTH_SHORT).show();

        if(radioButton.getText().equals("Bar Graph")) {
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, totalGoodHabits),
                    new DataPoint(1, totalMidHabits),
                    new DataPoint(2, totalBadHabits),
            });

            graph.addSeries(series);
            // styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            series.setSpacing(5);

            // draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);
        }
        else {
            LineGraphSeries<DataPoint> gHabitSeries = new LineGraphSeries<>(gHabits);
            LineGraphSeries<DataPoint> mHabitSeries = new LineGraphSeries<>(mHabits);
            LineGraphSeries<DataPoint> bHabitSeries = new LineGraphSeries<>(bHabits);
            graph.addSeries(gHabitSeries);
            graph.addSeries(mHabitSeries);
            graph.addSeries(bHabitSeries);
        }

        // Make the text report below the graph
        TextView habitReport = root.findViewById(R.id.habitReport);
        double averageGoodHabits = (double) totalGoodHabits / (double) days;
        double averageMidHabits = (double) totalMidHabits / (double) days;
        double averageBadHabits = (double) totalBadHabits / (double) days;
        habitReport.setText("Good habits: " + String.valueOf(totalGoodHabits)
                + ", average of " + String.valueOf(averageGoodHabits)
                + " per day\n\n"
                + "Mid habits: " + String.valueOf(totalMidHabits)
                + ", average of " + String.valueOf(averageMidHabits)
                + " per day\n\n"
                + "Bad habits: " + String.valueOf(totalBadHabits)
                + ", average of " + String.valueOf(averageBadHabits)
                + " per day\n\n");
    }
}