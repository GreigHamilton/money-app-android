package com.greighamilton.moneymanagement;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.adapters.ExpenseListAdapter;
import com.greighamilton.moneymanagement.adapters.IncomeListAdapter;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddExpenseActivity;
import com.greighamilton.moneymanagement.entry.AddIncomeActivity;
import com.greighamilton.moneymanagement.external.HintsTipsActivity;
import com.greighamilton.moneymanagement.util.SettingsActivity;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.views.ViewCategoriesActivity;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;

/**
 * IncExpListActivity Activity class.
 * 
 * @author Greig Hamilton
 *
 */
public class IncExpListActivity extends ListActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int TAB_INCOME = 0;
    private static final int TAB_EXPENSES = 1;
    
    private DatabaseHelper db;
    private Cursor c;
    private OnNavigationListener mOnNavigationListener;
    
    private int selectedTab;
    private String selectedItem;
    
    private Button incomeButton;
    private Button expensesButton;
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
        setContentView(R.layout.activity_incexp_list);        

        setUpActionBar();
        db = DatabaseHelper.getInstance(this);
        
        // initialise the interface
        init();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	selectedItem = null;
    	
    	init();
    }
    
    /**
     * Method to set up the action bar for the list interface.
     * 
     */
	private void setUpActionBar() {
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setTitle(null);
		
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.incexp_views_3,
		          R.layout.spinner_item_navigator);
		
		mOnNavigationListener = new OnNavigationListener() {
			  @Override
			  public boolean onNavigationItemSelected(int position, long itemId) {
				  Intent i;
				  switch (position) {
				  case 0:	break;
				  case 1:	i = new Intent(IncExpListActivity.this, DashboardActivity.class);
				  			i.putExtra("PASSWORD", false);
				  			startActivity(i);
				  			IncExpListActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		  					finish();
		  					break;
				  case 2:	i = new Intent(IncExpListActivity.this, IncExpVisualiserActivity.class);
				  			startActivity(i);
				  			IncExpListActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							finish();
							break;
				  case 3:	i = new Intent(IncExpListActivity.this, IncExpTrendsActivity.class);
				  			startActivity(i);
				  			IncExpListActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							finish();
				  			break;
				  default:	break;
				  }
				  return true;
			  }
		};
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);		
	}

    /**
     * Method to initialise form filters and fill list with data
     */
	private void init() {
		
		// Check initial states of spinners and checkboxes
		if (incomeButton == null || expensesButton == null) initTabsState();
		if (allDatesCheckBox == null || allCategoriesCheckBox == null) initCheckboxState();
		if (monthSpinner == null || yearSpinner == null || categorySpinner == null) initSpinnerState();
		
		// Get selection status
		String month = Util.makeMonthString(monthSpinner.getSelectedItemPosition()+1);
		String year = (String) yearSpinner.getSelectedItem();
		boolean allDates = allDatesCheckBox.isChecked();
		boolean allCategories = allCategoriesCheckBox.isChecked();
		
		// Query db and set list adapter
    	if (selectedTab == TAB_INCOME) {
    		listOfCategories = db.getIncomeCategoryList();
    		listOfCategoryIDs = db.getIncomeCategoryIDList();
    		int category = 0;
    		if (!listOfCategoryIDs.isEmpty()) category = listOfCategoryIDs.get(categorySpinner.getSelectedItemPosition());    		
    		c = db.getSpecifiedIncome(month, year, category, allDates, allCategories);
    		
    		setListAdapter(new IncomeListAdapter(this, c));
    	} else {
    		listOfCategories = db.getExpenseCategoryList();
    		listOfCategoryIDs = db.getExpenseCategoryIDList();
    		int category = 0;
    		if (!listOfCategoryIDs.isEmpty()) category = listOfCategoryIDs.get(categorySpinner.getSelectedItemPosition());    	
    		c = db.getSpecifiedExpenses(month, year, category, allDates, allCategories);
    		setListAdapter(new ExpenseListAdapter(this, c));
    	}
    	
	}
	
	/**
	 * Method to initialise the tab state of the list interface.
	 */
	private void initTabsState() {
		incomeButton = (Button) findViewById(R.id.incexp_income_button);
		expensesButton = (Button) findViewById(R.id.incexp_expenses_button);
		
		incomeButton.setBackgroundColor(getResources().getColor(R.color.blue3));
		expensesButton.setBackgroundColor(getResources().getColor(R.color.grey2));
	}
	
	/**
	 * Method to initialise the checkbox state of the interface.
	 * 
	 */
	private void initCheckboxState() {		
		
		// Set up check boxes
    	allDatesCheckBox = (CheckBox) findViewById(R.id.incexp_all_dates);
    	allCategoriesCheckBox = (CheckBox) findViewById(R.id.incexp_all_categories);
    	
    	allDatesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				init();
				monthSpinner.setVisibility((isChecked) ? View.INVISIBLE : View.VISIBLE);
				yearSpinner.setVisibility((isChecked) ? View.INVISIBLE : View.VISIBLE);
			   }
			});
		
		allCategoriesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {					   
				init();
				categorySpinner.setVisibility((isChecked) ? View.INVISIBLE : View.VISIBLE);
			}
			});
		
		allDatesCheckBox.setChecked(false);
		allCategoriesCheckBox.setChecked(true);
	}
	
	/**
	 * Method to initialise the selected month and year spinner on the interface.
	 * 
	 */
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
		    	init();
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
		    	init();
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
				init();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		// Select default values
    	monthSpinner.setSelection(currentMonth-1); // -1 because of zero based indexing
		yearSpinner.setSelection(listOfYears.indexOf(currentYearText));
		//categorySpinner.setSelection( DEFAULT );
		
    	// Initially show dates and hide categories
    	monthSpinner.setVisibility(View.VISIBLE);
    	yearSpinner.setVisibility(View.VISIBLE);
    	categorySpinner.setVisibility(View.INVISIBLE);
	}
    
    @Override
	public void onListItemClick(ListView list, View v, int position, long id) {
    	
    			
		// Show context action bar
    	if (selectedTab == TAB_INCOME) {
    		selectedItem = v.getTag(R.id.list_item_income).toString();
    		startActionMode(mActionModeCallback);
    	} else {
    		selectedItem = v.getTag(R.id.list_item_expense).toString();
    		startActionMode(mActionModeCallback);
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
        getMenuInflater().inflate(R.menu.activity_incexp_list, menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {

		case R.id.viewincexp_menu_addincome:
			i = new Intent(IncExpListActivity.this, AddIncomeActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;

		case R.id.viewincexp_menu_addexpense:
			i = new Intent(IncExpListActivity.this, AddExpenseActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;

		case R.id.viewincexp_menu_categories:
			i = new Intent(IncExpListActivity.this, ViewCategoriesActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;
		
		case R.id.viewincexp_menu_viewgoals:
			i = new Intent(IncExpListActivity.this, ViewGoalsActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;
			
		case R.id.viewincexp_menu_viewhints:
			i = new Intent(IncExpListActivity.this, HintsTipsActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;
			
		case R.id.viewincexp_menu_password:
			i = new Intent(IncExpListActivity.this, SettingsActivity.class);
			IncExpListActivity.this.startActivity(i);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    /**
     * Method to show the delete dialog on the interface.
     * 
     */
	protected void showDeleteDialog() {

    	new AlertDialog.Builder(IncExpListActivity.this)
        .setTitle("Delete")
        .setMessage("Are you sure you want to delete this?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
            	// Continue with delete
            	if (selectedTab == TAB_INCOME) {
            		if (db.getIncomeRepetitionPeriod(selectedItem) != 0) {
                		showDeleteSeriesDialog();
            		} else {
                		db.deleteIncome(selectedItem);
	            		init();
                		Toast.makeText(IncExpListActivity.this, "Income item deleted", Toast.LENGTH_SHORT).show();
                	}
            	} else {
            		if (db.getExpenseRepetitionPeriod(selectedItem) != 0) {
                		showDeleteSeriesDialog();
                	} else {
                		db.deleteExpense(selectedItem);
                		init();
                		Toast.makeText(IncExpListActivity.this, "Expense item deleted", Toast.LENGTH_SHORT).show();
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
	 * Method to show the delete series dialog box.
	 * 
	 */
	protected void showDeleteSeriesDialog() {

    	new AlertDialog.Builder(IncExpListActivity.this)
        .setTitle("Delete Series")
        .setMessage("Delete the whole series, or just this one?")
        .setPositiveButton("Whole Series", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Delete series
            	if (selectedTab == TAB_INCOME) {
               		db.deleteIncomeSeries(db.getIncomeSeriesID(selectedItem));
        			init();
                	Toast.makeText(IncExpListActivity.this, "Income items deleted", Toast.LENGTH_SHORT).show();
            	} else {
               		db.deleteExpenseSeries(db.getExpenseSeriesID(selectedItem));
               		init();
            		Toast.makeText(IncExpListActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();     		
            	}
            }
         })
        .setNeutralButton("Just This", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// Just delete this
            	if (selectedTab == TAB_INCOME) {
               		db.deleteIncome(selectedItem);
               		init();
                	Toast.makeText(IncExpListActivity.this, "Income items deleted", Toast.LENGTH_SHORT).show();
            	} else {
            		db.deleteExpense(selectedItem);
            		init();
            		Toast.makeText(IncExpListActivity.this, "Expense items deleted", Toast.LENGTH_SHORT).show();     		
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
	
	/**
	 * Method called on event click when user clicks the income button.
	 * 
	 * @param v			the current view
	 */
	public void onIncomeButtonClick(View v) {
		if (selectedTab != TAB_INCOME) {
			onButtonClicked(TAB_INCOME);
			incomeButton.setBackgroundColor(getResources().getColor(R.color.blue3));
			expensesButton.setBackgroundColor(getResources().getColor(R.color.grey2));
		}
	}
	
	/**
	 * Method called on event click when the user clicks the expense button.
	 * 
	 * @param v			the current view
	 */
	public void onExpensesButtonClick(View v) {
		if (selectedTab != TAB_EXPENSES) {
			onButtonClicked(TAB_EXPENSES);
			expensesButton.setBackgroundColor(getResources().getColor(R.color.blue3));
			incomeButton.setBackgroundColor(getResources().getColor(R.color.grey2));
		}
	}
	
	/**
	 * Method called when the button is clicked.
	 * 
	 * @param selected			current selected tab
	 */
	private void onButtonClicked(int selected) {
		selectedTab = selected;
    	selectedItem = null;
    	
    	if (categorySpinner != null) {
    	
	    	// Change list of categories
	    	if (selectedTab == 0) {
	    		listOfCategories = db.getIncomeCategoryList();
	    		listOfCategoryIDs = db.getIncomeCategoryIDList();
	    	} else {
	    		listOfCategories = db.getExpenseCategoryList();
	    		listOfCategoryIDs = db.getExpenseCategoryIDList();
	    	}
	    	
	    	// Update listadapter for categories spinner
			ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfCategories);
			categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			categorySpinner.setAdapter(categoryAdapter);
			categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					init();
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
			
    	}
    	init();
	}
    
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    /**
	     * Called when the action mode is created; startActionMode() was called
	     */
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_incexp, menu);			
	        return true;
	    }

	    /**
	     * Called each time the action mode is shown. Always called after onCreateActionMode, but
	     * may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    /**
	     * Called when the user selects a contextual menu item
	     */
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	Intent i;
	    	// Get selected item ID
	        switch (item.getItemId()) {
	        	
	        // Edit clicked
	            case R.id.context_incexp_edit:
	            	if (selectedTab == TAB_INCOME) {
	            		i = new Intent(IncExpListActivity.this, AddIncomeActivity.class);
	            		i.putExtra("CURRENT_ID", selectedItem);
	            		selectedItem = null;
	            		startActivity(i);
	            	} else {
	            		i = new Intent(IncExpListActivity.this, AddExpenseActivity.class);
	            		i.putExtra("CURRENT_ID", selectedItem);
	            		selectedItem = null;
	            		startActivity(i);
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
	    
	    /**
	     * Called when the user exits the action mode
	     */
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	selectedItem = null;
	    	mode = null;
	    }

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			return false;
		}
	};
    
}