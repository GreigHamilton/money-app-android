package com.greighamilton.moneymanagement.views;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;

public class ViewSummaryActivity extends Activity {
	
	private DatabaseHelper db;
	
	private List<Integer> incomes;
	private List<Integer> expenses;
	
	private int[] greens = new int[]{ R.color.green1,
			R.color.green2, R.color.green3, R.color.green4,
			R.color.green5, R.color.green6 };
	private int[] reds = new int[]{ R.color.red1,
			R.color.red2, R.color.red3, R.color.red4,
			R.color.red5, R.color.red6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewsummary);
		
		db = DatabaseHelper.getInstance(this);
		
		setUpIncome();		
	}
	
	private void setUpIncome(){
		
		// Initialise data structures
		incomes = new LinkedList<Integer>();
		expenses = new LinkedList<Integer>();
		int totalIncome = 0;
		int totalExpenses = 0;
		LinearLayout incomeLayout = (LinearLayout) findViewById(R.id.summary_income);
		LinearLayout expensesLayout = (LinearLayout) findViewById(R.id.summary_expenses);
		
		// Get Incomes
		Cursor incomeCursor = db.getIncomeByAmount("01", "2013", false);		
		incomeCursor.moveToFirst();
		while (!incomeCursor.isAfterLast()){
			incomes.add(incomeCursor.getInt(DatabaseHelper.INCOME_ID));
			incomeCursor.moveToNext();
		}
		incomeCursor.close();
		
		for (Integer i : incomes){
			Cursor c = db.getIncomeId(""+i);
			c.moveToFirst();
			totalIncome += c.getInt(DatabaseHelper.INCOME_AMOUNT);
			c.close();
		}
		((TextView) findViewById(R.id.income_total)).setText("IN �"+totalIncome);
		
		incomeLayout.removeAllViews();
		incomeLayout.setWeightSum(totalIncome);
		incomeLayout.setPadding(10, 10, 10, 10);
		for (int i=0; i<incomes.size(); i++){
			Cursor c = db.getIncomeId(""+incomes.get(i));
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.INCOME_AMOUNT);
			String name = c.getString(DatabaseHelper.INCOME_NAME);
			LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, amount));
			block.setBackgroundColor(getResources().getColor(greens[i%6]));
			block.setGravity(Gravity.CENTER);
			TextView text = new TextView(this);
			text.setText("Income "+i+" - "+name+" - �"+amount);
			block.addView(text);
			incomeLayout.addView(block);
		}
		
		
		// Get Expenses
		Cursor expensesCursor = db.getExpensesByAmount("01", "2013", false);		
		expensesCursor.moveToFirst();
		while (!expensesCursor.isAfterLast()){
			expenses.add(expensesCursor.getInt(DatabaseHelper.EXPENSE_ID));
			expensesCursor.moveToNext();
		}
		expensesCursor.close();
		
		for (Integer i : expenses){
			Cursor c = db.getExpenseId(""+i);
			c.moveToFirst();
			totalExpenses += c.getInt(DatabaseHelper.EXPENSE_AMOUNT);
			c.close();
		}
		((TextView) findViewById(R.id.expenses_total)).setText("OUT �"+totalExpenses);
		
		expensesLayout.removeAllViews();
		expensesLayout.setWeightSum(totalIncome); // same as income, NOT expenses
		expensesLayout.setPadding(10, 10, 10, 10);
		
		// Disposable Income
		LinearLayout b = new LinearLayout(this);
		int a = totalIncome - totalExpenses;
		b.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, a));
		b.setBackgroundColor(getResources().getColor(R.color.Purple));
		b.setGravity(Gravity.CENTER);
		TextView t = new TextView(this);
		t.setText("Disposable Income - �"+a);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		b.addView(t);
		expensesLayout.addView(b);
		
		for (int i=0; i<expenses.size(); i++){
			Cursor c = db.getExpenseId(""+expenses.get(i));
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.EXPENSE_AMOUNT);
			String name = c.getString(DatabaseHelper.EXPENSE_NAME);
			LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, amount));
			block.setBackgroundColor(getResources().getColor(reds[i%6]));
			block.setGravity(Gravity.CENTER);
			TextView text = new TextView(this);
			text.setText("Expense "+i+" - "+name+" - �"+amount);
			block.addView(text);
			expensesLayout.addView(block);
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
    	  	Intent i = new Intent(ViewSummaryActivity.this, AddIncomeActivity.class);
    	  	ViewSummaryActivity.this.startActivity(i);
        break;
        
      case R.id.viewsummary_menu_addexpense:
  	  	Intent j = new Intent(ViewSummaryActivity.this, AddExpenseActivity.class);
  	  ViewSummaryActivity.this.startActivity(j);
      break;
      
      case R.id.viewsummary_menu_viewtrends:
    	  Intent l = new Intent(ViewSummaryActivity.this, ViewTrendsActivity.class);
    	  ViewSummaryActivity.this.startActivity(l);
        break;
      }
      return super.onOptionsItemSelected(item);
    }
}
