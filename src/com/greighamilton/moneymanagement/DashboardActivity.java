package com.greighamilton.moneymanagement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddExpenseActivity;
import com.greighamilton.moneymanagement.entry.AddIncomeActivity;
import com.greighamilton.moneymanagement.external.HintsTipsActivity;
import com.greighamilton.moneymanagement.util.Password;
import com.greighamilton.moneymanagement.util.SettingsActivity;
import com.greighamilton.moneymanagement.util.Update;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;

/**
 * Dashboard Activity class.
 * 
 * @author Greig Hamilton
 *
 */
public class DashboardActivity extends Activity implements ActionBar.OnNavigationListener {
	
	private boolean unlocked = false;

	private static final int TYPE = 0;
	private static final int ID = 1;
	private static final int DATE = 2;
	
	private static final int INCOME = 0;
	private static final int EXPENSE = 1;
	
	private DatabaseHelper db;
	private List<LinearLayout> widgets;
	
	private LinearLayout selectedWidget;
	private int selectedID;
	private int selectedType;
	
	private OnNavigationListener mOnNavigationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_dashboard);		
		
		setUpActionBar();
		Update.doUpdate(this);
		
		// password check
		if (Password.isPasswordProtected(this) && getIntent().getBooleanExtra("PASSWORD", true)) requestPassword();
	}
	

	/**
	 * Method to get the required password from the user, if a password has been set.
	 * 
	 */
	private void requestPassword() {
		
		// build dialog box
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Password");
		alert.setMessage("Please enter your password to continue using Money Management.");
		alert.setCancelable(false);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String guess = input.getText().toString();
			if (!Password.isPasswordCorrect(DashboardActivity.this, guess)) {
				Toast.makeText(DashboardActivity.this, "Incorrect, please try again", Toast.LENGTH_SHORT).show();
				requestPassword();
			} else {
				unlocked = true;
				Toast.makeText(DashboardActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
			}
		}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			finish();
		}
		});

		alert.show();
		
	}
	
	/**
	 * Method to set up the action bar for the interface.
	 * 
	 */
	private void setUpActionBar() {
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setTitle(null);
		
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.incexp_views_0,
				R.layout.spinner_item_navigator);
		
		mOnNavigationListener = new OnNavigationListener() {
			  @Override
			  public boolean onNavigationItemSelected(int position, long itemId) {
				  Intent i;
				  switch (position) {
				  case 0:	break;
				  case 1:	i = new Intent(DashboardActivity.this, IncExpVisualiserActivity.class);
				  			startActivity(i);
				  			DashboardActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				  			finish();
				  			break;
				  case 2:	i = new Intent(DashboardActivity.this, IncExpTrendsActivity.class);
				  			startActivity(i);
				  			DashboardActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				  			finish();
				  			break;
				  case 3:	i = new Intent(DashboardActivity.this, IncExpListActivity.class);
		  					startActivity(i);
		  					DashboardActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				  			finish();
				  			break;
				  default:	break;
				  }
				  return true;
			  }
		};
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);		
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    
		db = DatabaseHelper.getInstance(this);
		widgets = new ArrayList<LinearLayout>();
		selectedWidget = null;
		selectedID = 0;
		
		setUpWidgets();
		
	}

	/**
	 * Method to set up the dashboard widgets --- built dynamically from the database.
	 * 
	 */
	private void setUpWidgets() {
		
		widgets.clear();
		List<LinearLayout> incWidgets = new ArrayList<LinearLayout>();
		List<LinearLayout> expWidgets = new ArrayList<LinearLayout>();
		
		// Query upcoming expenses
		Cursor expCursor = db.getExpensesByDate(Util.getTodaysDate(), null,	Util.ASCENDING);
		Cursor incCursor = db.getIncomeByDate(Util.getTodaysDate(), null, Util.ASCENDING);
		
		expCursor.moveToFirst();
		incCursor.moveToFirst();
		
		if (expCursor.isAfterLast() && incCursor.isAfterLast()) setContentView(R.layout.activity_dashboard_welcome);
		else {
			setContentView(R.layout.activity_dashboard);

			while (!expCursor.isAfterLast()) {
				LinearLayout widget;

				widget = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_countdown, null);
				int colour = Color.parseColor(db.getCategoryColour(expCursor.getInt(DatabaseHelper.EXPENSE_CATEGORY_ID)));

				try {
					TextView days = (TextView) widget.findViewById(R.id.days);
					TextView description = (TextView) widget.findViewById(R.id.description);
					LinearLayout colourBlock = (LinearLayout) widget.findViewById(R.id.colour);

					days.setText(""+ Util.daysUntil(expCursor.getString(DatabaseHelper.EXPENSE_DATE)));
					description.setText(expCursor.getString(DatabaseHelper.EXPENSE_NAME));
					colourBlock.setBackgroundColor(colour);

					widget.setTag(R.id.type, "EXPENSE");
					widget.setTag(R.id.id, "" + expCursor.getInt(DatabaseHelper.EXPENSE_ID));
					widget.setTag(R.id.date, ""+ expCursor.getString(DatabaseHelper.EXPENSE_DATE));
					expWidgets.add(widget);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				expCursor.moveToNext();
			}
			
			while (!incCursor.isAfterLast()) {
				LinearLayout widget;

				widget = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_countdown, null);
				int colour = Color.parseColor(db.getCategoryColour(incCursor.getInt(DatabaseHelper.INCOME_CATEGORY_ID)));

				try {
					TextView days = (TextView) widget.findViewById(R.id.days);
					TextView description = (TextView) widget.findViewById(R.id.description);
					LinearLayout colourBlock = (LinearLayout) widget.findViewById(R.id.colour);

					days.setText(""+ Util.daysUntil(incCursor.getString(DatabaseHelper.INCOME_DATE)));
					description.setText(incCursor.getString(DatabaseHelper.INCOME_NAME));
					colourBlock.setBackgroundColor(colour);

					widget.setTag(R.id.type, "INCOME");
					widget.setTag(R.id.id, ""+ incCursor.getInt(DatabaseHelper.INCOME_ID));
					widget.setTag(R.id.date, ""+ incCursor.getString(DatabaseHelper.INCOME_DATE));
					incWidgets.add(widget);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				incCursor.moveToNext();
			}
			
			widgets = mergeWidgets(incWidgets, expWidgets);
			
			GridView grid = (GridView) findViewById(R.id.grid_of_widgets);
			if (grid.getAdapter() == null) {
				grid.setAdapter(new WidgetAdapter(widgets));
			} else {
				((WidgetAdapter) grid.getAdapter()).setWidgets(widgets);
				grid.invalidateViews();
			}			
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch (item.getItemId()) {

		case R.id.dashboard_menu_viewgoals:
			i = new Intent(DashboardActivity.this,
					ViewGoalsActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.dashboard_menu_viewhints:
			i = new Intent(DashboardActivity.this,
					HintsTipsActivity.class);
			DashboardActivity.this.startActivity(i);
			break;
			
		case R.id.dashboard_menu_addincome:
			i = new Intent(DashboardActivity.this, AddIncomeActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.dashboard_menu_addexpense:
			i = new Intent(DashboardActivity.this, AddExpenseActivity.class);
			DashboardActivity.this.startActivity(i);
			break;
			
		case R.id.dashboard_menu_password:
			i = new Intent(DashboardActivity.this, SettingsActivity.class);
			DashboardActivity.this.startActivity(i);
			break;			
//			setPassword();			
			
		}		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Method to set a new password to be stored in SharedPreferences.
	 * 
	 */
	private void setPassword() {
		
		AlertDialog.Builder setPassword = new AlertDialog.Builder(this);

		setPassword.setTitle("Set Password");
		setPassword.setMessage("Enter your new password:");
		setPassword.setCancelable(false);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		setPassword.setView(input);

		setPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			if (input.getText().toString().length() < 4) {
				Toast.makeText(DashboardActivity.this, "Enter a password with at least 4 characters", Toast.LENGTH_SHORT).show();
				setPassword();
			} else {
				Password.setPassword(DashboardActivity.this, input.getText().toString());
			}
		}
		});
		
		if (Password.isPasswordProtected(DashboardActivity.this)) {		
			setPassword.setNegativeButton("Remove Password", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Password.setPassword(DashboardActivity.this, null);
				Toast.makeText(DashboardActivity.this, "Password removed", Toast.LENGTH_SHORT).show();
			}
			});		
		}

		setPassword.show();
		
	}
	
	/**
	 * Method to confirm the password entered by the user.
	 * 
	 */
	private void confirmPassword() {
		
		AlertDialog.Builder confirmPassword = new AlertDialog.Builder(this);

		confirmPassword.setTitle("Set Password");
		confirmPassword.setMessage("Enter your new password:");
		confirmPassword.setCancelable(false);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		confirmPassword.setView(input);

		confirmPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
//			if (pass1.getText().toString().equals(pass2.getText().toString())) {
//				Password.setPassword(DashboardActivity.this, pass1.getText().toString());
//			} else {
//				Toast.makeText(DashboardActivity.this, "Passwords not the same", Toast.LENGTH_SHORT).show();
//			}
			// Do something with value!
		}
		});

		confirmPassword.show();
		
	}
	
	/**
	 * Method for when the user clicks on a widget on the Dashboard interface.
	 * 
	 * @param v		the current view
	 */
	public void clickWidget(View v) {
		
		// if a widget is already selected - 'deselect it'
		if (selectedWidget != null) {
			selectedWidget.setBackgroundColor(getResources().getColor(R.color.grey1));
		}
		
		// get info from widget view
		selectedWidget = (LinearLayout) v;
		String type = (String) selectedWidget.getTag(R.id.type);
		String id = (String) selectedWidget.getTag(R.id.id);
		
		selectedType = (type.equals("INCOME") ? INCOME : EXPENSE);
		selectedID = Integer.parseInt(id);
		startActionMode(mActionModeCallback);
	}
	
	public void clickIncExp(View v) {
		Intent i = new Intent(this, IncExpListActivity.class);
		startActivity(i);
	}
	
	/**
	 * Method to merge the income and expense widgets for the dashboard interface.
	 * Required since version 1.1 of the app.
	 * 
	 * @param incomes		List of LinearLayouts for the income widgets.
	 * @param expenses		List of LinearLayouts for the expense widgets.
	 * @return				List of LinearLayout objects
	 */
	private List<LinearLayout> mergeWidgets(List<LinearLayout> incomes, List<LinearLayout> expenses) {
		
		List<LinearLayout> theWidgets = new ArrayList<LinearLayout>();
		
		int incIndex = 0;
		int expIndex = 0;
		
		// Add incomes/expenses in order to one list
		while (incIndex < incomes.size() & expIndex < expenses.size()) {
			
			String incomeDate = (String) incomes.get(incIndex).getTag(R.id.date);
			String expenseDate = (String) expenses.get(expIndex).getTag(R.id.date);
			
			if (incomeDate.compareTo(expenseDate) <= 0) {
				theWidgets.add(incomes.get(incIndex));
				incIndex++;
			} else {
				theWidgets.add(expenses.get(expIndex));
				expIndex++;
			}	
			
		}
		
		// Add any excess incomes/expenses to the end
		while (incIndex < incomes.size()) {
			theWidgets.add(incomes.get(incIndex));
			incIndex++;
		}
		while (expIndex < expenses.size()) {
			theWidgets.add(expenses.get(expIndex));
			expIndex++;
		}
		
		return theWidgets;
		
	}

	/**
	 * Private class for a WidgetAdapter object for use of the widgets on the dashboard.
	 * 
	 * @author Greig Hamilton
	 *
	 */
	public class WidgetAdapter extends BaseAdapter {
		
		private List<LinearLayout> widgets;

		// Constructor (needs sorted lists)
		public WidgetAdapter(List<LinearLayout> w) {
			widgets = w;
		}

		@Override
		public int getCount() {
			return widgets.size();
		}

		@Override
		public Object getItem(int position) {
			return widgets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return widgets.get(position);
		}
		
		public void setWidgets(List<LinearLayout> w) {
			widgets = w;
		}

	}
	
	/**
	 * Method called on create of the adapter.
	 * 
	 */
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_incexp, menu);
			TextView description = (TextView) (selectedWidget.findViewById(R.id.description));
			description.setSelected(true); // start marquee			
			selectedWidget.setBackgroundColor(getResources().getColor(R.color.blue2));
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Intent i;
			
			// Get selected item ID
			switch (item.getItemId()) {

			// Edit clicked
			case R.id.context_incexp_edit:
				if (selectedType == INCOME) {
					i = new Intent(DashboardActivity.this, AddIncomeActivity.class);
					i.putExtra("CURRENT_ID", "" + selectedID);
					startActivity(i);
					mode.finish();
				} else {
					i = new Intent(DashboardActivity.this, AddExpenseActivity.class);
					i.putExtra("CURRENT_ID", "" + selectedID);
					startActivity(i);
					mode.finish();
				}
				return true;

				// Delete clicked
			case R.id.context_incexp_delete:
				showDeleteDialog();
				mode.finish();
				return true;

			default:
				mode.finish();
				return false;
			}
	    }
	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
			selectedWidget.setBackgroundColor(getResources().getColor(R.color.grey1));
	    	mode = null;
	    }
	};
	
	/**
	 * Method to show the delete dialog.
	 * 
	 */
	protected void showDeleteDialog() {

    	new AlertDialog.Builder(DashboardActivity.this)
        .setTitle("Delete")
        .setMessage("Are you sure you want to delete this?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (selectedType == INCOME) {
            		if (db.getIncomeRepetitionPeriod(""+selectedID) != 0) {
                		showDeleteSeriesDialog();
                	} else {
                		db.deleteIncome(""+selectedID);
                		setUpWidgets();
                		Toast.makeText(DashboardActivity.this, "Income item deleted", Toast.LENGTH_SHORT).show();
                	}
            	} else {
	        		if (db.getExpenseRepetitionPeriod(""+selectedID) != 0) {
	            		showDeleteSeriesDialog();
	            	} else {
	            		db.deleteExpense(""+selectedID);
	            		setUpWidgets();
	            		Toast.makeText(DashboardActivity.this, "Expense item deleted", Toast.LENGTH_SHORT).show();
	            	}
            	}
            }
         })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
         .show();
	}
	
	/**
	 * Method to show the delete series dialog.
	 * 
	 */
	protected void showDeleteSeriesDialog() {

    	new AlertDialog.Builder(DashboardActivity.this)
        .setTitle("Delete Series")
        .setMessage("Delete the whole series, or just this one?")
        .setPositiveButton("Whole Series", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Delete series
            	if (selectedType == INCOME) {
                	db.deleteIncomeSeries(db.getIncomeSeriesID(""+selectedID));
                   	setUpWidgets();
                	Toast.makeText(DashboardActivity.this, "Income series deleted", Toast.LENGTH_SHORT).show();
            	} else {
                	db.deleteExpenseSeries(db.getExpenseSeriesID(""+selectedID));
                   	setUpWidgets();
                	Toast.makeText(DashboardActivity.this, "Expense series deleted", Toast.LENGTH_SHORT).show();
            	}
            }
         })
        .setNeutralButton("Just This", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (selectedType == INCOME) {
            	    db.deleteIncome(""+selectedID);
            		setUpWidgets();
            		Toast.makeText(DashboardActivity.this, "Income item deleted", Toast.LENGTH_SHORT).show();
            	} else {
            	    db.deleteExpense(""+selectedID);
            		setUpWidgets();
            		Toast.makeText(DashboardActivity.this, "Expense item deleted", Toast.LENGTH_SHORT).show();
            	}
            }
         })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// Do nothing
            }
         })
         .show();    	
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
}
