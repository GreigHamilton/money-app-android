package com.greighamilton.moneymanagement.views;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.adapters.CategoryListAdapter;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddCategoryActivity;

/**
 * Class for the categories activity.
 * 
 * @author Greig Hamilton
 *
 */
public class ViewCategoriesActivity extends ListActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int TAB_INCOME = 0;
    private static final int TAB_EXPENSES = 1;
    
    private int selectedTab;
    private String selectedItem;
    
    private DatabaseHelper db;
    private Cursor c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcategories);
        
        db = DatabaseHelper.getInstance(this);
        selectedTab = TAB_INCOME;

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("Income").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Expenses").setTabListener(this));
        
        refreshList();
        
	    actionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    /**
     * Method for refreshing the categories list view.
     * 
     */
    public void refreshList() {    	
    	c = (selectedTab == TAB_INCOME) ? db.getIncomeCategories() : db.getExpenseCategories();
    	setListAdapter(new CategoryListAdapter(this, c));
    }
    
    @Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		// Show context action bar
    	selectedItem = v.getTag(R.id.list_item_category).toString();
    	startActionMode(mActionModeCallback);
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
        getMenuInflater().inflate(R.menu.activity_viewcategories, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.viewcategories_menu_addcategory:
			Intent i = new Intent(ViewCategoriesActivity.this, AddCategoryActivity.class);
			
			if (selectedTab == 0)
				i.setFlags(0);
			else
				i.setFlags(1);
			
			ViewCategoriesActivity.this.startActivity(i);
			break;

		case android.R.id.home:

			finish();
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
    	 * On first tab we will show the Income Categories list and Expenses Categories on the second
    	 */
    	if (tab.getPosition() == TAB_INCOME) {
    		selectedTab = TAB_INCOME;
    	} else if (tab.getPosition() == TAB_EXPENSES) {
    		selectedTab = TAB_EXPENSES;
    	}
    	else {
    		// do nothing
    	}
		refreshList();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_categories, menu);			
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
	            case R.id.context_categories_edit:
	            	i = new Intent(ViewCategoriesActivity.this, AddCategoryActivity.class);
	            	i.putExtra("CURRENT_ID", selectedItem);
	            	startActivity(i);
	            	return true;
	            
	            // Delete clicked
	            case R.id.context_categories_delete:
	            	db.deleteCategory(selectedItem);
	            	refreshList();
	            	Toast.makeText(ViewCategoriesActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
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
    
}