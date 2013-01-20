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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;

public class ViewSummaryActivity extends Activity {

	private DatabaseHelper db;

	private List<Integer> incomes;
	private List<Integer> expenses;

	Spinner monthSpinner;
	Spinner yearSpinner;

	private int monthReq;
	private int yearReq;

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
		String month = Util.makeMonthString(monthReq);
		String year = Util.makeYearString(yearReq);
		
		setSpinnerContent();
		setUpIncome(month, year);
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
		monthSpinner.setSelection(month);
		monthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				monthReq = position + 1;

				String month = Util.makeMonthString(monthReq);
				String year = Util.makeYearString(yearReq);

				setUpIncome(month, year);
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

				String month = Util.makeMonthString(monthReq);
				String year = Util.makeYearString(yearReq);

				setUpIncome(month, year);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
	}

	private void setUpIncome(String month, String year) {

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

		incomeLayout.removeAllViews();
		incomeLayout.setWeightSum(totalIncome);
		incomeLayout.setPadding(10, 10, 10, 10);
		for (int i = 0; i < incomes.size(); i++) {
			Cursor c = db.getIncomeId("" + incomes.get(i));
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.INCOME_AMOUNT);
			String name = c.getString(DatabaseHelper.INCOME_NAME);
			LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					amount));
			block.setBackgroundColor(getResources().getColor(greens[i % 6]));
			block.setGravity(Gravity.CENTER);
			TextView text = new TextView(this);
			text.setText(name + " - £" + amount);
			block.addView(text);
			incomeLayout.addView(block);
		}

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

		expensesLayout.removeAllViews();
		expensesLayout.setWeightSum(totalIncome); // same as income, NOT
													// expenses
		expensesLayout.setPadding(10, 10, 10, 10);

		// Disposable Income
		LinearLayout b = new LinearLayout(this);
		int a = totalIncome - totalExpenses;
		b.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, a));
		b.setBackgroundColor(getResources().getColor(R.color.Purple));
		b.setGravity(Gravity.CENTER);
		TextView t = new TextView(this);
		t.setText("Disposable Income - £" + a);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		b.addView(t);
		expensesLayout.addView(b);

		for (int i = 0; i < expenses.size(); i++) {
			Cursor c = db.getExpenseId("" + expenses.get(i));
			c.moveToFirst();
			int amount = c.getInt(DatabaseHelper.EXPENSE_AMOUNT);
			String name = c.getString(DatabaseHelper.EXPENSE_NAME);
			LinearLayout block = new LinearLayout(this);
			block.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					amount));
			block.setBackgroundColor(getResources().getColor(reds[i % 6]));
			block.setGravity(Gravity.CENTER);
			TextView text = new TextView(this);
			text.setText(name + " - £" + amount);
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
}
