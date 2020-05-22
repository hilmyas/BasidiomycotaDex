package pens.informatika.basidiomycotadex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Step3 extends Fragment {
	private final String DB_PATH = "/data/data/pens.informatika.basidiomycotadex/";
	private final String DB_NAME = "BasidiomycotaDexDB";
 
	TextView warning;
	Button next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_step3, container, false);
         
        warning = (TextView) rootView.findViewById(R.id.textPeringatan);
        next = (Button) rootView.findViewById(R.id.buttonNext);

		warning.setText("APLIKASI INI MASIH DALAM TAHAP PENGEMBANGAN.\n\n"
				+ "Dimohon untuk tetap berhati-hati saat berinteraksi dengan jamur yang Anda temukan.\n"
				+ "Spesies yang ditampilkan belum tentu tepat dengan jenis yang sebenarnya.\n\n"
				+ "Terima kasih");
		
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SQLiteDatabase db;

				db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
						SQLiteDatabase.NO_LOCALIZED_COLLATORS
								| SQLiteDatabase.CREATE_IF_NECESSARY);

				db.execSQL("update baruinstal set baru=0");
				db.close();
				
				Intent i = new Intent(getActivity(),
						MainActivity.class);
				startActivity(i);
				getActivity().finish();
			}
		});
		
        return rootView;
    }
}

