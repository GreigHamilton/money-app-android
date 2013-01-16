package com.greighamilton.moneymanagement.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import arnodenhond.graphview.GraphView;

import com.greighamilton.moneymanagement.R;

public class ViewTrendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_viewtrends);
		
		super.onCreate(savedInstanceState);
		float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f, 1.0f };
		String[] verlabels = new String[] { "great", "ok", "bad" };
		String[] horlabels = new String[] { "today", "tomorrow", "next week", "next month" };
		GraphView graphView = new GraphView(this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);
		
		setContentView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_viewtrends, menu);
		return true;
	}
}
