package pens.informatika.basidiomycotadex;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Step1 extends Fragment {
	TextView insDeteksi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_step1, container, false);
         
        insDeteksi = (TextView) rootView.findViewById(R.id.textDeteksi);
		
		String deteksi = "1. Ambil gambar dari kamera atau gallery\n"
				+ "2. Potong gambar hingga presisi pada sisi terluar obyek\n"
				+ "3. Jika masih terdapat background, klik tombol Ambil Obyek\n"
				+ "4. Jika jamur telah terpisah dari background, klik tombol Lanjut\n"
				+ "5. Lihat hasilnya";

		insDeteksi.setText(deteksi);
        return rootView;
    }
}