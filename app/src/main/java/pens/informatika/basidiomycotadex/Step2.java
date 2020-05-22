package pens.informatika.basidiomycotadex;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Step2 extends Fragment {
 
	TextView insCari;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_step2, container, false);
         
        insCari = (TextView) rootView.findViewById(R.id.textCari);
        
        String cari = "1. Buka navigation drawer\n"
				+ "2. Pilih menu pencarian\n" + "3. Masukkan kata kunci\n"
				+ "4. Pilih hasil yang diinginkan\n" + "5. Lihat hasilnya";

		insCari.setText(cari);
        return rootView;
    }
}
