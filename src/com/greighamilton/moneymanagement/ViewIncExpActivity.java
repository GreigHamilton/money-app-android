package com.greighamilton.moneymanagement;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.greighamilton.moneymanagement.fragments.ExpenseListFragment;
import com.greighamilton.moneymanagement.fragments.IncomeListFragment;

public class ViewIncExpActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int TAB_INCOME = 0;
    private static final int TAB_EXPENSES = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewincexp);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("Income").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Expenses").setTabListener(this));
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
      
      case R.id.menu_addincome:
    	  Intent i = new Intent(ViewIncExpActivity.this, AddIncomeActivity.class);
    	  ViewIncExpActivity.this.startActivity(i);
      break;
        
      case R.id.menu_addexpense:
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
    	if (tab.getPosition() == TAB_INCOME) {
    		IncomeListFragment f = new IncomeListFragment();
    		getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
    	} else if (tab.getPosition() == TAB_EXPENSES) {
    		ExpenseListFragment g = new ExpenseListFragment();
    		getSupportFragmentManager().beginTransaction().replace(R.id.container, g).commit();    	
    	}
    	else {
    		// do nothing
    	}
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}