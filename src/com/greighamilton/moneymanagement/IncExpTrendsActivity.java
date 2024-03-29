package com.greighamilton.moneymanagement;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddExpenseActivity;
import com.greighamilton.moneymanagement.entry.AddIncomeActivity;
import com.greighamilton.moneymanagement.external.HintsTipsActivity;
import com.greighamilton.moneymanagement.external.VerticalTextView;
import com.greighamilton.moneymanagement.util.SettingsActivity;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;

/**
 * IncExpTrendsActivity Activity class.
 * 
 * @author Greig Hamilton
 *
 */
public class IncExpTrendsActivity extends Activity {

	private OnNavigationListener mOnNavigationListener;
	private DatabaseHelper db;

	private LinearLayout myGallery;
	private Spinner yearSpinner;
	private List<String> listOfYears;
	private List<String> listOfMonths;
	private float scale; // size of largest value in graph
	
	private String currencySymbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incexp_trends);

		setUpActionBar();
		db = DatabaseHelper.getInstance(this);

		myGallery = (LinearLayout) findViewById(R.id.gallery_view);
		
		setUpSpinner();
		resetView();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		currencySymbol = sp.getString("CURRENCYSYMBOL", "");
	}
	
	/**
	 * Method to set up the action bar for the Trends interface.
	 * 
	 */
	private void setUpActionBar() {
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setTitle(null);
		
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.incexp_views_2,
				R.layout.spinner_item_navigator);
		
		mOnNavigationListener = new OnNavigationListener() {
			  @Override
			  public boolean onNavigationItemSelected(int position, long itemId) {
				  Intent i;
				  switch (position) {
				  case 0:	break;
				  case 1:	i = new Intent(IncExpTrendsActivity.this, DashboardActivity.class);
		  					i.putExtra("PASSWORD", false);
		  					startActivity(i);
		  					IncExpTrendsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							finish();
		  					break;
				  case 2:	i = new Intent(IncExpTrendsActivity.this, IncExpVisualiserActivity.class);
		  					startActivity(i);
		  					IncExpTrendsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							finish();
		  					break;
				  case 3:	i = new Intent(IncExpTrendsActivity.this, IncExpListActivity.class);
		  					startActivity(i);
		  					IncExpTrendsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
	 * Method to set up the year spinner on the interface.
	 * 
	 */
	public void setUpSpinner() {		

		listOfYears = Util.getListOfYears();
		listOfMonths = Util.getListOfMonthsLong();
		
		yearSpinner = (Spinner) findViewById(R.id.year_spinner);
		
		// Set spinner adapter
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfYears);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(yearAdapter);
		yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				resetView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
	}
	
	/**
	 * Method used to reset the current view of data on the interface.
	 * 
	 */
	private void resetView() {
		
		// Find largest value to use as basis for scaling
		String selectedYear = listOfYears.get(yearSpinner.getSelectedItemPosition());
		int largest = 0;
		for (int i=0; i<listOfMonths.size(); i++) {
			int inc = db.getTotalIncomeAmountForMonth(selectedYear, Util.makeMonthString(i+1));
			largest = Math.max(largest, inc);
			int exp = db.getTotalExpenseAmountForMonth(selectedYear, Util.makeMonthString(i+1));
			largest = Math.max(largest, exp);
		}
		scale = largest;
		
		// Update view
		myGallery.removeAllViews();
		for (int i=0; i<listOfMonths.size(); i++) myGallery.addView(createMonthView(selectedYear, i+1));	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_incexp_trends, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {

		case R.id.viewtrends_menu_addincome:
			i = new Intent(IncExpTrendsActivity.this, AddIncomeActivity.class);
			IncExpTrendsActivity.this.startActivity(i);
			break;

		case R.id.viewtrends_menu_addexpense:
			i = new Intent(IncExpTrendsActivity.this, AddExpenseActivity.class);
			IncExpTrendsActivity.this.startActivity(i);
			break;

		case R.id.viewtrends_menu_viewgoals:
			i = new Intent(IncExpTrendsActivity.this, ViewGoalsActivity.class);
			IncExpTrendsActivity.this.startActivity(i);
			break;
			
		case R.id.viewtrends_menu_viewhints:
			i = new Intent(IncExpTrendsActivity.this, HintsTipsActivity.class);
			IncExpTrendsActivity.this.startActivity(i);
			break;
			
		case R.id.viewtrends_menu_password:
			i = new Intent(IncExpTrendsActivity.this, SettingsActivity.class);
			IncExpTrendsActivity.this.startActivity(i);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method used to create the view for each month in the trends interface.
	 * 
	 * @param year			the chosen year required to be shown
	 * @param month			the chosen month required to be shown
	 * 
	 * @return				the view
	 */
	private View createMonthView(String year, int month) {
		
		int income = db.getTotalIncomeAmountForMonth(year, Util.makeMonthString(month));
		int expense = db.getTotalExpenseAmountForMonth(year, Util.makeMonthString(month));
		
		// Create frame
		LinearLayout frame = new LinearLayout(getApplicationContext());
		frame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		frame.setOrientation(LinearLayout.VERTICAL);
		frame.setGravity(Gravity.CENTER_HORIZONTAL);
		frame.setBackgroundResource((month%2 == 0) ? R.color.grey4 : R.color.grey1);
		
		// Month text
		TextView text = new TextView(this);
		text.setText(listOfMonths.get(month-1));
		text.setGravity(Gravity.CENTER_HORIZONTAL);
		text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		frame.addView(text);
		
		// Monthly Summary text
		TextView summaryText = new TextView(this);
		if (income-expense > 0) {
			summaryText.setTextColor(getResources().getColor(R.color.green1));
			summaryText.setText('\n'+ "      + " + currencySymbol +(income-expense));
		}
		else {
			summaryText.setTextColor(getResources().getColor(R.color.red1));
			summaryText.setText('\n'+ "      - " + currencySymbol +(expense-income));
		}
		summaryText.setGravity(Gravity.CENTER_HORIZONTAL);
		summaryText.setGravity(Gravity.TOP);
		summaryText.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		frame.addView(summaryText);
		
		// Create frame
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(150, LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		frame.setBackgroundResource((month%2 == 0) ? R.color.grey4 : R.color.grey1);
		layout.setWeightSum(2.0f);
		layout.setPadding(10, 10, 10, 10);		

		// Income frame
		LinearLayout incomeLayout = new LinearLayout(getApplicationContext());
		incomeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
		incomeLayout.setOrientation(LinearLayout.VERTICAL);
		incomeLayout.setGravity(Gravity.CENTER);
		frame.setBackgroundResource((month%2 == 0) ? R.color.grey4 : R.color.grey1);
		incomeLayout.setWeightSum(scale);
		incomeLayout.setPadding(10, 10, 0, 10);
		
		// Income Bar
		LinearLayout incomeBar = new LinearLayout(getApplicationContext());
		incomeBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, scale-income));
		incomeBar.setGravity(Gravity.CENTER_HORIZONTAL);
		incomeBar.setBackgroundResource(R.color.green1);
		
		// Income text
		VerticalTextView incomeText = new VerticalTextView(this);
		incomeText.setText("" + currencySymbol + income);
		incomeText.setScaleX(0.7f);
		incomeText.setScaleY(0.7f);
		incomeBar.addView(incomeText);
		
		// Income Space
		LinearLayout incomeSpace = new LinearLayout(getApplicationContext());
		incomeSpace.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, income));
		incomeSpace.setGravity(Gravity.BOTTOM);

		incomeLayout.addView(incomeSpace);
		incomeLayout.addView(incomeBar);
		
		// Expense frame
		LinearLayout expenseLayout = new LinearLayout(getApplicationContext());
		expenseLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
		expenseLayout.setOrientation(LinearLayout.VERTICAL);
		expenseLayout.setGravity(Gravity.CENTER);
		frame.setBackgroundResource((month%2 == 0) ? R.color.grey4 : R.color.grey1);
		expenseLayout.setWeightSum(scale);
		expenseLayout.setPadding(0, 10, 10, 10);
		
		LinearLayout expenseBar = new LinearLayout(getApplicationContext());
		expenseBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, scale-expense));
		expenseBar.setGravity(Gravity.CENTER_HORIZONTAL);
		expenseBar.setBackgroundResource(R.color.red1);
		
		// Expense text
		VerticalTextView expenseText = new VerticalTextView(this);
		expenseText.setText("" + currencySymbol + expense);
		expenseText.setScaleX(0.7f);
		expenseText.setScaleY(0.7f);
		expenseBar.addView(expenseText);
		
		LinearLayout expenseSpace = new LinearLayout(getApplicationContext());
		expenseSpace.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, expense));
		expenseSpace.setGravity(Gravity.BOTTOM);

		expenseLayout.addView(expenseSpace);
		expenseLayout.addView(expenseBar);
		
		layout.addView(incomeLayout);
		layout.addView(expenseLayout);
		
		frame.addView(layout);

		return frame;

	}
}
