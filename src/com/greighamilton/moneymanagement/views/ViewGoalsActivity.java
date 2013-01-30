package com.greighamilton.moneymanagement.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.utilities.AddGoalActivity;

public class ViewGoalsActivity extends Activity {

	private DatabaseHelper db;

	private List<String> listOfGoals;
	private List<Integer> listOfGoalIDs;

	private int selectedItem;
	private int selectedNeeded;
	private int selectedSaved;

	private Spinner goalSpinner;
	private TextView nameText;
	private TextView neededText;
	private TextView savedText;
	private TextView toSaveText;
	private ImageView goalImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewgoals);

		db = DatabaseHelper.getInstance(this);

		goalSpinner = (Spinner) findViewById(R.id.goal_spinner);
		nameText = (TextView) findViewById(R.id.goal_name);
		neededText = (TextView) findViewById(R.id.goal_needed);
		savedText = (TextView) findViewById(R.id.goal_saved);
		toSaveText = (TextView) findViewById(R.id.goal_to_save);
		goalImage = (ImageView) findViewById(R.id.goal_image);

		selectedItem = 0;

		setUpSpinner();
	}

	private void setUpSpinner() {

		listOfGoals = db.getListOfGoals();
		listOfGoalIDs = db.getListOfGoalIDs();

		// Month
		ArrayAdapter<String> goalAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listOfGoals);
		goalAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		goalSpinner.setAdapter(goalAdapter);
		goalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				selectedItem = position;
				refreshView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
	}

	private void refreshView() {
		Cursor c = db.getGoal(listOfGoalIDs.get(selectedItem));
		c.moveToFirst();
		if (!c.isAfterLast()) {
			selectedNeeded = c.getInt(DatabaseHelper.GOAL_NEEDED);
			selectedSaved = c.getInt(DatabaseHelper.GOAL_SAVED);
			String imagePath = c.getString(DatabaseHelper.GOAL_IMAGE);
			

			nameText.setText(c.getString(DatabaseHelper.GOAL_NAME));
			neededText.setText("£ " + selectedNeeded);
			savedText.setText("£ " + selectedSaved);
			toSaveText.setText("£ " + (selectedNeeded - selectedSaved));
			
			Log.i("", "Path: "+imagePath);
			
			if (imagePath != null) {
				
				try {
					Uri uri = Uri.parse(imagePath);
//					Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					image.compress(Bitmap.CompressFormat.JPEG, 50, out);
//					Bitmap miniImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//					//Bitmap image = BitmapFactory.decodeFile(imagePath);
					Bitmap miniImage = getThumbnail(uri);
					goalImage.setImageBitmap(miniImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		c.close();
	}

	   public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
	        InputStream input = this.getContentResolver().openInputStream(uri);

	        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
	        onlyBoundsOptions.inJustDecodeBounds = true;
	        onlyBoundsOptions.inDither=true;//optional
	        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
	        input.close();
	        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
	            return null;

	        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

	        double THUMBNAIL_SIZE = 2;
	        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
	        //double ratio = 1.0;

	        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
	        bitmapOptions.inDither=true;//optional
	        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        input = getContentResolver().openInputStream(uri);
	        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
	        input.close();
	        return bitmap;
	    }

	    private static int getPowerOfTwoForSampleRatio(double ratio){
	        int k = Integer.highestOneBit((int)Math.floor(ratio));
	        if(k==0) return 1;
	        else return k;
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_viewgoals, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.viewgoals_menu_addgoal:
			Intent k = new Intent(ViewGoalsActivity.this, AddGoalActivity.class);
			ViewGoalsActivity.this.startActivity(k);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
