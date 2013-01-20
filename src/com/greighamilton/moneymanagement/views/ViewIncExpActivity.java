package com.greighamilton.moneymanagement.views;

import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.adapters.ExpenseListAdapter;
import com.greighamilton.moneymanagement.adapters.IncomeListAdapter;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;



public class ViewIncExpActivity extends ListActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int TAB_INCOME = 0;
    private static final int TAB_EXPENSES = 1;
    
//    private IncomeListFragment incomeFragment;
//    private ExpenseListFragment expenseFragment;
    
    private DatabaseHelper db;
    private Cursor c;
    
    private int selectedTab;    
//    private boolean allDatesSelected = false;
//    private boolean allCategoriesSelected = true;    
//    private int selectedCategory = -1;
//    private int selectedMonth = Util.getTodaysMonth();
//    private int selectedYear = Util.getTodaysYear();

    private CheckBox allDatesCheckBox;
    private CheckBox allCategoriesCheckBox;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner categorySpinner;
    
    private List<String> listOfCategories;
    private List<Integer> listOfCategoryIDs;
    private List<String> listOfMonths;
    private List<String> listOfYears;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_viewincexp);        

        db = DatabaseHelper.getInstance(this);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("Income").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Expenses").setTabListener(this));
        selectedTab = TAB_INCOME;
        
    	setUpList();        
    }

	private void setUpList() {
		
		// Check initial states of spinners and checkboxes
		if (allDatesCheckBox == null || allCategoriesCheckBox == null) initCheckboxState();
		if (monthSpinner == null || yearSpinner == null || categorySpinner == null) initSpinnerState();
		
		// Get selection status
		String month = Util.makeMonthString(monthSpinner.getSelectedItemPosition()+1);
		String year = (String) yearSpinner.getSelectedItem();
		int category = listOfCategoryIDs.get(categorySpinner.getSelectedItemPosition());
		boolean allDates = allDatesCheckBox.isChecked();
		boolean allCategories = allCategoriesCheckBox.isChecked();
		
		// Query db and set list adapter
    	if (selectedTab == TAB_INCOME) {
    		c = db.getSpecifiedIncome(month, year, category, allDates, allCategories);
    		setListAdapter(new IncomeListAdapter(this, c));    		
    	} else {
    		c = db.getSpecifiedExpenses(month, year, category, allDates, allCategories);
    		setListAdapter(new ExpenseListAdapter(this, c));
    	}
    	
	}
	
	private void initCheckboxState() {		
		
		// Set up check boxes
    	allCategoriesCheckBox = (CheckBox) findViewById(R.id.incexp_all_categories);
    	allDatesCheckBox = (CheckBox) findViewById(R.id.incexp_all_categories);
    	
    	allDatesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setUpList();
				monthSpinner.setEnabled(!isChecked);
				yearSpinner.setEnabled(!isChecked);
			   }
			});
		
		allCategoriesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {					   
				setUpList();
				categorySpinner.setEnabled(!isChecked);
			}
			});
		
		// Default values
    	allCategoriesCheckBox.setChecked(true); // all categories
    	allDatesCheckBox.setChecked(false); // only current month
	}
	
    private void initSpinnerState() {
    	
		// Get current month and year
		int currentMonth = Util.getTodaysMonth();
		int currentYear = Util.getTodaysYear();
		String currentYearText = Util.makeYearString(currentYear);
    	
    	// Initialise Spinners
    	monthSpinner = (Spinner) findViewById(R.id.incexp_month);
    	yearSpinner = (Spinner) findViewById(R.id.incexp_year);
    	categorySpinner = (Spinner) findViewById(R.id.incexp_category);
    	
    	// Initialise Lists
    	listOfMonths = Util.getListOfMonthsShort();
		listOfYears = Util.getListOfYears();
		listOfCategories = db.getIncomeCategoryList();
		listOfCategoryIDs = db.getIncomeCategoryIDList();
    	
    	// Month
    	ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfMonths);
    	monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	monthSpinner.setAdapter(monthAdapter);
    	monthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	setUpList();
		    }
	
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }	
		});
    	
		// Year
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfYears);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(yearAdapter);
		yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		        setUpList();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }
		});
		
		// Category
		ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfCategories);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				setUpList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		// Select default values
    	monthSpinner.setSelection(currentMonth-1); // -1 because of zero based indexing
		yearSpinner.setSelection(listOfYears.indexOf(currentYearText));
		//categorySpinner.setSelection( DEFAULT );
	}
    
    @Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		//showOptionsDialog();
    	if (selectedTab == TAB_INCOME) {
    		String income_id = v.getTag(R.id.list_item_income).toString();
    		Intent i = new Intent(ViewIncExpActivity.this, AddIncomeActivity.class);
    		i.putExtra("CURRENT_ID", income_id);
    		startActivity(i);
    	} else {
    		String expense_id = v.getTag(R.id.list_item_expense).toString();
    		Intent i = new Intent(ViewIncExpActivity.this, AddExpenseActivity.class);
    		i.putExtra("CURRENT_ID", expense_id);
    		startActivity(i);
    	}
    }

	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_viewincexp, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
      case R.id.viewincexp_menu_addincome:
    	  Intent i = new Intent(ViewIncExpActivity.this, AddIncomeActivity.class);
    	  ViewIncExpActivity.this.startActivity(i);
      break;
        
      case R.id.viewincexp_menu_addexpense:
  	  	Intent j = new Intent(ViewIncExpActivity.this, AddExpenseActivity.class);
  	  	ViewIncExpActivity.this.startActivity(j);
      break;
      
      }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        
    	/**
    	 * On first tab we will show the Income list
    	 */
    	selectedTab = tab.getPosition();
    	setUpList();
    	if (tab.getPosition() == TAB_INCOME) {
//    		incomeFragment = new IncomeListFragment();
//    		getSupportFragmentManager().beginTransaction().replace(R.id.container, incomeFragment).commit();
    		
    	} else if (tab.getPosition() == TAB_EXPENSES) {
//    		expenseFragment = new ExpenseListFragment();
//    		getSupportFragmentManager().beginTransaction().replace(R.id.container, expenseFragment).commit();    	
    	}
    	else {
    		// do nothing
    	}
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
}