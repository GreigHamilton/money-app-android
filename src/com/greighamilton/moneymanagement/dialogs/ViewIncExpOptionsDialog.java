package com.greighamilton.moneymanagement.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class ViewIncExpOptionsDialog extends DialogFragment implements OnEditorActionListener {
	
	private DatabaseHelper db;
	private String currentId;
	private String idType;

    public interface OptionsDialogListener {
		void onFinishOptionsDialog(String inputText);
    }
    
    public ViewIncExpOptionsDialog() {
    	// Empty constructor required for DialogFragment
    }

    public ViewIncExpOptionsDialog(String id, String type) {
    	currentId = id;
    	idType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewincexp_options_dialog, container);
        getDialog().setTitle("Options");
        
        // Watch for editButton clicks.
        Button editButton = (Button) view.findViewById(R.id.viewincexp_options_menu_edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
            	Log.i("BUTTON", "Edit clicked");
            	// TODO
            	dismiss();
            }
        });
        
        // Watch for editButton clicks.
        Button deleteButton = (Button) view.findViewById(R.id.viewincexp_options_menu_delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	// When delete is clicked, delete the row using the id from the db
            	
            	db = DatabaseHelper.getInstance(getActivity());
            	if (idType.equalsIgnoreCase("INCOME")) {
            		Log.i("INCOME", "DELETION");
            		db.deleteIncome(currentId);
            	}
            	else if (idType.equalsIgnoreCase("EXPENSE")) {
            		Log.i("EXPENSE", "DELETION");
            		db.deleteExpense(currentId);
            	}
            	
            	dismiss();
            }
        });

        return view;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

    
}