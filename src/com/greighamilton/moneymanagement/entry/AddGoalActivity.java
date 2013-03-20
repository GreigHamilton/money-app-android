package com.greighamilton.moneymanagement.entry;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

/**
 * Class for add goal activity.
 * 
 * @author Greig Hamilton
 *
 */
public class AddGoalActivity extends Activity {

	private DatabaseHelper db;
	
	private static int RESULT_LOAD_IMAGE = 1;
	private String imagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoal);

		db = DatabaseHelper.getInstance(this);
		imagePath = "";
		
		Button buttonLoadImage = (Button) findViewById(R.id.add_goal_select_image);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {
                 
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
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

				//String image = imagePath; // TODO add image

				db.addGoal(name, needed, saved, imagePath);

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
	}

	public void clickSave(View v) {
		Toast.makeText(this, "Click save", Toast.LENGTH_LONG).show();
	}
	
	public void imageClick(View v) {
		
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            
            imagePath = ""+selectedImage;
            
            Button buttonLoadImage = (Button) findViewById(R.id.add_goal_select_image);
            if (imagePath == "") {
            	buttonLoadImage.setText("No image selected");
            }
            else {
            	int indexOfFileName = imagePath.lastIndexOf('/')+1;
            	buttonLoadImage.setText("Image selected");
            }
            
        }    
    }
}
