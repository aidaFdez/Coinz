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


public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder>{

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextview;
        TextView descrTextview;

        ViewHolder(View itemView){
            super(itemView);
            String TAG = "GoalsAdapter";
            Log.d(TAG, "");
            nameTextview = itemView.findViewById(R.id.goal_name);
            descrTextview = itemView.findViewById(R.id.goal_description);
        }
    }
    private Goal[] goalsArray;

    GoalsAdapter(Goal[] goals){
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
        return new ViewHolder(goalView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data model based on position
        Goal goal = goalsArray[position];

        //Set item views based on your views and data model
        TextView textView = holder.nameTextview;
        textView.setText(goal.getName());

        //If the goal has been achieved, then set it's background to green. Red otherwise.
        if(goal.getAchieved()){
            textView.setBackgroundColor(Color.parseColor("#0F9F5F"));
        }else{
            textView.setBackgroundColor(Color.parseColor("#EC3B3B"));
        }
        TextView textview = holder.descrTextview;
        textview.setText(goal.getDescription());
    }

    @Override
    public int getItemCount() {
        return goalsArray.length;
    }

}
