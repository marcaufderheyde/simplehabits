package com.example.simplehabits.ui.home;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplehabits.CatModal;
import com.example.simplehabits.CatRVAdapter;
import com.example.simplehabits.DBHandler;
import com.example.simplehabits.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    // creating variables for our array list,
    // dbhandler, adapter and recycler view.
    private ArrayList<CatModal> habModalArrayList;
    private DBHandler dbHandler;
    private Button readCatBtn;
    private String[] habitNames, habitCurrencies, habitSentiments, habits;
    private ListView listView;
    private String habitPressed = "";
    private int habitPressedNum = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //readCatBtn = root.findViewById(R.id.idBtnReadCourse);
        listView = (ListView)root.findViewById(R.id.list_view);
        populateHabitList();
        //readCatBtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
            //    populateHabitList();
            //}
        //});

        return root;
    }

    public void populateHabitList(){
        // initializing our all variables.
        habModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(getActivity());

        // getting our course array
        // list from db handler class.
        habModalArrayList = dbHandler.readHabits();

        habitNames = new String[habModalArrayList.size()];
        habitCurrencies = new String[habModalArrayList.size()];
        habitSentiments = new String[habModalArrayList.size()];
        habits = new String[habModalArrayList.size()];

        for(int i = 0; i < habModalArrayList.size(); i++) {
            habitNames[i] = habModalArrayList.get(i).getCatName();
            habitCurrencies[i] = habModalArrayList.get(i).getCatCurrency();
            habitSentiments[i] = habModalArrayList.get(i).getCatSentiment();
            habits[i] = habModalArrayList.get(i).getCatName() + ", with a cost of "
                    + habModalArrayList.get(i).getCatCurrency() + "\n "
                    + habModalArrayList.get(i).getCatSentiment() + "\n "
                    + habModalArrayList.get(i).getTimestamp();
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, habits);
        //CatRVAdapter catRVAdapter = new CatRVAdapter(catModalArrayList, getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CatModal item = habModalArrayList.get(position);
                Log.d("Item pressed: ", item.getCatName());

                Toast.makeText(getActivity(), "Press habit five times to delete...", Toast.LENGTH_SHORT).show();

                if(!habitPressed.equals(item.getTimestamp())){
                    habitPressed = item.getTimestamp();
                    habitPressedNum = 1;
                } else if(habitPressed.equals(item.getTimestamp())){
                    habitPressedNum += 1;
                }

                // Delete if pressed five times
                if(habitPressedNum >= 5){
                    dbHandler.deleteHabit(item.getTimestamp());
                    populateHabitList();
                }

                if(item.getCatSentiment().equals("Good")) {
                    view.setBackgroundColor(Color.GREEN);
                } else if(item.getCatSentiment().equals("Bad")) {
                    view.setBackgroundColor(Color.RED);
                } else {
                    view.setBackgroundColor(Color.YELLOW);
                }

            }
        });
    }
}