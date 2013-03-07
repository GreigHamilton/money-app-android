package com.greighamilton.moneymanagement.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.util.Password;

public class PasswordDialogPreference extends DialogPreference {

    public PasswordDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);        
        this.setDialogLayoutResource(R.layout.dialog_set_password);
    }
    
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	EditText input = (EditText)((AlertDialog) dialog).findViewById(R.id.passwordText);
        	String password = input.getText().toString();
        	if (password.length() < 4) {
        		Toast.makeText(this.getContext(), "Enter a password with at least 4 characters", Toast.LENGTH_SHORT).show();
        	} else {        	
        		Password.setPassword(this.getContext(), password);
        		Toast.makeText(this.getContext(), "New password set", Toast.LENGTH_SHORT).show();
        	}
        }
    }

}
