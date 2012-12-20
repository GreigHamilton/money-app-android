package com.greighamilton.moneymanagement.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.dialogs.ViewCategoriesOptionsDialog;

/**
 * @see http://android.artemzin.ru/?p=7
 *
 */
public class CategoryIncomeListFragment extends ListFragment implements com.greighamilton.moneymanagement.dialogs.ViewCategoriesOptionsDialog.OptionsDialogListener {

	private DatabaseHelper db;
	private Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabaseHelper.getInstance(getActivity());
		c = db.getIncomeCategories();
		ListAdapter listAdapter = new CategoryAdapter(getActivity(), c);
		this.setListAdapter(listAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.simple_list_fragment, container, false);
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		showOptionsDialog(v.getTag(R.id.list_item_category).toString());
	}

	private class CategoryAdapter extends CursorAdapter {
		private LayoutInflater inflater;
		
		public CategoryAdapter(Context context, Cursor cursor) {
			super(context, cursor, CursorAdapter.NO_SELECTION);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			view.setTag(R.id.list_item_category, c.getInt(DatabaseHelper.CATEGORY_ID));
			
			TextView categoryName = (TextView) view.findViewById(R.id.list_category_name);
			TextView categoryType = (TextView) view.findViewById(R.id.list_category_type);
			TextView categoryColor = (TextView) view.findViewById(R.id.list_category_color);
			TextView categoryDescription = (TextView) view.findViewById(R.id.list_category_description);
			
			categoryName.setText(c.getString(DatabaseHelper.CATEGORY_NAME));
			categoryType.setText(Integer.toString(c.getInt(DatabaseHelper.CATEGORY_TYPE)));
			categoryColor.setText(c.getString(DatabaseHelper.CATEGORY_COLOUR));
			categoryDescription.setText(c.getString(DatabaseHelper.CATEGORY_DESCRIPTION));
			
			TextView box = (TextView) view.findViewById(R.id.list_category_box);
			box.setBackgroundColor(Color.parseColor(c.getString(DatabaseHelper.CATEGORY_COLOUR)));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_item_category, null);
		}
	}

	private void showOptionsDialog(String id) {
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        ViewCategoriesOptionsDialog optionsDialog = new ViewCategoriesOptionsDialog(id);
        optionsDialog.show(fm, "fragment_categories_options_dialog");
    }

    @Override
    public void onFinishOptionsDialog(String inputText) {
    }
}