package pens.informatika.basidiomycotadex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetectionActivity extends Activity {

	ImageView tmpsegmented, tampilan;
	TextView jarijari, titiktengah, standardisasi, sorted, jml, mean, stddev,
			skewness;
	TextView judulhasil1, judulhasil2, judulhasil3, judulhasil4, judulhasil5;
	TextView judulhasil1cm, judulhasil2cm, judulhasil3cm, judulhasil4cm;
	TextView judulhasil1ccdcm, judulhasil2ccdcm, judulhasil3ccdcm,
			judulhasil4ccdcm;
	TextView juduljarijari, judulstandard, judulsorted, judulmean, judulstddev,
			judulskewness;
	TextView hasil1, hasil2, hasil3, hasil4, hasil5;
	TextView hasil1cm, hasil2cm, hasil3cm, hasil4cm;
	TextView hasil1ccdcm, hasil2ccdcm, hasil3ccdcm, hasil4ccdcm;
	ImageLoader image;
	Button btnload, btnDetail, btnDetailcm;
	Uri photoUri;
	int tampildetail = 0, tampildetailcm = 0;
	int dari;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detection);

		tampilan = (ImageView) findViewById(R.id.imageAll);
		tmpsegmented = (ImageView) findViewById(R.id.imageSegmented);
		btnload = (Button) findViewById(R.id.buttonLoad);
		btnDetail = (Button) findViewById(R.id.buttonDetail);
		btnDetailcm = (Button) findViewById(R.id.buttonDetailcm);
		jarijari = (TextView) findViewById(R.id.txtJarijari);
		titiktengah = (TextView) findViewById(R.id.txtTitikTengah);
		standardisasi = (TextView) findViewById(R.id.txtStandardisasi);
		sorted = (TextView) findViewById(R.id.txtSorted);
		jml = (TextView) findViewById(R.id.txtJumlah);
		mean = (TextView) findViewById(R.id.txtMean);
		stddev = (TextView) findViewById(R.id.txtStdDev);
		skewness = (TextView) findViewById(R.id.txtSkewness);
		hasil1 = (TextView) findViewById(R.id.txtHasil1);
		hasil2 = (TextView) findViewById(R.id.txtHasil2);
		hasil3 = (TextView) findViewById(R.id.txtHasil3);
		hasil4 = (TextView) findViewById(R.id.txtHasil4);
		hasil5 = (TextView) findViewById(R.id.txtHasil5);
		judulhasil1 = (TextView) findViewById(R.id.textView11);
		judulhasil2 = (TextView) findViewById(R.id.textView12);
		judulhasil3 = (TextView) findViewById(R.id.textView13);
		judulhasil4 = (TextView) findViewById(R.id.textView14);
		judulhasil5 = (TextView) findViewById(R.id.textView15);
		hasil1cm = (TextView) findViewById(R.id.txtHasil1cm);
		hasil2cm = (TextView) findViewById(R.id.txtHasil2cm);
		hasil3cm = (TextView) findViewById(R.id.txtHasil3cm);
		hasil4cm = (TextView) findViewById(R.id.txtHasil4cm);
		judulhasil1cm = (TextView) findViewById(R.id.textView16);
		judulhasil2cm = (TextView) findViewById(R.id.textView17);
		judulhasil3cm = (TextView) findViewById(R.id.textView18);
		judulhasil4cm = (TextView) findViewById(R.id.textView19);
		juduljarijari = (TextView) findViewById(R.id.textView4);
		judulstandard = (TextView) findViewById(R.id.textView5);
		judulsorted = (TextView) findViewById(R.id.textView6);
		judulmean = (TextView) findViewById(R.id.textView8);
		judulstddev = (TextView) findViewById(R.id.textView9);
		judulskewness = (TextView) findViewById(R.id.textView10);
		hasil1ccdcm = (TextView) findViewById(R.id.txtHasil1ccdcm);
		hasil2ccdcm = (TextView) findViewById(R.id.txtHasil2ccdcm);
		hasil3ccdcm = (TextView) findViewById(R.id.txtHasil3ccdcm);
		hasil4ccdcm = (TextView) findViewById(R.id.txtHasil4ccdcm);
		judulhasil1ccdcm = (TextView) findViewById(R.id.textView20);
		judulhasil2ccdcm = (TextView) findViewById(R.id.textView21);
		judulhasil3ccdcm = (TextView) findViewById(R.id.textView22);
		judulhasil4ccdcm = (TextView) findViewById(R.id.textView23);

		// load image
		image = new ImageLoader();
		dari = getIntent().getIntExtra("dari", 0);
		if (dari == 1) {
			load();
		} else if (dari == 2) {
			loadSegmented();
		}

		tmpsegmented.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (image.objBitmapOri != null) {
					// select the object
					int x = (int) ((int) event.getX() / ((float) tmpsegmented
							.getWidth() / (float) image.objBitmapTemp
							.getWidth()));
					int y = (int) ((int) event.getY() / ((float) tmpsegmented
							.getHeight() / (float) image.objBitmapTemp
							.getHeight()));
					// int w = image.objBitmapOri.getPixel(x, y);
					/*
					 * Toast.makeText( getApplicationContext(), "R=" +
					 * Color.red(w) + " G=" + Color.green(w) + " B=" +
					 * Color.blue(w), Toast.LENGTH_LONG) .show();
					 */
					Toast.makeText(getApplicationContext(),
							"X=" + x + "; Y=" + y + ";", Toast.LENGTH_LONG)
							.show();
					/*
					 * h = image.toHsv(Color.red(w), Color.green(w),
					 * Color.blue(w)); Toast.makeText(getApplicationContext(),
					 * "H=" + h, Toast.LENGTH_LONG).show(); image.select(x, y);
					 * tmpsegmented.setImageBitmap(image.rotatedBitmap);
					 * tampilan.setImageBitmap(image.objBitmap3);
					 */
				}
				return false;
			}

		});

		btnload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, 1);
			}
		});

		btnDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tampildetail == 1) {
					juduljarijari.setVisibility(View.GONE);
					judulstandard.setVisibility(View.GONE);
					judulsorted.setVisibility(View.GONE);
					jarijari.setVisibility(View.GONE);
					standardisasi.setVisibility(View.GONE);
					sorted.setVisibility(View.GONE);
					tampildetail = 0;
				} else {
					juduljarijari.setVisibility(View.VISIBLE);
					judulstandard.setVisibility(View.VISIBLE);
					judulsorted.setVisibility(View.VISIBLE);
					jarijari.setVisibility(View.VISIBLE);
					standardisasi.setVisibility(View.VISIBLE);
					sorted.setVisibility(View.VISIBLE);
					tampildetail = 1;
				}
			}
		});

		btnDetailcm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tampildetailcm == 1) {
					judulmean.setVisibility(View.GONE);
					judulstddev.setVisibility(View.GONE);
					judulskewness.setVisibility(View.GONE);
					mean.setVisibility(View.GONE);
					stddev.setVisibility(View.GONE);
					skewness.setVisibility(View.GONE);
					tampildetailcm = 0;
				} else {
					judulmean.setVisibility(View.VISIBLE);
					judulstddev.setVisibility(View.VISIBLE);
					judulskewness.setVisibility(View.VISIBLE);
					mean.setVisibility(View.VISIBLE);
					stddev.setVisibility(View.VISIBLE);
					skewness.setVisibility(View.VISIBLE);
					tampildetailcm = 1;
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: {
			if (resultCode == RESULT_OK) {
				// tmpsegmented.setImageBitmap(null);
				photoUri = data.getData();
				load();
			}
		}
		}
	}

	protected void load() {
		photoUri = getIntent().getData();
		if (photoUri != null) {
			image.dataGambar = photoUri;
			// image.ori = photoUri;
			try {
				Context context = getApplicationContext();
				image.setContext(context);
				image.origin();
				lakukanProses();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	protected void loadSegmented() {
		try {
			Context context = getApplicationContext();

			image.setContext(context);
			image.objBitmapOri = getIntent().getParcelableExtra("image");
			lakukanProses();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	protected void lakukanProses() {
		try {
			tampilan.setImageBitmap(image.objBitmapOri);
			image.cariTitikTengah();
			image.fokus();
			titiktengah.setText("X = piksel ke-" + image.xtengah
					+ "\nY = piksel ke-" + image.ytengah
					+ "\nJari-jari Fokus = " + image.max + " piksel");

			for (int cetak = 0; cetak < 3; cetak++) {
				String r = "";

				if (cetak == 0) {
					image.cariJarijari();
				} else if (cetak == 1) {
					image.standardisasi();
				} else {
					image.sortSudut();
				}

				for (int i = 0; i < 12; i++) {
					r = r + image.r[i] + "\n";
				}

				if (cetak == 0) {
					jarijari.setText(r);
				} else if (cetak == 1) {
					standardisasi.setText(r);
				} else {
					sorted.setText(r);
				}
			}

			image.cariColorMoment();
			// jml.setVisibility(View.VISIBLE);
			// image.toLab(0, 0, 0);
			// jml.setText(image.L+"; "+image.a+"; "+image.b);
			mean.setText("aMean = " + image.aMean + "; bMean = " + image.bMean);
			stddev.setText("aStdDev = " + image.aStdDev + "; bStdDev = "
					+ image.bStdDev);
			skewness.setText("aSkewness = " + image.aSkewness
					+ "; bSkewness = " + image.bSkewness);

			image.bersihkan();
			image.match(1);
			image.komulasi();
			// skewness.setText(image.coba);
			// hasil1.setText(image.hasilKomulatif[0][0]+" "+image.hasilKomulatif[0][1]+"%");
			// hasil2.setText(image.hasil[1][1]);
			// hasil3.setText(image.hasil[2][1]);
			// hasil4.setText(image.hasil[3][1]);
			// hasil5.setText(image.hasil[4][1]);
			judulhasil1.setVisibility(View.GONE);
			hasil1.setVisibility(View.GONE);
			judulhasil2.setVisibility(View.GONE);
			hasil2.setVisibility(View.GONE);
			judulhasil3.setVisibility(View.GONE);
			hasil3.setVisibility(View.GONE);
			judulhasil4.setVisibility(View.GONE);
			hasil4.setVisibility(View.GONE);
			judulhasil5.setVisibility(View.GONE);
			hasil5.setVisibility(View.GONE);
			judulhasil1cm.setVisibility(View.GONE);
			hasil1cm.setVisibility(View.GONE);
			judulhasil2cm.setVisibility(View.GONE);
			hasil2cm.setVisibility(View.GONE);
			judulhasil3cm.setVisibility(View.GONE);
			hasil3cm.setVisibility(View.GONE);
			judulhasil4cm.setVisibility(View.GONE);
			hasil4cm.setVisibility(View.GONE);
			judulhasil1ccdcm.setVisibility(View.GONE);
			hasil1ccdcm.setVisibility(View.GONE);
			judulhasil2ccdcm.setVisibility(View.GONE);
			hasil2ccdcm.setVisibility(View.GONE);
			judulhasil3ccdcm.setVisibility(View.GONE);
			hasil3ccdcm.setVisibility(View.GONE);
			judulhasil4ccdcm.setVisibility(View.GONE);
			hasil4ccdcm.setVisibility(View.GONE);

			tampilkanHasil(1);

			image.bersihkan();
			image.match(2);
			image.komulasi();
			tampilkanHasil(2);

			image.bersihkan();
			image.match(3);
			image.komulasi();
			tampilkanHasil(3);

			image.targetObyek();
			tmpsegmented.setImageBitmap(image.objBitmapTemp);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	protected void tampilkanHasil(int fitur) {
		for (int i = 0; i < image.hasilKomulatif.length; i++) {
			if (image.hasilKomulatif[i][0] != null) {
				switch (i) {
				case 0:
					if (fitur == 1) {
						hasil1.setText(image.hasilKomulatif[0][0] + " "
								+ image.hasilKomulatif[0][1] + "%");
						judulhasil1.setVisibility(View.VISIBLE);
						hasil1.setVisibility(View.VISIBLE);
					} else if (fitur == 2) {
						hasil1cm.setText(image.hasilKomulatif[0][0] + " "
								+ image.hasilKomulatif[0][1] + "%");
						judulhasil1cm.setVisibility(View.VISIBLE);
						hasil1cm.setVisibility(View.VISIBLE);
					} else {
						hasil1ccdcm.setText(image.hasilKomulatif[0][0] + " "
								+ image.hasilKomulatif[0][1] + "%");
						judulhasil1ccdcm.setVisibility(View.VISIBLE);
						hasil1ccdcm.setVisibility(View.VISIBLE);
					}
					break;
				case 1:
					if (fitur == 1) {
						hasil2.setText(image.hasilKomulatif[1][0] + " "
								+ image.hasilKomulatif[1][1] + "%");
						judulhasil2.setVisibility(View.VISIBLE);
						hasil2.setVisibility(View.VISIBLE);
					} else if (fitur == 2) {
						hasil2cm.setText(image.hasilKomulatif[1][0] + " "
								+ image.hasilKomulatif[1][1] + "%");
						judulhasil2cm.setVisibility(View.VISIBLE);
						hasil2cm.setVisibility(View.VISIBLE);
					} else {
						hasil2ccdcm.setText(image.hasilKomulatif[1][0] + " "
								+ image.hasilKomulatif[1][1] + "%");
						judulhasil2ccdcm.setVisibility(View.VISIBLE);
						hasil2ccdcm.setVisibility(View.VISIBLE);
					}
					break;
				case 2:
					if (fitur == 1) {
						hasil3.setText(image.hasilKomulatif[2][0] + " "
								+ image.hasilKomulatif[2][1] + "%");
						judulhasil3.setVisibility(View.VISIBLE);
						hasil3.setVisibility(View.VISIBLE);
					} else if (fitur == 2) {
						hasil3cm.setText(image.hasilKomulatif[2][0] + " "
								+ image.hasilKomulatif[2][1] + "%");
						judulhasil3cm.setVisibility(View.VISIBLE);
						hasil3cm.setVisibility(View.VISIBLE);
					} else {
						hasil3ccdcm.setText(image.hasilKomulatif[2][0] + " "
								+ image.hasilKomulatif[2][1] + "%");
						judulhasil3ccdcm.setVisibility(View.VISIBLE);
						hasil3ccdcm.setVisibility(View.VISIBLE);
					}
					break;
				case 3:
					if (fitur == 1) {
						hasil4.setText(image.hasilKomulatif[3][0] + " "
								+ image.hasilKomulatif[3][1] + "%");
						judulhasil4.setVisibility(View.VISIBLE);
						hasil4.setVisibility(View.VISIBLE);
					} else if (fitur == 2) {
						hasil4cm.setText(image.hasilKomulatif[3][0] + " "
								+ image.hasilKomulatif[3][1] + "%");
						judulhasil4cm.setVisibility(View.VISIBLE);
						hasil4cm.setVisibility(View.VISIBLE);
					} else {
						hasil4ccdcm.setText(image.hasilKomulatif[3][0] + " "
								+ image.hasilKomulatif[3][1] + "%");
						judulhasil4ccdcm.setVisibility(View.VISIBLE);
						hasil4ccdcm.setVisibility(View.VISIBLE);
					}
					break;
				case 4:
					hasil5.setText(image.hasilKomulatif[4][0] + " "
							+ image.hasilKomulatif[4][1] + "%");
					judulhasil5.setVisibility(View.VISIBLE);
					hasil5.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		}
	}
}
