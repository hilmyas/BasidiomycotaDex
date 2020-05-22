package pens.informatika.basidiomycotadex;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InstructionActivity extends Fragment {

	TextView insDeteksi;
	TextView insCari;
	TextView warning;

	public InstructionActivity() {
		// konstruktor kosong diperlukan untuk subkelas fragmen
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_instruction, container,
				false);

		insDeteksi = (TextView) rootView.findViewById(R.id.textDeteksi);
		insCari = (TextView) rootView.findViewById(R.id.textCari);
		warning = (TextView) rootView.findViewById(R.id.textPeringatan);

		warning.setText("APLIKASI INI MASIH DALAM TAHAP PENGEMBANGAN.\n\n"
				+ "Dimohon untuk tetap berhati-hati saat berinteraksi dengan jamur yang Anda temukan.\n"
				+ "Spesies yang ditampilkan belum tentu tepat dengan jenis yang sebenarnya.\n\n"
				+ "Terima kasih");

		String deteksi = "1. Ambil gambar dari kamera atau gallery\n"
				+ "2. Potong gambar hingga presisi pada sisi terluar obyek\n"
				+ "3. Jika masih terdapat background, klik tombol Ambil Obyek\n"
				+ "4. Jika jamur telah terpisah dari background, klik tombol Lanjut\n"
				+ "5. Lihat hasilnya";
		String cari = "1. Buka navigation drawer\n"
				+ "2. Pilih menu pencarian\n" + "3. Masukkan kata kunci\n"
				+ "4. Pilih hasil yang diinginkan\n" + "5. Lihat hasilnya";

		insDeteksi.setText(deteksi);
		insCari.setText(cari);
		
		return rootView;
	}
}
