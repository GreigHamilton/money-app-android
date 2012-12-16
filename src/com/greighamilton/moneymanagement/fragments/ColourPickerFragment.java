package com.greighamilton.moneymanagement.fragments;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.R.array;
import com.greighamilton.moneymanagement.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ColourPickerFragment extends DialogFragment {
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.pick_color);
	    builder.setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	           }
	    });
	    return builder.create();
	}
}