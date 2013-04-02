package com.greighamilton.moneymanagement.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.greighamilton.moneymanagement.R;

/**
 * Class for a PasswordDialogPreference object.
 * 
 * @author Greig Hamilton
 *
 */
public class CurrencyDialogPreference extends DialogPreference {

    public CurrencyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);        
        this.setDialogLayoutResource(R.layout.dialog_set_currency);
    }
    
    /**
     * Method called when clicked to create a dialog.
     * 
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        	
        	RadioGroup radioGroup = (RadioGroup)((AlertDialog) dialog).findViewById(R.id.currencySelect);
        	
        	int id = radioGroup.getCheckedRadioButtonId();
        	if (id == -1){
        	    //no item selected
        	}
        	else{
        	    if (id == R.id.dollar) {
        	    	sp.edit().putString("CURRENCYSYMBOL", "$").commit();
        	    }
        	    else if (id == R.id.euro) {
        	    	sp.edit().putString("CURRENCYSYMBOL", "€").commit();
        	    }
        	    else if (id == R.id.pound) {
        	    	sp.edit().putString("CURRENCYSYMBOL", "£").commit();
        	    }
        	    else if (id == R.id.yen) {
        	    	sp.edit().putString("CURRENCYSYMBOL", "¥").commit();
        	    }
        	    else {
        	    	sp.edit().putString("CURRENCYSYMBOL", "£").commit();
        	    }
        	}
        }
    }

}
