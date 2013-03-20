package com.greighamilton.moneymanagement.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;

/**
 * Class for hints and tips activity --- sample code used and extended for purpose.
 * 
 * @author Greig Hamilton
 *
 */
public class HintsTipsActivity extends ListActivity {

	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new MyTask().execute();
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	
	        	finish();
	            break;

	    }
	    return super.onOptionsItemSelected(item);
	}

	private class MyTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog progressDialog;

		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(HintsTipsActivity.this, "", "Loading Feed. Please wait...", true);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			
			// required url
			HttpGet httpGet = new HttpGet("https://api.twitter.com/1/statuses/user_timeline.json?screen_name=notmadeofmoney");
			
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					// Couldn't obtain the data
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String timeline = builder.toString();
			try {
				JSONArray jsonArray = new JSONArray(timeline);

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject obj = jsonArray.getJSONObject(i);

					Tweet tweet = new Tweet();
					tweet.setAuthor("@NotMadeOfMoney");
					tweet.setContent(obj.getString("text"));
					tweet.setSource();
					tweets.add(tweet);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			setListAdapter(new TweetListAdaptor(HintsTipsActivity.this, R.layout.list_item, tweets));
			getListView().setOnItemClickListener(new OnItemClickListener()
			{
			    @Override
			    public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3)
			    {
			    	try {
				        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((Tweet) v.getTag()).getSource()));
				        startActivity(browserIntent);
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    	}
			    }
			});
		}

		private class TweetListAdaptor extends ArrayAdapter<Tweet> {
			
			private ArrayList<Tweet> tweets;

			public TweetListAdaptor(Context context, int textViewResourceId, ArrayList<Tweet> items) {
				super(context, textViewResourceId, items);
				this.tweets = items;
			}

			@Override
			public View getView(int position, View view, ViewGroup parent) {
				
				View v = view;

				if (v == null) {
					
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.list_item, null);
				}
				
				Tweet tweet = tweets.get(position);

				v.setTag(tweet);
				TextView top = (TextView) v.findViewById(R.id.toptext);
				TextView bottom = (TextView) v.findViewById(R.id.bottomtext);
				top.setText(tweet.author);
				bottom.setText(tweet.content);

				return v;
			}
		}
	}
}
