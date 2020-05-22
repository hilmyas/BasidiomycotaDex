package pens.informatika.basidiomycotadex;

import java.util.ArrayList;
import java.util.List;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListActivity extends Fragment {

	EditText filter;
	ListView list;
	ArrayAdapter<String> adapter;
	List<String> data = new ArrayList<String>();
	private final String DB_PATH = "/data/data/pens.informatika.basidiomycotadex/";
	private final String DB_NAME = "BasidiomycotaDexDB";

	public ListActivity() {
		// konstruktor kosong diperlukan untuk subkelas fragmen
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_list, container,
				false);

		filter = (EditText) rootView.findViewById(R.id.filter);
		list = (ListView) rootView.findViewById(R.id.daftarJamur);

		ambilData();

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String[] s;
				s = list.getItemAtPosition(position).toString().split(" ");

				Intent resultIntent = new Intent(getActivity(),
						ResultActivity.class);
				resultIntent.putExtra("namalatin", s[1] + " " + s[2]);
				startActivity(resultIntent);
			}
		});

		filter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				ListActivity.this.adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		return rootView;
	}

	public void ambilData() {
		SQLiteDatabase db;
		Cursor cursor;

		db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);

		cursor = db.rawQuery("select namalatin,namalokal from spesies", null);
		if (cursor.getCount() > 0) {
			int i = 1;
			cursor.moveToFirst();
			do {
				data.add(i + ". " + cursor.getString(0) + " ( "
						+ cursor.getString(1) + " )");
				i++;
			} while (cursor.moveToNext());
		}
		db.close();
	}
}
