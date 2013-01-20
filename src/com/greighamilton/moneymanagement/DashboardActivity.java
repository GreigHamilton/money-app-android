package com.greighamilton.moneymanagement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;
import com.greighamilton.moneymanagement.views.ViewCategoriesActivity;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;
import com.greighamilton.moneymanagement.views.ViewIncExpActivity;
import com.greighamilton.moneymanagement.views.ViewSummaryActivity;
import com.greighamilton.moneymanagement.views.ViewTrendsActivity;

public class DashboardActivity extends Activity {

	DatabaseHelper db;
	List<LinearLayout> widgets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
		db = DatabaseHelper.getInstance(this);
		widgets = new ArrayList<LinearLayout>();
		
		setUpWidgets();
		
	}

	private void setUpWidgets() {

		// Query upcoming expenses
		Cursor c = db.getExpensesByDate(Util.getTodaysDate(), null,	Util.ASCENDING);
		c.moveToFirst();
		while (!c.isAfterLast()) {

			LinearLayout widget;
			String daysUntil;
			
			widget = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_countdown, null);
			daysUntil = c.getString(DatabaseHelper.EXPENSE_DATE);
			int colour = Color.parseColor(db.getCategoryColour(c.getInt(DatabaseHelper.EXPENSE_CATEGORY_ID)));
			
			try {
				daysUntil = "" + Util.daysUntil(c.getString(DatabaseHelper.EXPENSE_DATE));
				TextView days = (TextView) widget.findViewById(R.id.days);
				TextView description = (TextView) widget.findViewById(R.id.description);
				days.setText(daysUntil);
				description.setText(c.getString(DatabaseHelper.EXPENSE_NAME));
				widget.setTag("" + c.getInt(DatabaseHelper.EXPENSE_ID));
				widget.setBackgroundColor(colour);
				widgets.add(widget);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.moveToNext();
		}
		
		GridView grid = (GridView) findViewById(R.id.grid_of_widgets);
		grid.setAdapter(new WidgetAdapter(this, widgets));
		
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
		switch (item.getItemId()) {

		case R.id.dashboard_menu_addincome:
			Intent i = new Intent(DashboardActivity.this,
					AddIncomeActivity.class);
			DashboardActivity.this.startActivity(i);
			break;

		case R.id.dashboard_menu_addexpense:
			Intent j = new Intent(DashboardActivity.this,
					AddExpenseActivity.class);
			DashboardActivity.this.startActivity(j);
			break;

		case R.id.dashboard_menu_viewgoals:
			Intent k = new Intent(DashboardActivity.this,
					ViewGoalsActivity.class);
			DashboardActivity.this.startActivity(k);
			break;

		case R.id.dashboard_menu_viewcategories:
			Intent l = new Intent(DashboardActivity.this,
					ViewCategoriesActivity.class);
			DashboardActivity.this.startActivity(l);
			break;

		case R.id.dashboard_menu_summary:
			Intent m = new Intent(DashboardActivity.this,
					ViewSummaryActivity.class);
			DashboardActivity.this.startActivity(m);
			break;

		case R.id.dashboard_menu_viewtrends:
			Intent n = new Intent(DashboardActivity.this,
					ViewTrendsActivity.class);
			DashboardActivity.this.startActivity(n);
			break;

		case R.id.dashboard_menu_viewincexp:
			Intent o = new Intent(DashboardActivity.this,
					ViewIncExpActivity.class);
			DashboardActivity.this.startActivity(o);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void clickWidget(View v){
		String expenseID = (String) v.getTag();
		Intent i = new Intent(this, AddExpenseActivity.class);
		i.putExtra("CURRENT_ID", expenseID);
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

}
