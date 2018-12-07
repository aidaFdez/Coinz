package com.example.aafo.coinz;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Set;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder>{
    String TAG = "GoalsAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTextview;
        public TextView descrTextview;

        public ViewHolder(View itemView){
            super(itemView);
            Log.d(TAG, "");
            nameTextview = (TextView) itemView.findViewById(R.id.goal_name);
            descrTextview = (TextView) itemView.findViewById(R.id.goal_description);
        }
    }
    private Goal[] goalsArray;

    public GoalsAdapter(Goal[] goals){
        goalsArray = goals;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate the custom layout
        View goalView = inflater.inflate(R.layout.goals, parent, false);

        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(goalView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data model based on position
        Goal goal = (Goal) goalsArray[position];

        //Set item views based on your views and data model
        TextView textView = holder.nameTextview;
        textView.setText(goal.getName());

        //If the goal has been achieved, then set it's background to green. Red otherwise.
        if(goal.getAchieved()){
            textView.setBackgroundColor(Color.GREEN);
        }else{
            textView.setBackgroundColor(Color.RED);
        }
        TextView textview = holder.descrTextview;
        textview.setText(goal.getDescription());
    }

    @Override
    public int getItemCount() {
        return goalsArray.length;
    }

}
