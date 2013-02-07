package com.greighamilton.moneymanagement.utilities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class AddGoalActivity extends Activity {

	private static final int SELECT_PHOTO = 100;
	private DatabaseHelper db;
	
	private String imagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoal);

		db = DatabaseHelper.getInstance(this);
		imagePath = "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_addgoal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.goal_menu_cancel:
			finish();
			break;

		case R.id.goal_menu_save:
			
			try {

				// Get name data
				String name = ((EditText) findViewById(R.id.add_goal_name))
						.getText().toString();

				// Get amount to save data
				float needed = (Float
						.parseFloat(((EditText) findViewById(R.id.add_goal_amount_to_save))
								.getText().toString()));

				// Get amount saved data
				float saved = (Float
						.parseFloat(((EditText) findViewById(R.id.add_goal_amount_saved))
								.getText().toString()));

				String image = imagePath; // TODO add image

				db.addGoal(name, needed, saved, image);

			} catch (Exception e) {
				Toast.makeText(this, "Please complete all fields",
						Toast.LENGTH_SHORT).show();
			}
			
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clickImage(View v) {
		Toast.makeText(this, "This feature is not supported yet.", Toast.LENGTH_SHORT).show();
//		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//		photoPickerIntent.setType("image/*");
//		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	public void clickSave(View v) {
		Toast.makeText(this, "Click save", Toast.LENGTH_LONG).show();
	}
	
	public void imageClick(View v) {
		
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				imagePath = selectedImage.toString();

				Log.i("Image path: ", imagePath);
//				String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//				Cursor cursor = getContentResolver().query(selectedImage,
//						filePathColumn, null, null, null);
//				cursor.moveToFirst();
//
//				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//				String filePath = cursor.getString(columnIndex);
//				cursor.close();
//
//				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
//				imagePath = filePath;
			}
		}
	}
}
