package com.greighamilton.moneymanagement.views;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;

public class ViewSummaryActivity extends Activity {

	private static int TYPE_INCOME = 0;
	private static int TYPE_EXPENSE = 1;
	
	private DatabaseHelper db;

	private List<Integer> incomes;
	private List<Integer> expenses;

	Spinner monthSpinner;
	Spinner yearSpinner;

	private int monthReq;
	private int yearReq;
	
	private int selectedType;				// TYPE_INCOME or TYPE_EXPENSE
	private String selectedItem;			// income or expense ID
	private LinearLayout selectedView;		// selected block

	private int[] greens = new int[] { R.color.green1, R.color.green2,
			R.color.green3, R.color.green4, R.color.green5, R.color.green6 };
	private int[] reds = new int[] { R.color.red1, R.color.red2, R.color.red3,
			R.color.red4, R.color.red5, R.color.red6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewsummary);

		db = DatabaseHelper.getInstance(this);

		// start-up only show current month only
		monthReq = Util.getTodaysMonth();
		yearReq = Util.getTodaysYear();
		
		setSpinnerContent();
		setUpVisualisation();
	}

	private void setSpinnerContent() {
		
		// get current month and year
		int month = Util.getTodaysMonth();
		int year = Util.getTodaysYear();
		String yearText = Util.makeYearString(year);

		// Spinner for months
		monthSpinner = (Spinner) findViewById(R.id.incexp_month);
		List<String> months = Util.getListOfMonthsShort();

		ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, months);
		monthAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		monthSpinner.setAdapter(monthAdapter);
		monthSpinner.setSelection(month-1); // zero based indexing
		monthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				monthReq = position + 1;
				setUpVisualisation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		// Spinner for months
		yearSpinner = (Spinner) findViewById(R.id.incexp_year);
		final List<String> years = Util.getListOfYears();
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, years);
		yearAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(yearAdapter);
		yearSpinner.setSelection(years.indexOf(yearText));
		yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				yearReq = Integer.parseInt(years.get(position));
				setUpVisualisation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
	}

	private void setUpVisualisation() {
		
		String month = Util.makeMonthString(monthReq);
		String year = Util.makeYearString(yearReq);

		// Initialise data structures
		incomes = new LinkedList<Integer>();
		expenses = new LinkedList<Integer>();
		int totalIncome = 0;
		int totalExpenses = 0;
		LinearLayout incomeLayout = (LinearLayout) findViewById(R.id.summary_income);
		LinearLayout expensesLayout = (LinearLayout) findViewById(R.id.summary_expenses);

		// Get Incomes
		Cursor incomeCursor = db.getIncomeByAmount(month, year, false);
		incomeCursor.moveToFirst();
		while (!incomeCursor.isAfterLast()) {
			incomes.add(incomeCursor.getInt(DatabaseHelper.INCOME_ID));
			incomeCursor.moveToNext();
		}
		incomeCursor.close();

		for (Integer i : incomes) {
			Cursor c = db.getIncomeId("" + i);
			c.moveToFirst();
			totalIncome += c.getInt(DatabaseHelper.INCOME_AMOUNT);
			c.close();
		}
		((TextView) findViewById(R.id.income_total)).setText("IN £"
				+ totalIncome);

		// Get Expenses
		Cursor expensesCursor = db.getExpensesByAmount(month, year, false);
		expensesCursor.moveToFirst();
		while (!expensesCursor.isAfterLast()) {
			expenses.add(expensesCursor.getInt(DatabaseHelper.EXPENSE_ID));
			expensesCursor.moveToNext();
		}
		expensesCursor.close();

		for (Integer i : expenses) {
			Cursor c = db.getExpenseId("" + i);
			c.moveToFirst();
			totalExpenses += c.getInt(DatabaseHelper.EXPENSE_AMOUNT);
			c.close();
		}
		((TextView) findViewById(R.id.expenses_total)).setText("OUT £"
				+ totalExpenses);
		
		// Disposable Income
		LinearLayout extraBlock = new LinearLayout(this);
		int disposableIncome = totalIncome - totalExpenses;
		
		incomeLayout.removeAllViews();
		expensesLayout.removeAllViews();
		if (disposableIncome > 0) {
			extraBlock.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, disposableIncome));
			extraBlock.setBackgroundColor(getResources().getColor(R.color.Green));
			extraBlock.setGravity(Gravity.CENTER);
			
			TextView t = new TextView(this);
			t.setText("Disposable Income - £" + disposableIncome);
			t.setTypeface(Typeface.DEFAULT_BOLD);
			
			LinearLayout whiteBlock = new LinearLayout(this);
			whiteBlock.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, disposableIncome));
			whiteBlock.setBackgroundColor(getResources().getColor(R.color.white));
			whiteBlock.setGravity(Gravity.CENTER);
			
			whiteBlock.addView(t);
			extraBlock.addView(whiteBlock);
			extraBlock.setPadding(10, 0, 0, 0);
			
			expensesLayout.addView(extraBlock);
			incomeLayout.setWeightSum(totalIncome);
			expensesLayout.setWeightSum(totalIncome); // same as income, not expenses
		} else {
			// Overspending
			int weight = -disposableIncome;
			extraBlock.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, weight));
			extraBlock.setBackgroundColor(getResources().getColor(R.color.Red));
			extraBlock.setGravity(Gravity.CENTER);
			
			TextView t = new TextView(this);
			t.setText("Overspending - £" + -disposableIncome);
			t.setTypeface(Typeface.DEFAULT_BOLD);
			
			LinearLayout whiteBlock = new LinearLayout(this);
			whiteBlock.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, weight));
			whiteBlock.setBackgroundColor(getResources().getColor(R.color.white));
			whiteBlock.setGravity(Gravity.CENTER);
			
			whiteBlock.addView(t);
			extraBlock.addView(whiteBlock);
			extraBlock.setPadding(0, 0, 10, 0);
			
			incomeLayout.addView(extraBlock);
			expensesLayout.setWeightSum(totalExpenses);
			incomeLayout.setWeightSum(totalExpenses); // same as expenses, not income
		}
		
		// Income
		incomeLayout.setPadding(10, 10, 10, 10);
		for (int i = 0; i < incomes.size(); i++) {
			
			// Get from db
			final String incomeID = "" + incomes.get(i);
			Cursor c = db.getIncomeId(incomeID);
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.INCOME_AMOUNT);
			String name = c.getString(DatabaseHelper.INCOME_NAME);
			String categoryColour = db.getCategoryColour(c.getInt(DatabaseHelper.INCOME_CATEGORY_ID));
			c.close();
			
			// Make UI block
			final LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					amount));
			block.setBackgroundColor(Color.parseColor(categoryColour));
			block.setTag(categoryColour);
			block.setGravity(Gravity.CENTER);
			block.setPadding(0, 0, 0, 5);
			TextView text = new TextView(this);
			text.setText(name + " - £" + amount);
			block.addView(text);
			block.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   
					   // Reset previously selected view
					   if (selectedView != null) {
						   String originalColor = (String) selectedView.getTag();
						   selectedView.setBackgroundColor(Color.parseColor(originalColor));
					   }
					   
					   // Set currently selected view and start context action
					   selectedType = TYPE_INCOME;
					   selectedItem = incomeID;
					   selectedView = block;
					   startActionMode(mActionModeCallback);
				   }
				});
			incomeLayout.addView(block);
		}

		// Expenses
		expensesLayout.setPadding(10, 10, 10, 10);
		for (int i = 0; i < expenses.size(); i++) {
			
			// Get from db
			final String expenseID = "" + expenses.get(i);
			Cursor c = db.getExpenseId(expenseID);
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.EXPENSE_AMOUNT);
			String name = c.getString(DatabaseHelper.EXPENSE_NAME);
			String categoryColour = db.getCategoryColour(c.getInt(DatabaseHelper.EXPENSE_CATEGORY_ID));
			
			// Make UI block
			final LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					amount));
			block.setBackgroundColor(Color.parseColor(categoryColour));
			block.setTag(categoryColour);
			block.setGravity(Gravity.CENTER);
			TextView text = new TextView(this);
			text.setText(name + " - £" + amount);
			block.addView(text);
			block.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   
					   // Reset previously selected view
					   if (selectedView != null) {
						   String originalColor = (String) selectedView.getTag();
						   selectedView.setBackgroundColor(Color.parseColor(originalColor));
					   }
					   
					   // Set currently selected view and start context action
					   selectedType = TYPE_EXPENSE;
					   selectedItem = expenseID;
					   selectedView = block;
					   startActionMode(mActionModeCallback);
				   }
				});
			expensesLayout.addView(block);
			c.close();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.viewsummary_menu_addincome:
			Intent i = new Intent(ViewSummaryActivity.this,
					AddIncomeActivity.class);
			ViewSummaryActivity.this.startActivity(i);
			break;

		case R.id.viewsummary_menu_addexpense:
			Intent j = new Intent(ViewSummaryActivity.this,
					AddExpenseActivity.class);
			ViewSummaryActivity.this.startActivity(j);
			break;

		case R.id.viewsummary_menu_viewtrends:
			Intent l = new Intent(ViewSummaryActivity.this,
					ViewTrendsActivity.class);
			ViewSummaryActivity.this.startActivity(l);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void showDeleteDialog() {

    	new AlertDialog.Builder(ViewSummaryActivity.this)
        .setTitle("Delete")
        .setMessage("Are you sure you want to delete this?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
            	// Continue with delete
            	if (selectedType == TYPE_INCOME) {
            		if (db.getIncomeRepetitionPeriod(selectedItem) != 0) {
                		showDeleteSeriesDialog();
                	} else {
                		db.deleteIncome(selectedItem);
        				setUpVisualisation();
                		Toast.makeText(ViewSummaryActivity.this, "Income item deleted", Toast.LENGTH_SHORT).show();
                	}            		
            	} else {
            		if (db.getExpenseRepetitionPeriod(selectedItem) != 0) {
                		showDeleteSeriesDialog();
                	} else {
                		db.deleteExpense(selectedItem);
        				setUpVisualisation();
                		Toast.makeText(ViewSummaryActivity.this, "Expense item deleted", Toast.LENGTH_SHORT).show();
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
	
	protected void showDeleteSeriesDialog() {

    	new AlertDialog.Builder(ViewSummaryActivity.this)
        .setTitle("Delete Series")
        .setMessage("Delete the whole series, or just this one?")
        .setPositiveButton("Whole Series", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Delete series
            	if (selectedType == TYPE_INCOME) {
               		db.deleteIncomeSeries(db.getIncomeSeriesID(selectedItem));
        			setUpVisualisation();
                	Toast.makeText(ViewSummaryActivity.this, "Income items deleted", Toast.LENGTH_SHORT).show();
            	} else {
               		db.deleteExpenseSeries(db.getExpenseSeriesID(selectedItem));
            		setUpVisualisation();
            		Toast.makeText(ViewSummaryActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();     		
            	}
            }
         })
        .setNeutralButton("Just This", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// Just delete this
            	if (selectedType == TYPE_INCOME) {
               		db.deleteIncome(selectedItem);
        			setUpVisualisation();
                	Toast.makeText(ViewSummaryActivity.this, "Income items deleted", Toast.LENGTH_SHORT).show();
            	} else {
            		db.deleteExpense(selectedItem);
            		setUpVisualisation();
            		Toast.makeText(ViewSummaryActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();     		
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

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_incexp, menu);
		    selectedView.setBackgroundColor(getResources().getColor(R.color.blue1));
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
	            	if (selectedType == TYPE_INCOME) {
					    i = new Intent(ViewSummaryActivity.this, AddIncomeActivity.class);
					    i.putExtra("CURRENT_ID", selectedItem);
					    ViewSummaryActivity.this.startActivity(i);
	            	} else {
					    i = new Intent(ViewSummaryActivity.this, AddExpenseActivity.class);
					    i.putExtra("CURRENT_ID", selectedItem);
					    ViewSummaryActivity.this.startActivity(i);
	            	} return true;
	            
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
			   String originalColor = (String) selectedView.getTag();
			   selectedView.setBackgroundColor(Color.parseColor(originalColor));	    	
	    }
	};    
}