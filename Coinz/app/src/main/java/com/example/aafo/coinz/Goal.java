package com.example.aafo.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class Goal {
    private String name;
    private String description;
    private boolean achieved;

    public Goal(String name, String description){
        this.achieved = false;
        this.description = description;
        this.name = name;
    }

    public String getName(){return name;}
    public String getDescription(){return description;}
    public boolean getAchieved(){return achieved;}
    public void setAchieved(boolean bool){achieved=bool;}


    private static String PREF_NAME = "preferences";
    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Goal[] getGoals(Context context){
        Gson gson = new Gson();
        Goal[] empty = new Goal[]{ new Goal("", "")};
        String storedArrayString = getPrefs(context).getString("goals", "");
        java.lang.reflect.Type type = new TypeToken<Goal[]>(){}.getType();
        //If there is no stored array, then return an empty array
        if (storedArrayString.equals("")){
            return empty/*.toArray(new Goal[empty.size()])*/;
        }
        //Transform the string into an array and return it
        Goal[] goals = gson.fromJson(storedArrayString, type);
        return goals/*.toArray(new Goal[goals.size()])*/;
    }
    public static void setGoals(Context context, Goal[] goals){
        Log.d("setGoals", "Committing the goals to the sharedPrefs");
        Gson gson = new Gson();
        String hashGoalsStr = gson.toJson(goals);
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("goals", hashGoalsStr);
        editor.commit();
    }

}
