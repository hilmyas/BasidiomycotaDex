package pens.informatika.basidiomycotadex;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class SearchActivity extends Fragment {
	EditText kataKunci;
	ListView list;
	ImageButton btnSearch;
	ArrayAdapter<String> adapter;
	List<String> data = new ArrayList<String>();
	private final String DB_PATH = "/data/data/pens.informatika.basidiomycotadex/";
	private final String DB_NAME = "BasidiomycotaDexDB";

	public SearchActivity() {
		// konstruktor kosong diperlukan untuk subkelas fragmen
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_search, container,
				false);

		kataKunci = (EditText) rootView.findViewById(R.id.kataKunci);
		list = (ListView) rootView.findViewById(R.id.daftarJamur);
		btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// filter.getText().toString(), Toast.LENGTH_LONG).show();
				ambilData(kataKunci.getText().toString());
			}
		});

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
		
		return rootView;
	}

	public void ambilData(String param) {
		SQLiteDatabase db;
		Cursor cursor;

		data.clear();
		db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);

		cursor = db.rawQuery(
				"select namalatin,namalokal from spesies where namalatin like '%"
						+ param + "%' or namalokal like '%" + param
						+ "%' or statusracun like '%" + param
						+ "%' or statusmakan like '%" + param
						+ "%' or habitat like '%" + param
						+ "%' or kegunaan like '%" + param
						+ "%' or warna like '%" + param
						+ "%' or bentukpayung like '%" + param + "%'", null);
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

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		list.setAdapter(adapter);
	}
}
