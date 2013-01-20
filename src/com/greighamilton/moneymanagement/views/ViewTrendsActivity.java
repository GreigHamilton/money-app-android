package com.greighamilton.moneymanagement.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import arnodenhond.graphview.LineGraph;

import com.greighamilton.moneymanagement.R;

public class ViewTrendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewtrends);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_viewtrends, menu);
		return true;
	}

	public void lineGraphHandler(View view) {
		LineGraph line = new LineGraph();
		Intent lineIntent = line.getIntent(this);
		startActivity(lineIntent);
	}
}
