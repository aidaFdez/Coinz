package com.example.aafo.coinz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class CheckGoldNews extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        AlertDialog.Builder checkGold = new AlertDialog.Builder(getActivity());
        checkGold.setMessage("For buying news, you need to spend 100 gold. Do you want to buy one?");
        checkGold.setCancelable(true);
        checkGold.setPositiveButton("Yes",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Get the gold and substract 100 if possible, when the wallet is made
                Toast.makeText(getActivity(),
                        "You clicked on YES", Toast.LENGTH_SHORT)
                        .show();
                dialog.cancel();
            }
        });

        checkGold.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Don't do anything
                Toast.makeText(getActivity(),
                        "You clicked on NO", Toast.LENGTH_SHORT)
                        .show();
                dialog.cancel();
            }
        });
        return checkGold.create();
    }
}