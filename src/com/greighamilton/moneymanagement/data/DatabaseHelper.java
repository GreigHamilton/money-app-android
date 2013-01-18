package com.greighamilton.moneymanagement.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Copies an already existing SQLite database into this application. Provides a
 * singleton instance to a database helper which provides cursors to the
 * database.
 * 
 * @see http
 *      ://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android
 *      -applications/
 * 
 * @author Juan-Manuel Flux�
 * 
 * @author Greig Hamilton (queries)
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final int INCOME_ID = 0;
	public static final int INCOME_NAME = 1;
	public static final int INCOME_AMOUNT = 2;
	public static final int INCOME_DATE = 3;
	public static final int INCOME_REPETITION_PERIOD = 4;
	public static final int INCOME_REPETITION_LENGTH = 5;
	public static final int INCOME_NOTES = 6;
	public static final int INCOME_CATEGORY_ID = 7;
	public static final int INCOME_NOTIFICATION_ID = 8;

	public static final int EXPENSE_ID = 0;
	public static final int EXPENSE_NAME = 1;
	public static final int EXPENSE_AMOUNT = 2;
	public static final int EXPENSE_DATE = 3;
	public static final int EXPENSE_REPETITION_PERIOD = 4;
	public static final int EXPENSE_REPETITION_LENGTH = 5;
	public static final int EXPENSE_NOTES = 6;
	public static final int EXPENSE_CATEGORY_ID = 7;
	public static final int EXPENSE_NOTIFICATION_ID = 8;
	
	public static final int CATEGORY_ID = 0;
	public static final int CATEGORY_NAME = 1;
	public static final int CATEGORY_TYPE = 2;
	public static final int CATEGORY_COLOUR = 3;
	public static final int CATEGORY_DESCRIPTION = 4;
	
	
	
	
	
	
	// The Android's default system path of your application database.
	private static final String DB_PATH = "/data/data/com.greighamilton.moneymanagement/databases/";
	private static final String DB_NAME = "moneymanagement.db";
	private static DatabaseHelper instance;
	private static boolean exists;
	private SQLiteDatabase db;
	private final Context context;

	/**
	 * Constructor
	 */
	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;

		exists = checkDatabaseExists();
	}

	/**
	 * Returns the singleton DatabaseHelper, instantiating if not yet done so.
	 * 
	 * @param context
	 *            Application context.
	 * @return The DatabaseHelper singleton.
	 */
	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
			try {
				instance.createDatabase();
			} catch (IOException ioe) {
				/*
				 * Critical error - database could not be created. This is
				 * probably due to lack of space.
				 */
				throw new Error(
						"Unable to create database, probably due to lack of space");
			}

			if (!exists) {
				return null;
			}
		}

		return instance;
	}

	/**
	 * Creates an empty database on the system and populates it from a
	 * pre-prepared database.
	 */
	private void createDatabase() throws IOException {
		if (checkDatabaseExists()) {
			/* Database exists, so we can just open it */
			instance.openDatabase();
		} else {
			/* Database does not exist - create a new one */
			this.getReadableDatabase();

			/*
			 * Set up an AsyncTask to go off and copy our pre-prepared database
			 * into the data section of this application.
			 */
			copyDatabase();
			exists = true;
			instance.openDatabase();
		}
	}

	/**
	 * Checks if the database already exists.
	 */
	public static boolean checkDatabaseExists() {
		SQLiteDatabase checkDB = null;

		/*
		 * Opening the database will fail if it doesn't exist, so we can catch
		 * and ignore that error.
		 */
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			/*
			 * Database does't exist yet - we can ignore this exception. This
			 * function is not for creating the database.
			 */
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies each database file assets-folder to a single file in the data
	 * storage for this app.
	 * 
	 * @see http
	 *      ://stackoverflow.com/questions/2860157/load-files-bigger-than-1m-
	 *      from-assets-folder/3093966#3093966
	 * @author Seva Alekseyev
	 */
	private void copyDatabase() throws IOException {
		File dbFile = new File(DB_PATH + DB_NAME);
		AssetManager am = context.getAssets();
		OutputStream os = new FileOutputStream(dbFile);
		dbFile.createNewFile();

		byte[] b = new byte[1024];
		int i, r;

		String[] Files = am.list("");
		Arrays.sort(Files);

		for (i = 1; i < 10; i++) {
			String fn = String.format("%d.db", i);
			if (Arrays.binarySearch(Files, fn) < 0)
				break;
			InputStream is = am.open(fn);
			while ((r = is.read(b)) != -1)
				os.write(b, 0, r);
			is.close();
		}
		os.close();
	}

	/**
	 * Attempts to open the database in read-write mode.
	 */
	private void openDatabase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Close the open database.
	 */
	@Override
	public synchronized void close() {
		if (db != null)
			db.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// stub if needed
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// stub if needed
	}

	/* Everything which follows is mostly query related. */
	
	// INCOME queries
	public Cursor getIncome() {
		return db.query("INCOME", null, null, null, null, null, "_id asc");
	}
	
	public Cursor getIncomeId(String id) {
		return db.query("INCOME", null, "_id="+id, null, null, null, null);
	}
	
	public Cursor getSpecifiedIncome(String month, String year, boolean allReq) {
		
		if (allReq)
			return db.query("INCOME", null, null, null, null, null, "date asc");
		else
			return db.query("INCOME", null, "date LIKE ?", new String[] {"%"+"/"+month+"/"+year}, null, null, "date asc");
	}
	
	public Cursor getIncomeByAmount(String month, String year, boolean allReq) {
		
		if (allReq)
			return db.query("INCOME", null, null, null, null, null, "amount desc");
		else
			return db.query("INCOME", null, "date LIKE ?", new String[] {"%"+"/"+month+"/"+year}, null, null, "amount desc");
	}

	public void addIncome(String name, int amount, String date, int repetition_period, int repetition_length, String notes, int categoryId, int notification_id) {
		ContentValues cv = new ContentValues(9);
		cv.put("_id", nextIncomeID());
		cv.put("name", name);
		cv.put("amount", amount);
		cv.put("date", date);
		cv.put("repetition_period", repetition_period);
		cv.put("repetition_length", repetition_length);
		cv.put("notes", notes);
		cv.put("category_id", categoryId);
		cv.put("notification_id", notification_id);

		db.insert("INCOME", null, cv);
	}
	
	public void updateIncome(String id, String name, int amount, String date, int repetition_period, int repetition_length, String notes, int categoryId, int notification_id) {
		ContentValues cv = new ContentValues(9);
		
		cv.put("name", name);
		cv.put("amount", amount);
		cv.put("date", date);
		cv.put("repetition_period", repetition_period);
		cv.put("repetition_length", repetition_length);
		cv.put("notes", notes);
		cv.put("category_id", categoryId);
		cv.put("notification_id", notification_id);

		db.update("INCOME", cv, "_id="+id, null);
	}
	
	public void deleteIncome(String id) {
		db.delete("INCOME", "_id="+id, null);
	}

	public String getIncomeName(int index) {
		Cursor c = db.rawQuery("SELECT * FROM INCOME WHERE _id = "+index, null);
		String name = "nothing :(";
		c.moveToFirst();
		return (!c.isAfterLast()) ? c.getString(INCOME_NAME) : name;
	}

	public int nextIncomeID() {
		Cursor c = db.query("INCOME", null, null, null, null, null, "_id desc");
		c.moveToFirst();
		return ((!c.isAfterLast()) ? c.getInt(INCOME_ID)+1 : 1);
	}
	
	
	
	
	// EXPENSE queries
	public Cursor getExpenses() {
			return db.query("EXPENSE", null, null, null, null, null, "_id asc");
		}
	
	public Cursor getExpensesByDate() {
		return db.query("EXPENSE", null, null, null, null, null, "date desc");
	}
	
	public Cursor getExpenseId(String id) {
		return db.query("EXPENSE", null, "_id="+id, null, null, null, null);
	}
	
	public Cursor getSpecifiedExpenses(String month, String year, boolean allReq) {
		
		if (allReq)
			return db.query("EXPENSE", null, null, null, null, null, "date asc");
		else
			return db.query("EXPENSE", null, "date LIKE ?", new String[] {"%"+"/"+month+"/"+year}, null, null, "date asc");
	}
	
	public Cursor getExpensesByAmount(String month, String year, boolean allReq) {
		
		if (allReq)
			return db.query("EXPENSE", null, null, null, null, null, "amount desc");
		else
			return db.query("EXPENSE", null, "date LIKE ?", new String[] {"%"+"/"+month+"/"+year}, null, null, "amount desc");
	}

	public void addExpense(String name, int amount, String date, int repetition_period, int repetition_length, String notes, int categoryId, int notification_id) {
		ContentValues cv = new ContentValues(9);
		cv.put("_id", nextExpenseID());
		cv.put("name", name);
		cv.put("amount", amount);
		cv.put("date", date);
		cv.put("repetition_period", repetition_period);
		cv.put("repetition_length", repetition_length);
		cv.put("notes", notes);
		cv.put("category_id", categoryId);
		cv.put("notification_id", notification_id);

		db.insert("EXPENSE", null, cv);
	}
	
	public void updateExpense(String id, String name, int amount, String date, int repetition_period, int repetition_length, String notes, int categoryId, int notification_id) {
		ContentValues cv = new ContentValues(9);
		
		cv.put("name", name);
		cv.put("amount", amount);
		cv.put("date", date);
		cv.put("repetition_period", repetition_period);
		cv.put("repetition_length", repetition_length);
		cv.put("notes", notes);
		cv.put("category_id", categoryId);
		cv.put("notification_id", notification_id);

		db.update("EXPENSE", cv, "_id="+id, null);
	}
	
	public void deleteExpense(String id) {
		db.delete("EXPENSE", "_id="+id, null);
	}

	public String getExpenseName(int index) {
		Cursor c = db.rawQuery("SELECT * FROM EXPENSE WHERE _id = "+index, null);
		String name = "nothing :(";
		c.moveToFirst();
		return (!c.isAfterLast()) ? c.getString(EXPENSE_NAME) : name;
	}

	public int nextExpenseID() {
		Cursor c = db.query("EXPENSE", null, null, null, null, null, "_id desc");
		c.moveToFirst();
		return ((!c.isAfterLast()) ? c.getInt(EXPENSE_ID)+1 : 1);
	}
	
	
	
	// CATEGORY queries
	public Cursor getIncomeCategories() {
		return db.query("CATEGORY", null, "type=0", null, null, null, "_id asc");
	}
	
	public Cursor getExpenseCategories() {
		return db.query("CATEGORY", null, "type=1", null, null, null, "_id asc");
	}
	
	public Cursor getCategories() {
		return db.query("CATEGORY", null, null, null, null, null, "_id asc");
	}
	
	public Cursor getCategoryId(String id) {
		return db.query("CATEGORY", null, "_id="+id, null, null, null, null);
	}

	public void addCategory(String name, int type, String colour, String description) {
		ContentValues cv = new ContentValues(5);
		cv.put("_id", nextCategoryID());
		cv.put("name", name);
		cv.put("type", type);
		cv.put("colour", colour);
		cv.put("description", description);

		db.insert("CATEGORY", null, cv);
	}
	
	public void updateCategory(String id, String name, int type, String colour, String description) {
		ContentValues cv = new ContentValues(5);
		
		cv.put("_id", nextCategoryID());
		cv.put("name", name);
		cv.put("type", type);
		cv.put("colour", colour);
		cv.put("description", description);

		db.update("CATEGORY", cv, "_id="+id, null);
	}
	
	public void deleteCategory(String id) {
		db.delete("CATEGORY", "_id="+id, null);
	}

	public String getCategoryName(int index) {
		Cursor c = db.rawQuery("SELECT * FROM CATEGORY WHERE _id = "+index, null);
		String name = "nothing :(";
		c.moveToFirst();
		return (!c.isAfterLast()) ? c.getString(CATEGORY_NAME) : name;
	}
	
	public String getCategoryColour(int index) {
		Cursor c = db.rawQuery("SELECT * FROM CATEGORY WHERE _id = "+index, null);
		String colour = "#A8A8A8";
		c.moveToFirst();
		return (!c.isAfterLast()) ? c.getString(CATEGORY_COLOUR) : colour;
	}

	public int nextCategoryID() {
		Cursor c = db.query("CATEGORY", null, null, null, null, null, "_id desc");
		c.moveToFirst();
		return ((!c.isAfterLast()) ? c.getInt(CATEGORY_ID)+1 : 1);
		}
	
	
	public List<String> getIncomeCategoryList() {
        List<String> categories = new ArrayList<String>();
 
        Cursor c = db.rawQuery("SELECT * FROM CATEGORY WHERE type = 0", null);
 
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
            	categories.add(c.getString(1));
            } while (c.moveToNext());
        }
 
        return categories;
    }
	
	public List<String> getExpenseCategoryList() {
        List<String> categories = new ArrayList<String>();
 
        Cursor c = db.rawQuery("SELECT * FROM CATEGORY WHERE type = 1", null);
 
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
            	categories.add(c.getString(1));
            } while (c.moveToNext());
        }
 
        return categories;
    }
	
	
	

	/* AsyncTask to create database on first run. */
	private class CreateDatabaseTask extends
			AsyncTask<Integer, Integer, Boolean> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Bla");
			dialog.setMessage("Bla 2");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
			Toast.makeText(context, "Pre Execute", Toast.LENGTH_SHORT).show();
			/*
			 * dialog = ProgressDialog.show(context,
			 * context.getString(R.string.preparing_database),
			 * context.getString(R.string.preparing_database_message), true);
			 */
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Copy database from assets
				copyDatabase();

			} catch (IOException e) {
				/* An error occurred while copying the database */
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				dialog.dismiss();
				Toast.makeText(context, "Post Execute", Toast.LENGTH_SHORT)
						.show();
			} catch (IllegalArgumentException e) {
				/*
				 * See Ticket #11. Appears to throw this error if the dialog
				 * somehow loses its parent view. Can safely be ignored in this
				 * case, we're only hiding a dialog window.
				 */
			}

			exists = true;
			instance.openDatabase();
		}
	}
}