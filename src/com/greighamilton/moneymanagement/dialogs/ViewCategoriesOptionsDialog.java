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
import com.greighamilton.moneymanagement.entry.AddCategoryActivity;

/**
 * Class for creating a categories dialog.
 * 
 * @author Greig Hamilton
 *
 */
public class ViewCategoriesOptionsDialog extends DialogFragment implements OnEditorActionListener {
	
	private DatabaseHelper db;
	private String currentId;

    public interface OptionsDialogListener {
		void onFinishOptionsDialog(String inputText);
    }
    
    public ViewCategoriesOptionsDialog() {
    	// Empty constructor required for DialogFragment
    }

    public ViewCategoriesOptionsDialog(String id) {
    	currentId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewcategories_options_dialog, container);
        getDialog().setTitle("Options");
        
        // Watch for editButton clicks.
        Button editButton = (Button) view.findViewById(R.id.viewcategories_options_menu_edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
            	// TODO
            	Intent i = new Intent(v.getContext(), AddCategoryActivity.class);
            	i.putExtra("CURRENT_ID", currentId);
            	startActivity(i);
            	
            	dismiss();
            }
        });
        
        // Watch for editButton clicks.
        Button deleteButton = (Button) view.findViewById(R.id.viewcategories_options_menu_delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	// When delete is clicked, delete the row using the id from the db
            	
            	db = DatabaseHelper.getInstance(getActivity());
            	db.deleteCategory(currentId);
            	
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