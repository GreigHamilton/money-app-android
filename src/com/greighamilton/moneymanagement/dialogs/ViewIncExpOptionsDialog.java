package com.greighamilton.moneymanagement.dialogs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.greighamilton.moneymanagement.entry.AddExpenseActivity;
import com.greighamilton.moneymanagement.entry.AddIncomeActivity;

/**
 * Class for an inc exp dialog.
 * 
 * @author Greig Hamilton
 *
 */
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
        View view = inflater.inflate(R.layout.fragment_incexp_list_options_dialog, container);
        getDialog().setTitle("Options");
        
        // Watch for editButton clicks.
        Button editButton = (Button) view.findViewById(R.id.viewincexp_options_menu_edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
            	dismiss();
            	
            	if (idType.equalsIgnoreCase("INCOME")) {
            		Intent i = new Intent(v.getContext(), AddIncomeActivity.class);
                	i.putExtra("CURRENT_ID", currentId);
                	startActivity(i);
            	}
            	else if (idType.equalsIgnoreCase("EXPENSE")) {
            		Intent i = new Intent(v.getContext(), AddExpenseActivity.class);
                	i.putExtra("CURRENT_ID", currentId);
                	startActivity(i);
            	}
            }
        });
        
        // Watch for deleteButton clicks.
        Button deleteButton = (Button) view.findViewById(R.id.viewincexp_options_menu_delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// When delete is clicked, delete the row using the id from the db
            	
            	db = DatabaseHelper.getInstance(getActivity());
            	if (idType.equalsIgnoreCase("INCOME")) {
            		db.deleteIncome(currentId);
            	}
            	else if (idType.equalsIgnoreCase("EXPENSE")) {
            		db.deleteExpense(currentId);
            	}

            	dismiss();
            }
        });

        return view;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return false;
	}
}