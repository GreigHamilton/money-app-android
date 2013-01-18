package com.greighamilton.moneymanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.Days;
import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;
import com.greighamilton.moneymanagement.views.ViewCategoriesActivity;
import com.greighamilton.moneymanagement.views.ViewGoalsActivity;
import com.greighamilton.moneymanagement.views.ViewIncExpActivity;
import com.greighamilton.moneymanagement.views.ViewSummaryActivity;
import com.greighamilton.moneymanagement.views.ViewTrendsActivity;

public class DashboardActivity extends Activity {

	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		db = DatabaseHelper.getInstance(this);

		Button b = ((Button) findViewById(R.id.button_top_left));
		b.setText(DatabaseHelper.getInstance(this).getIncomeName(1));

		Button d = ((Button) findViewById(R.id.button_top_centre));
		d.setText(DatabaseHelper.getInstance(this).getExpenseName(1));

		LinearLayout top = ((LinearLayout) findViewById(R.id.top));
		top.removeAllViews();
		LinearLayout widget = (LinearLayout) getLayoutInflater().inflate(
				R.layout.widget_countdown, top);
		TextView days = (TextView) widget.findViewById(R.id.days);
		TextView description = (TextView) widget.findViewById(R.id.description);

		Cursor c = db.getExpensesByDate();
		c.moveToFirst();
		
		String daysText = c.getString(DatabaseHelper.EXPENSE_DATE);

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date itemDate = (Date) sdf.parse(daysText);
			Time now = new Time();
			now.setToNow();
			Date nowDate = (Date) sdf.parse(now.year + "-" +
			((now.month < 10)    ? "0" + now.month    : now.month    ) + 
			((now.monthDay < 10) ? "0" + now.monthDay : now.monthDay ));
			daysText = "" + Days.daysBetween(new DateTime(nowDate), new DateTime(itemDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		days.setText(daysText);
		description.setText(c.getString(DatabaseHelper.EXPENSE_NAME));

		// while (!c.isAfterLast()){
		// // TODO
		// c.moveToNext();
		// }

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

}
