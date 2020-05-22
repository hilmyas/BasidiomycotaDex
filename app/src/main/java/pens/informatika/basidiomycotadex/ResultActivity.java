package pens.informatika.basidiomycotadex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

	ImageView gambar;
	TextView namalokal, namalatin, statusracun, statusmakan, habitat, kegunaan,
			warna, bentukpayung;
	ImageLoader image;
	Uri photoUri;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		gambar = (ImageView) findViewById(R.id.gambar);
		namalokal = (TextView) findViewById(R.id.namaLokal);
		namalatin = (TextView) findViewById(R.id.namaLatin);
		statusracun = (TextView) findViewById(R.id.statusRacun);
		statusmakan = (TextView) findViewById(R.id.statusMakan);
		habitat = (TextView) findViewById(R.id.habitat);
		kegunaan = (TextView) findViewById(R.id.kegunaan);
		warna = (TextView) findViewById(R.id.warna);
		bentukpayung = (TextView) findViewById(R.id.bentukPayung);

		// load image
		context = getApplicationContext();
		image = new ImageLoader();
		image.setContext(context);
		String data = getIntent().getStringExtra("namalatin");
		if (data.equalsIgnoreCase("")) {
			loadSegmented();
		} else {
			String[] frase;
			String depan,belakang,belakang1,belakang2;
			frase=data.split(" ");
			depan=frase[0].toLowerCase();
			frase=frase[1].split("-");
			belakang1=frase[0];
			if(frase.length>1){
				belakang2=frase[1];
				belakang=belakang1+"_"+belakang2;
			}
			else{
				belakang=belakang1;
			}
			image.ambilHasil(data);
			Log.i("data kirim", data);
			gambar.setImageResource(getResources().getIdentifier("pens.informatika.basidiomycotadex:drawable/"+depan+"_"+belakang+"",null,null));
			tampilkanHasil();
		}

	}

	protected void loadSegmented() {
		try {
			image.objBitmapOri = getIntent().getParcelableExtra("image");
			lakukanProses();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Gambar tidak diterima",
					Toast.LENGTH_LONG).show();
		}
	}
	
	protected void hasilTakAda() {
		namalokal.setText("Obyek Tidak Dikenal");
		namalokal.setTextColor(Color.RED);
		namalatin.setText("( - )");
		statusracun.setText("-");
		statusmakan.setText("-");
		habitat.setText("-");
		kegunaan.setText("-");
		warna.setText("-");
		bentukpayung.setText("-");
	}

	protected void tampilkanHasil() {
		String[] s;
		String s2;

		namalokal.setText(image.hasilAkhir[2]);
		namalatin.setText(image.hasilAkhir[0]);
		statusracun.setText(image.hasilAkhir[3]);
		statusmakan.setText(image.hasilAkhir[4]);
		if (image.hasilAkhir[3].equalsIgnoreCase("tidak beracun")) {
			statusracun.setBackgroundColor(Color.rgb(100, 255, 100));
		} else if (image.hasilAkhir[3].equalsIgnoreCase("beracun")) {
			statusracun.setBackgroundColor(Color.rgb(255, 100, 100));
		} else {
			statusracun.setBackgroundColor(Color.TRANSPARENT);
		}
		if (image.hasilAkhir[4].equalsIgnoreCase("dapat dimakan")) {
			statusmakan.setBackgroundColor(Color.rgb(100, 255, 100));
		} else if (image.hasilAkhir[4].equalsIgnoreCase("tidak dapat dimakan")) {
			statusmakan.setBackgroundColor(Color.rgb(255, 100, 100));
		} else {
			statusmakan.setBackgroundColor(Color.TRANSPARENT);
		}
		s2 = "";
		s = image.hasilAkhir[5].split(",");
		for (int i = 0; i < s.length; i++) {
			s2 = s2 + "- " + s[i] + "\n";
		}
		habitat.setText(s2);
		s2 = "";
		s = image.hasilAkhir[6].split(",");
		for (int i = 0; i < s.length; i++) {
			s2 = s2 + "- " + s[i] + "\n";
		}
		kegunaan.setText(s2);
		s2 = "";
		s = image.hasilAkhir[7].split(",");
		for (int i = 0; i < s.length; i++) {
			s2 = s2 + "- " + s[i] + "\n";
		}
		warna.setText(s2);
		s2 = "";
		s = image.hasilAkhir[8].split(",");
		for (int i = 0; i < s.length; i++) {
			s2 = s2 + "- " + s[i] + "\n";
		}
		bentukpayung.setText(s2);
	}

	protected void lakukanProses() {
		try {

			image.cariTitikTengah();
			image.fokus();
			image.cariJarijari();
			image.standardisasi();
			image.sortSudut();
			image.cariColorMoment();

			image.bersihkan();
			image.match(3);
			image.komulasi();

			image.hasilAkhir();
			// Toast.makeText(getApplicationContext(), image.hasilAkhir[0]+"",
			// Toast.LENGTH_LONG).show();

			gambar.setImageBitmap(image.objBitmapTemp);
			Log.i("persentase",image.hasilAkhir[1]);
			if(Float.parseFloat(image.hasilAkhir[1])>=image.threshold){
				image.ambilHasil(image.hasilAkhir[0]);
				tampilkanHasil();
			}
			else{
				hasilTakAda();
			}
			
			Log.i("Aksi", "Menampilkan hasil");
			// image.targetObyek();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}
}