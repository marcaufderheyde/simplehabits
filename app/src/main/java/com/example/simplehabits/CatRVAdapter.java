package com.example.simplehabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CatRVAdapter extends RecyclerView.Adapter<CatRVAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<CatModal> catModalArrayList;
    private Context context;

    // constructor
    public CatRVAdapter(ArrayList<CatModal> catModalArrayList, Context context) {
        this.catModalArrayList = catModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        CatModal modal = catModalArrayList.get(position);
        holder.catNameTV.setText(modal.getCatName());
        holder.catCurrencyTV.setText(modal.getCatCurrency());
        holder.catSentimentTV.setText(modal.getCatSentiment());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return catModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView catNameTV, catCurrencyTV, catSentimentTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            catNameTV = itemView.findViewById(R.id.idTVCatName);
            catCurrencyTV = itemView.findViewById(R.id.idTVCatCurrency);
            catSentimentTV = itemView.findViewById(R.id.idTVCatSentiment);
        }
    }
}
