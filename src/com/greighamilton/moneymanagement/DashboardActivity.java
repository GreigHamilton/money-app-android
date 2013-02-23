package com.greighamilton.moneymanagement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.external.HintsActivity;
import com.greighamilton.moneymanagement.util.Update;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;
import com.greighamilton.moneymanagement.views.ViewIncExpActivity;
import com.greighamilton.moneymanagement.views.ViewSummaryActivity;
import com.greighamilton.moneymanagement.views.ViewTrendsActivity;

public class DashboardActivity extends Activity {

	private DatabaseHelper db;
	private List<LinearLayout> widgets;
	
	private int selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_dashboard);
		
		Update.doUpdate(this);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
		db = DatabaseHelper.getInstance(this);
		widgets = new ArrayList<LinearLayout>();
		selectedItem = 0;
		
		setUpWidgets();
		
	}

	private void setUpWidgets() {		
		
		// Query upcoming expenses
		Cursor c = db.getExpensesByDate(Util.getTodaysDate(), null,	Util.ASCENDING);
		
		if (!c.moveToFirst()) setContentView(R.layout.activity_dashboard_welcome);
		else {
			setContentView(R.layout.activity_dashboard);

			while (!c.isAfterLast()) {

				LinearLayout widget;

				widget = (LinearLayout) getLayoutInflater().inflate(
						R.layout.widget_countdown, null);
				int colour = Color.parseColor(db.getCategoryColour(c
						.getInt(DatabaseHelper.EXPENSE_CATEGORY_ID)));

				try {
					TextView days = (TextView) widget.findViewById(R.id.days);
					TextView description = (TextView) widget
							.findViewById(R.id.description);
					LinearLayout colourBlock = (LinearLayout) widget
							.findViewById(R.id.colour);

					days.setText(""
							+ Util.daysUntil(c
									.getString(DatabaseHelper.EXPENSE_DATE)));
					description.setText(c
							.getString(DatabaseHelper.EXPENSE_NAME));
					colourBlock.setBackgroundColor(colour);

					widget.setTag("" + c.getInt(DatabaseHelper.EXPENSE_ID));
					widgets.add(widget);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				c.moveToNext();
			}

			GridView grid = (GridView) findViewById(R.id.grid_of_widgets);
			grid.setAdapter(new WidgetAdapter(this, widgets));
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

		case R.id.dashboard_menu_summary:
			i = new Intent(DashboardActivity.this,
					ViewSummaryActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.dashboard_menu_viewtrends:
			i = new Intent(DashboardActivity.this,
					ViewTrendsActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.dashboard_menu_viewincexp:
			i = new Intent(DashboardActivity.this,
					ViewIncExpActivity.class);
			DashboardActivity.this.startActivity(i);
			break;
			
		case R.id.dashboard_menu_viewhints:
			i = new Intent(DashboardActivity.this,
					HintsActivity.class);
			DashboardActivity.this.startActivity(i);
			break;
			
		case R.id.viewincexp_menu_addincome:
			i = new Intent(DashboardActivity.this, AddIncomeActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.viewincexp_menu_addexpense:
			i = new Intent(DashboardActivity.this, AddExpenseActivity.class);
			DashboardActivity.this.startActivity(i);
			break;
			
		case R.id.dashboard_menu_feedback:
			i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_EMAIL, "greigyboi@gmail.com");
			i.putExtra(Intent.EXTRA_SUBJECT, "Money Management Evaluation Feedback");
			i.putExtra(Intent.EXTRA_TEXT, "What is going well: " + '\n' + '\n' + '\n' +
					"What I'm having problems with: " + '\n' + '\n' + '\n' +
					"What I like: " + '\n' + '\n' + '\n' +
					"What I would change: " + '\n' + '\n' + '\n' +
					"Other comments: " + '\n');

			startActivity(Intent.createChooser(i, "Send Feedback"));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void clickWidget(View v) {
		String expenseID = (String) v.getTag();
		selectedItem = Integer.parseInt(expenseID);
		startActionMode(mActionModeCallback);
	}
	
	public void clickIncExp(View v) {
		Intent i = new Intent(this, ViewIncExpActivity.class);
		startActivity(i);
	}

	public class WidgetAdapter extends BaseAdapter {

		private Context context;
		private List<LinearLayout> widgets;

		// Constructor
		public WidgetAdapter(Context c, List<LinearLayout> w) {
			context = c;
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

	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_incexp, menu);			
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
	            	i = new Intent(DashboardActivity.this, AddExpenseActivity.class);
	            	i.putExtra("CURRENT_ID", ""+selectedItem);
	            	startActivity(i);
	            	mode.finish();
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
	    	mode = null;
	    }
	};
	
	protected void showDeleteDialog() {

    	new AlertDialog.Builder(DashboardActivity.this)
        .setTitle("Delete")
        .setMessage("Are you sure you want to delete this?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {            	
        		if (db.getExpenseRepetitionPeriod(""+selectedItem) != 0) {
            		showDeleteSeriesDialog();
            	} else {
            		db.deleteExpense(""+selectedItem);
            		setUpWidgets();
            		Toast.makeText(DashboardActivity.this, "Expense item deleted", Toast.LENGTH_SHORT).show();
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
	
	protected void showDeleteSeriesDialog() {

    	new AlertDialog.Builder(DashboardActivity.this)
        .setTitle("Delete Series")
        .setMessage("Delete the whole series, or just this one?")
        .setPositiveButton("Whole Series", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Delete series
            	db.deleteExpenseSeries(db.getExpenseSeriesID(""+selectedItem));
               	setUpWidgets();
            	Toast.makeText(DashboardActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();     		
            	}
         })
        .setNeutralButton("Just This", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	    db.deleteExpense(""+selectedItem);
            		setUpWidgets();
            		Toast.makeText(DashboardActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();
            }
         })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// Do nothing
            }
         })
         .show();    	
	}
}
