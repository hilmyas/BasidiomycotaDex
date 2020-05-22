package pens.informatika.basidiomycotadex;

import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

public class ImageLoader {
	Bitmap objBitmapOri; // gambar asli
	Bitmap objBitmapTemp, objBitmapTempSeg, objBitmapRotated; // olah CCD
	float r[] = new float[12]; // jari-jari
	float cm[] = new float[6]; // warna
	int xtengah = 0, ytengah = 0, max; // letak pusat
	float aMean, bMean; // warna
	float aStdDev, bStdDev; // warna
	float aSkewness, bSkewness; // warna
	int jmlHasil = 6; // jumlah terdekat
	final float threshold=2*100/jmlHasil;
	float L, a, b; // Lab space
	int jumlah = 0; // jumlah piksel obyek
	String hasil[][] = new String[jmlHasil][2]; // data hasil
	String hasilKomulatif[][] = new String[jmlHasil][2]; // data komulatif
	int jumlahKandidat = 0;
	String hasilAkhir[] = new String[9]; // data akhir
	final float penyeimbang=4.0f;
	// String coba="";

	Uri dataGambar; // data Uri gambar
	Context c; // penerima konteks dari activity

	// untuk segmentasi
	Bitmap objBitmapCluster1, objBitmapCluster2;
	// objBitmapCluster3;
	int hasilCluster;
	float[] data = new float[2];

	private String DB_PATH = "/data/data/pens.informatika.basidiomycotadex/";
	private String DB_NAME = "BasidiomycotaDexDB";

	public void origin() {
		try {
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = this.c.getContentResolver().query(this.dataGambar,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			this.objBitmapOri = BitmapFactory.decodeFile(filePath);

			if (this.objBitmapOri.getWidth() > this.objBitmapOri.getHeight()) {
				float skala;

				if (this.objBitmapOri.getWidth() > 1600) {
					skala = 1600 / this.objBitmapOri.getWidth();
					this.objBitmapOri = Bitmap
							.createScaledBitmap(
									this.objBitmapOri,
									1600,
									(int) (this.objBitmapOri.getHeight() * skala),
									true);
					Log.i("RESIZE", "UBAH KE 1600");
				}
				if (this.objBitmapOri.getWidth() > 800) {
					skala = 800 / this.objBitmapOri.getWidth();
					this.objBitmapOri = Bitmap
							.createScaledBitmap(
									this.objBitmapOri,
									800,
									(int) (this.objBitmapOri.getHeight() * skala),
									true);
					Log.i("RESIZE", "UBAH KE 800");
				}
				if (this.objBitmapOri.getWidth() > 320) {
					skala = 320 / this.objBitmapOri.getWidth();
					this.objBitmapOri = Bitmap
							.createScaledBitmap(
									this.objBitmapOri,
									320,
									(int) (this.objBitmapOri.getHeight() * skala),
									true);
					Log.i("RESIZE", "UBAH KE 320");
				}
			} else if (this.objBitmapOri.getWidth() < this.objBitmapOri
					.getHeight()) {
				float skala;

				if (this.objBitmapOri.getHeight() > 1600) {
					skala = 1600 / this.objBitmapOri.getHeight();
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri,
							(int) (this.objBitmapOri.getWidth() * skala), 1600,
							true);
					Log.i("RESIZE", "UBAH KE 1600");
				}
				if (this.objBitmapOri.getHeight() > 800) {
					skala = 800 / this.objBitmapOri.getHeight();
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri,
							(int) (this.objBitmapOri.getWidth() * skala), 800,
							true);
					Log.i("RESIZE", "UBAH KE 800");
				}
				if (this.objBitmapOri.getHeight() > 320) {
					skala = 320 / this.objBitmapOri.getHeight();
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri,
							(int) (this.objBitmapOri.getWidth() * skala), 320,
							true);
					Log.i("RESIZE", "UBAH KE 320");
				}
			} else {
				if (this.objBitmapOri.getWidth() > 1600
						|| this.objBitmapOri.getHeight() > 1600) {
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri, 1600, 1600, true);
					Log.i("RESIZE", "UBAH KE 1600");
				}
				if (this.objBitmapOri.getWidth() > 800
						|| this.objBitmapOri.getHeight() > 800) {
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri, 800, 800, true);
					Log.i("RESIZE", "UBAH KE 800");
				}
				if (this.objBitmapOri.getWidth() > 320
						|| this.objBitmapOri.getHeight() > 320) {
					this.objBitmapOri = Bitmap.createScaledBitmap(
							this.objBitmapOri, 320, 320, true);
					Log.i("RESIZE", "UBAH KE 320");
				}
			}
			// objBitmapTemp = Bitmap.createBitmap(objBitmap2);

		} catch (Exception e) {
		}
	}

	public void setContext(Context context) {
		this.c = context;
	}

	public void saveDistance(int d, float c0, float c1) {
		this.data[0] = (float) Math.sqrt((float) Math.pow(this.a - c0, 2)
				+ (float) Math.pow(this.b - c1, 2));
		this.data[1] = d;
	}

	public void clustering() {
		Random r = new Random();
		float[][] c = new float[2][2];
		float[][] meanC = new float[2][2];
		float[] jumlah = new float[2];
		int sama = 0;

		this.objBitmapCluster1 = Bitmap.createBitmap(
				this.objBitmapTempSeg.getWidth(),
				this.objBitmapTempSeg.getHeight(), Bitmap.Config.RGB_565);
		this.objBitmapCluster2 = Bitmap.createBitmap(
				this.objBitmapTempSeg.getWidth(),
				this.objBitmapTempSeg.getHeight(), Bitmap.Config.RGB_565);
		/*
		 * this.objBitmapCluster3 = Bitmap.createBitmap(
		 * this.objBitmapOri.getWidth(), this.objBitmapOri.getHeight(),
		 * Bitmap.Config.RGB_565);
		 */
		Log.i("aksi", "membuat cluster");
		for (int i = 0; i < 2; i++) {
			c[i][0] = r.nextFloat() * (127f - (-128f)) + (-128f);
			c[i][1] = r.nextFloat() * (127f - (-128f)) + (-128f);
		}
		Log.i("aksi", "deklarasi centroid");

		do {

			for (int i = 0; i < 2; i++) {
				meanC[i][0] = 0;
				meanC[i][1] = 0;
				jumlah[i] = 0;
			}
			Log.i("aksi", "pengosongan" + this.objBitmapTempSeg.getWidth()
					+ " " + this.objBitmapTempSeg.getHeight());

			for (int i = 0; i < this.objBitmapTempSeg.getWidth(); i++) {
				for (int j = 0; j < this.objBitmapTempSeg.getHeight(); j++) {
					toLab(Color.red(this.objBitmapTempSeg.getPixel(i, j)),
							Color.green(this.objBitmapTempSeg.getPixel(i, j)),
							Color.blue(this.objBitmapTempSeg.getPixel(i, j)));
					this.objBitmapCluster1.setPixel(i, j, Color.rgb(0, 0, 0));
					this.objBitmapCluster2.setPixel(i, j, Color.rgb(0, 0, 0));
					// this.objBitmapCluster3.setPixel(i, j, Color.rgb(0, 0,
					// 0));

					for (int d = 0; d < 2; d++) {
						if (d == 0) {
							saveDistance(d, c[d][0], c[d][1]);
						} else {
							if (this.data[0] > (float) Math.sqrt((float) Math
									.pow(this.a - c[d][0], 2)
									+ (float) Math.pow(this.b - c[d][1], 2))) {
								saveDistance(d, c[d][0], c[d][1]);
							}
						}
					}

					if (this.data[1] == 0) {
						this.objBitmapCluster1.setPixel(i, j,
								Color.rgb(Color.red(this.objBitmapTempSeg
										.getPixel(i, j)), Color
										.green(this.objBitmapTempSeg.getPixel(
												i, j)), Color
										.blue(this.objBitmapTempSeg.getPixel(i,
												j))));
					} else if (this.data[1] == 1) {
						this.objBitmapCluster2.setPixel(i, j,
								Color.rgb(Color.red(this.objBitmapTempSeg
										.getPixel(i, j)), Color
										.green(this.objBitmapTempSeg.getPixel(
												i, j)), Color
										.blue(this.objBitmapTempSeg.getPixel(i,
												j))));
					} else {
						/*
						 * this.objBitmapCluster3.setPixel(i, j, Color.rgb(
						 * Color.red(this.objBitmapOri.getPixel(i, j)),
						 * Color.green(this.objBitmapOri.getPixel(i, j)),
						 * Color.blue(this.objBitmapOri.getPixel(i, j))));
						 */}
				}
			}

			for (int i = 0; i < this.objBitmapTempSeg.getWidth(); i++) {
				for (int j = 0; j < this.objBitmapTempSeg.getHeight(); j++) {

					toLab(Color.red(this.objBitmapCluster1.getPixel(i, j)),
							Color.green(this.objBitmapCluster1.getPixel(i, j)),
							Color.blue(this.objBitmapCluster1.getPixel(i, j)));
					if (!(L <= 0)) {
						meanC[0][0] += a;
						meanC[0][1] += b;
						jumlah[0]++;
					}

					toLab(Color.red(this.objBitmapCluster2.getPixel(i, j)),
							Color.green(this.objBitmapCluster2.getPixel(i, j)),
							Color.blue(this.objBitmapCluster2.getPixel(i, j)));
					if (!(L <= 0)) {
						meanC[1][0] += a;
						meanC[1][1] += b;
						jumlah[1]++;
					}

					/*
					 * toLab(Color.red(this.objBitmapCluster3.getPixel(i, j)),
					 * Color.green(this.objBitmapCluster3.getPixel(i, j)),
					 * Color.blue(this.objBitmapCluster3.getPixel(i, j))); if
					 * (!(L <= 0)) { meanC[2][0] += a; meanC[2][1] += b;
					 * jumlah[2]++; }
					 */
				}
			}

			for (int i = 0; i < 2; i++) {
				if (jumlah[i] < 1) {
					meanC[i][0] = c[i][0];
					meanC[i][1] = c[i][1];
				} else {
					meanC[i][0] /= jumlah[i];
					meanC[i][1] /= jumlah[i];
				}

				if (c[i][0] == meanC[i][0] || c[i][1] == meanC[i][1]) {
					sama = 1;
				}

				c[i][0] = meanC[i][0];
				c[i][1] = meanC[i][1];
			}

		} while (sama == 0);

		if (jumlah[0] >= jumlah[1]) {
			hasilCluster = 1;
		} else {
			hasilCluster = 2;
		}
	}

	public void cariColorMoment() {
		// int jumlah=0;
		aMean = 0;
		bMean = 0;
		aStdDev = 0;
		bStdDev = 0;
		aSkewness = 0;
		bSkewness = 0;

		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				// if (Color.red(this.objBitmapOri.getPixel(i, j)) != 0
				// && Color.green(this.objBitmapOri.getPixel(i, j)) != 0
				// && Color.blue(this.objBitmapOri.getPixel(i, j)) != 0) {
				toLab(Color.red(this.objBitmapOri.getPixel(i, j)),
						Color.green(this.objBitmapOri.getPixel(i, j)),
						Color.blue(this.objBitmapOri.getPixel(i, j)));
				if (this.L != 0) {
					this.aMean = this.aMean + this.a;
					this.bMean = this.bMean + this.b;
					this.jumlah++;
				}
				// }
			}
		}

		this.aMean = this.aMean / jumlah;
		this.bMean = this.bMean / jumlah;

		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				// if (Color.red(this.objBitmapTemp.getPixel(i, j)) != 0
				// && Color.green(this.objBitmapTemp.getPixel(i, j)) != 0
				// && Color.blue(this.objBitmapTemp.getPixel(i, j)) != 0) {
				toLab(Color.red(this.objBitmapOri.getPixel(i, j)),
						Color.green(this.objBitmapOri.getPixel(i, j)),
						Color.blue(this.objBitmapOri.getPixel(i, j)));
				if (this.L != 0) {
					this.aStdDev = this.aStdDev
							+ (float) Math.pow((this.a - this.aMean), 2);
					this.bStdDev = this.bStdDev
							+ (float) Math.pow((this.b - this.bMean), 2);
					this.aSkewness = this.aSkewness
							+ (float) Math.pow((this.a - this.aMean), 3);
					this.bSkewness = this.bSkewness
							+ (float) Math.pow((this.b - this.bMean), 3);
				}
			}
		}

		this.aStdDev = (float) Math.sqrt(this.aStdDev / (jumlah - 1));
		this.bStdDev = (float) Math.sqrt(this.bStdDev / (jumlah - 1));
		this.aSkewness = (float) Math.cbrt(this.aSkewness / (jumlah - 1));
		this.bSkewness = (float) Math.cbrt(this.bSkewness / (jumlah - 1));
	}

	public void toLab(float R, float G, float B) {

		float var_R; // R from 0 to 255
		float var_G; // G from 0 to 255
		float var_B; // B from 0 to 255
		float var_X, var_Y, var_Z;

		var_R = (R / 255); // R from 0 to 255
		var_G = (G / 255); // G from 0 to 255
		var_B = (B / 255); // B from 0 to 255

		if (var_R > 0.04045) {
			var_R = (float) (Math.pow(((var_R + 0.055) / 1.055), 2.4));
		} else {
			var_R = (float) (var_R / 12.92);
		}
		if (var_G > 0.04045) {
			var_G = (float) (Math.pow(((var_G + 0.055) / 1.055), 2.4));
		} else {
			var_G = (float) (var_G / 12.92);
		}
		if (var_B > 0.04045) {
			var_B = (float) (Math.pow(((var_B + 0.055) / 1.055), 2.4));
		} else {
			var_B = (float) (var_B / 12.92);
		}

		var_R = var_R * 100;
		var_G = var_G * 100;
		var_B = var_B * 100;

		// Observer. = 2°, Illuminant = D65
		float X = (float) (var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
		float Y = (float) (var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
		float Z = (float) (var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);

		var_X = (float) (X / 95.047); // Observer= 2°, Illuminant= D65
		var_Y = (float) (Y / 100.000);
		var_Z = (float) (Z / 108.883);

		if (var_X > 0.008856) {
			var_X = (float) Math.cbrt(var_X);
		} else {
			var_X = (float) ((7.787 * var_X) + (16f / 116));
		}
		if (var_Y > 0.008856) {
			var_Y = (float) Math.cbrt(var_Y);
		} else {
			var_Y = (float) ((7.787 * var_Y) + (16f / 116));
		}
		if (var_Z > 0.008856) {
			var_Z = (float) Math.cbrt(var_Z);
		} else {
			var_Z = (float) ((7.787 * var_Z) + (16f / 116));
		}

		this.L = (116 * var_Y) - 16;
		this.a = 500 * (var_X - var_Y);
		this.b = 200 * (var_Y - var_Z);

	}

	public void cariJarijari() {
		Matrix matrix = new Matrix();
		int counter;

		for (int a = 0; a < 12; a++) {
			matrix.postRotate(a * 30);
			this.objBitmapRotated = Bitmap.createBitmap(this.objBitmapTemp, 0,
					0, objBitmapTemp.getWidth(), objBitmapTemp.getHeight(),
					matrix, false);
			/*
			 * this.objBitmapRotated=Bitmap.createScaledBitmap( (Bitmap)
			 * this.objBitmapRotated, objBitmapTemp.getWidth()+14,
			 * objBitmapTemp.getHeight()+14, true);
			 */
			counter = 0;
			Log.i("luas", "Width=" + this.objBitmapRotated.getWidth()
					+ "; Height=" + this.objBitmapRotated.getHeight());
			for (int i = (this.objBitmapRotated.getWidth() - 1) / 2; i < this.objBitmapRotated
					.getWidth(); i++) {
				Log.i("derajat=" + (a * 30),
						Color.red(this.objBitmapRotated.getPixel(i, this.max))
								+ "; "
								+ Color.green(this.objBitmapRotated.getPixel(i,
										this.max))
								+ "; "
								+ Color.blue(this.objBitmapRotated.getPixel(i,
										this.max)));
				if (Color.red(this.objBitmapRotated.getPixel(i, this.max)) == 0
						&& Color.green(this.objBitmapRotated.getPixel(i,
								this.max)) == 0
						&& Color.blue(this.objBitmapRotated.getPixel(i,
								this.max)) == 0) {
					break;
				}
				counter++;
			}
			this.r[a] = counter;
			Log.i("Jari-jari", r[a] + "");
		}
	}

	public void standardisasi() {
		float rasio = 320;
		float max = r[0];

		for (int i = 1; i < 12; i++) {
			if (r[i] > max) {
				max = r[i];
			}
		}

		rasio /= max;

		for (int i = 0; i < 12; i++) {
			r[i] = r[i] * rasio;
		}
	}

	public void sortSudut() {
		float min = r[0];
		int idx = 0;
		float temp[] = new float[12];

		for (int i = 1; i < 12; i++) {
			if (r[i] < min) {
				min = r[i];
				idx = i;
			}
		}

		for (int i = 0; i < 12; i++) {
			temp[i] = r[i];
		}

		for (int i = 0; i < 12; i++) {
			r[i] = temp[idx];
			if (idx == 11) {
				idx = 0;
			} else {
				idx = idx + 1;
			}
		}
	}

	public void cariTitikTengah() {
		int xpower = 0, ypower = 0;
		int counter, sudah;

		// mencari power
		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				if (Color.red(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.green(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.blue(this.objBitmapOri.getPixel(i, j)) != 0) {
					xpower++;
				}
			}
		}

		for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
			for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
				if (Color.red(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.green(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.blue(this.objBitmapOri.getPixel(i, j)) != 0) {
					ypower++;
				}
			}
		}

		// mencari titik tengah
		counter = 0;
		sudah = 0;
		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				if (Color.red(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.green(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.blue(this.objBitmapOri.getPixel(i, j)) != 0) {
					counter++;
					if (counter == xpower / 2) {
						this.xtengah = i;
						sudah = 1;
						break;
					}
				}
			}
			if (sudah == 1) {
				break;
			}
		}

		counter = 0;
		sudah = 0;
		for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
			for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
				if (Color.red(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.green(this.objBitmapOri.getPixel(i, j)) != 0
						&& Color.blue(this.objBitmapOri.getPixel(i, j)) != 0) {
					counter++;
					if (counter == ypower / 2) {
						this.ytengah = j;
						sudah = 1;
						break;
					}
				}
			}
			if (sudah == 1) {
				break;
			}
		}

		Log.i("x tengah", xtengah + "");
		Log.i("y tengah", ytengah + "");
	}

	public void fokus() {
		int atas = 0, bawah = 0, kanan = 0, kiri = 0;
		int foundV = 0, foundH = 0;

		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				if (!(Color.red(this.objBitmapOri.getPixel(i, j)) == 0
						&& Color.green(this.objBitmapOri.getPixel(i, j)) == 0 && Color
							.blue(this.objBitmapOri.getPixel(i, j)) == 0)) {
					if (foundV == 0) {
						atas = j;
						bawah = j;
						foundV = 1;
					} else {
						if (atas > j) {
							atas = j;
						}
						if (bawah < j) {
							bawah = j;
						}
					}

					if (foundH == 0) {
						kiri = i;
						kanan = i;
						foundH = 1;
					} else {
						kanan = i;
					}
				}
			}
		}

		Log.i("atas temp", atas + "");
		Log.i("bawah temp", bawah + "");
		Log.i("kanan temp", kanan + "");
		Log.i("kiri temp", kiri + "");

		atas = Math.abs(this.ytengah - atas) + 1;
		bawah = Math.abs(this.ytengah - bawah) + 1;
		kanan = Math.abs(this.xtengah - kanan) + 1;
		kiri = Math.abs(this.xtengah - kiri) + 1;

		Log.i("atas", atas + "");
		Log.i("bawah", bawah + "");
		Log.i("kanan", kanan + "");
		Log.i("kiri", kiri + "");

		/*
		 * atas = 0; bawah = 0; kanan = 0; kiri = 0;
		 * 
		 * for (int i = this.xtengah; i < this.objBitmapOri.getWidth(); i++) {
		 * if (Color.red(this.objBitmapOri.getPixel(i, this.ytengah)) == 0 &&
		 * Color.green(this.objBitmapOri.getPixel(i, this.ytengah)) == 0 &&
		 * Color.blue(this.objBitmapOri.getPixel(i, this.ytengah)) == 0) {
		 * break; } kanan++; } for (int i = this.xtengah; i >= 0; i--) { if
		 * (Color.red(this.objBitmapOri.getPixel(i, this.ytengah)) == 0 &&
		 * Color.green(this.objBitmapOri.getPixel(i, this.ytengah)) == 0 &&
		 * Color.blue(this.objBitmapOri.getPixel(i, this.ytengah)) == 0) {
		 * break; } kiri++; }
		 * 
		 * for (int j = this.ytengah; j < this.objBitmapOri.getHeight(); j++) {
		 * if (Color.red(this.objBitmapOri.getPixel(this.xtengah, j)) == 0 &&
		 * Color.green(this.objBitmapOri.getPixel(this.xtengah, j)) == 0 &&
		 * Color.blue(this.objBitmapOri.getPixel(this.xtengah, j)) == 0) {
		 * break; } atas++; } for (int j = this.ytengah; j >= 0; j--) { if
		 * (Color.red(this.objBitmapOri.getPixel(this.xtengah, j)) == 0 &&
		 * Color.green(this.objBitmapOri.getPixel(this.xtengah, j)) == 0 &&
		 * Color.blue(this.objBitmapOri.getPixel(this.xtengah, j)) == 0) {
		 * break; } bawah++; }
		 * 
		 * Log.i("atas",atas+""); Log.i("bawah",bawah+"");
		 * Log.i("kanan",kanan+""); Log.i("kiri",kiri+"");
		 */

		this.max = cariMax(atas, bawah, kanan, kiri);

		tangkapObyek();
	}

	public void tangkapObyek() {
		this.objBitmapTemp = Bitmap.createBitmap(this.max * 2 + 1,
				this.max * 2 + 1, Bitmap.Config.RGB_565);

		for (int i = this.xtengah - this.max, x = 0; i <= this.xtengah
				+ this.max; i++, x++) {
			for (int j = this.ytengah - this.max, y = 0; j <= this.ytengah
					+ this.max; j++, y++) {
				if (i < 0 || j < 0 || i > this.objBitmapOri.getWidth() - 1
						|| j > this.objBitmapOri.getHeight() - 1) {
					this.objBitmapTemp.setPixel(x, y, Color.rgb(0, 0, 0));
				} else {
					this.objBitmapTemp.setPixel(x, y, Color.rgb(
							Color.red(this.objBitmapOri.getPixel(i, j)),
							Color.green(this.objBitmapOri.getPixel(i, j)),
							Color.blue(this.objBitmapOri.getPixel(i, j))));
				}
			}
		}
	}

	public void targetObyek() {
		this.objBitmapTemp = Bitmap.createBitmap(this.objBitmapOri.getWidth(),
				this.objBitmapOri.getHeight(), Bitmap.Config.RGB_565);

		for (int i = 0; i < this.objBitmapOri.getWidth(); i++) {
			for (int j = 0; j < this.objBitmapOri.getHeight(); j++) {
				if (i == this.xtengah
						|| j == this.ytengah
						|| (i == this.xtengah - this.max && (j <= this.ytengah
								+ this.max && j >= this.ytengah - this.max))
						|| (i == this.xtengah + this.max && (j <= this.ytengah
								+ this.max && j >= this.ytengah - this.max))
						|| (j == this.ytengah - this.max && (i <= this.xtengah
								+ this.max && i >= this.xtengah - this.max))
						|| (j == this.ytengah + this.max && (i <= this.xtengah
								+ this.max && i >= this.xtengah - this.max))) {
					this.objBitmapTemp.setPixel(i, j, Color.rgb(255, 0, 0));
				} else {
					this.objBitmapTemp.setPixel(i, j, Color.rgb(
							Color.red(this.objBitmapOri.getPixel(i, j)),
							Color.green(this.objBitmapOri.getPixel(i, j)),
							Color.blue(this.objBitmapOri.getPixel(i, j))));
				}
			}
		}
	}

	public int cariMax(int a, int b, int c, int d) {
		int m = a;

		if (m < b) {
			m = b;
		}
		if (m < c) {
			m = c;
		}
		if (m < d) {
			m = d;
		}

		return m;
	}

	public void match(int fitur) {

		SQLiteDatabase db;
		Cursor cursor, cursor2;
		this.cm[0] = this.aMean;
		this.cm[1] = this.bMean;
		this.cm[2] = this.aStdDev;
		this.cm[3] = this.bStdDev;
		this.cm[4] = this.aSkewness;
		this.cm[5] = this.bSkewness;

		db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);

		if (fitur == 1 || fitur == 2) {
			if (fitur == 1) {
				cursor = db.rawQuery("select * from bentuk", null);
			} else {
				cursor = db.rawQuery("select * from warna", null);
			}
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				try {
					int i2 = 0;

					do {
						float jarakTemp = 0;
						String namaTemp = "";
						for (int j = 0; j < cursor.getColumnCount(); j++) {
							if (j == cursor.getColumnCount() - 1) {
								namaTemp = cursor.getString(j);
							} else {
								if (fitur == 1) {
									jarakTemp += (float) Math.pow(
											this.r[j]
													- Float.parseFloat(cursor
															.getString(j)), 2);
								} else {
									jarakTemp += (float) Math.pow(
											this.cm[j]
													- Float.parseFloat(cursor
															.getString(j)), 2);
								}
							}
						}
						jarakTemp = (float) Math.sqrt(jarakTemp);
						if (i2 < this.jmlHasil) {
							this.hasil[i2][0] = Float.toString(jarakTemp);
							this.hasil[i2][1] = namaTemp;
							i2++;
						} else {
							urutkan(2);
							if (jarakTemp < Float.parseFloat(this.hasil[0][0])) {
								this.hasil[0][0] = Float.toString(jarakTemp);
								this.hasil[0][1] = namaTemp;
							}
						}
					} while (cursor.moveToNext());

				} catch (Exception e) {
					Toast.makeText(this.c, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		} else {
			cursor = db.rawQuery("select * from bentuk", null);
			cursor2 = db.rawQuery("select * from warna", null);
			if (cursor.getCount() > 0 && cursor2.getCount() > 0) {
				cursor.moveToFirst();
				cursor2.moveToFirst();
				try {
					int i2 = 0;

					do {
						float jarakTemp = 0;
						float jarakTempccd = 0;
						float jarakTempcm = 0;
						String namaTemp = "";
						for (int j = 0; j < cursor.getColumnCount(); j++) {
							if (j == cursor.getColumnCount() - 1) {
								namaTemp = cursor.getString(j);
							} else {
								jarakTempccd += (float) Math.pow(
										r[j]
												- Float.parseFloat(cursor
														.getString(j)), 2);
							}
						}
						jarakTempccd = (float) Math.sqrt(jarakTempccd);
						for (int j = 0; j < cursor2.getColumnCount(); j++) {
							float cmTemp[] = new float[6];
							float ar2Temp[] = new float[6];
							if (j == cursor2.getColumnCount() - 1) {
								namaTemp = cursor2.getString(j);
							} else {
								if (j == 2 || j == 3) {
									cmTemp[j] = penyeimbang * ((320 - 0) / (127 - (0))
											* (this.cm[j] - (0)) + 0);
									ar2Temp[j] = penyeimbang * ((320 - 0)
											/ (127 - (0))
											* (Float.parseFloat(cursor2
													.getString(j)) - (0)) + 0);
								} else {
									cmTemp[j] = penyeimbang * ((320 - 0) / (127 - (-128))
											* (this.cm[j] - (-128)) + 0);
									ar2Temp[j] = penyeimbang * ((320 - 0)
											/ (127 - (-128))
											* (Float.parseFloat(cursor2
													.getString(j)) - (-128)) + 0);
								}
								jarakTempcm += (float) Math.pow(cmTemp[j]
										- ar2Temp[j], 2);
							}
						}
						jarakTempcm = (float) Math.sqrt(jarakTempcm);
						jarakTemp = jarakTempccd + jarakTempcm;
						if (i2 < this.jmlHasil) {
							this.hasil[i2][0] = Float.toString(jarakTemp);
							this.hasil[i2][1] = namaTemp;
							i2++;
						} else {
							urutkan(2);
							if (jarakTemp < Float.parseFloat(this.hasil[0][0])) {
								this.hasil[0][0] = Float.toString(jarakTemp);
								this.hasil[0][1] = namaTemp;
							}
						}
					} while (cursor.moveToNext() & cursor2.moveToNext());
					cursor2.close();
				} catch (Exception e) {
					Toast.makeText(this.c, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}

		urutkan(1);
		cursor.close();
		db.close();
	}

	public void urutkan(int mode) {
		int batasIndex = this.jmlHasil - 1;
		if (mode == 1) {
			// ascending
			for (int h = 0; h < this.jmlHasil - 1; h++) {
				for (int j = 0; j < batasIndex; j++) {
					String temp[] = new String[2];
					if (Float.parseFloat(this.hasil[j][0]) > Float
							.parseFloat(this.hasil[j + 1][0])) {
						temp[0] = this.hasil[j][0];
						temp[1] = this.hasil[j][1];
						this.hasil[j][0] = this.hasil[j + 1][0];
						this.hasil[j][1] = this.hasil[j + 1][1];
						this.hasil[j + 1][0] = temp[0];
						this.hasil[j + 1][1] = temp[1];
					}
				}
				batasIndex--;
			}
		} else {
			// descending
			for (int h = 0; h < this.jmlHasil - 1; h++) {
				for (int j = 0; j < batasIndex; j++) {
					String temp[] = new String[2];
					if (Float.parseFloat(this.hasil[j][0]) < Float
							.parseFloat(this.hasil[j + 1][0])) {
						temp[0] = this.hasil[j][0];
						temp[1] = this.hasil[j][1];
						this.hasil[j][0] = this.hasil[j + 1][0];
						this.hasil[j][1] = this.hasil[j + 1][1];
						this.hasil[j + 1][0] = temp[0];
						this.hasil[j + 1][1] = temp[1];
					}
				}
				batasIndex--;
			}
		}
	}

	public void komulasi() {
		String sudahAda[] = new String[this.hasil.length];
		int pos = 0;

		for (int i = 0; i < this.hasil.length; i++) {
			int ada = 0;
			for (int j = 0; j < sudahAda.length; j++) {
				if (hasil[i][1].equalsIgnoreCase(sudahAda[j])) {
					ada = 1;
				}
			}
			if (ada == 0) {
				jumlahKandidat++;
				hasilKomulatif[pos][0] = hasil[i][1];
				sudahAda[pos] = hasil[i][1];
				float total = 0;
				for (int j = i; j < this.hasil.length; j++) {
					if (hasil[i][1].equalsIgnoreCase(hasil[j][1])) {
						total++;
					}
				}
				total = total * 100 / this.hasil.length;
				hasilKomulatif[pos][1] = Float.toString(total);
				pos++;
			}
		}
	}

	public void bersihkan() {
		this.jumlahKandidat = 0;
		for (int i = 0; i < this.hasilKomulatif.length; i++) {
			this.hasil[i][0] = null;
			this.hasil[i][1] = null;
			this.hasilKomulatif[i][0] = null;
			this.hasilKomulatif[i][1] = null;
		}
	}

	public void hasilAkhir() {
		this.hasilAkhir[0] = this.hasilKomulatif[0][0];
		this.hasilAkhir[1] = this.hasilKomulatif[0][1];

		for (int i = 1; i < this.jumlahKandidat; i++) {
			Log.i("hasilkomulatif", this.hasilKomulatif[i][0]);
			Log.i("hasilakhir", this.hasilAkhir[0]);
			if (Float.parseFloat(this.hasilAkhir[1]) < Float
					.parseFloat(this.hasilKomulatif[i][1])) {
				this.hasilAkhir[0] = this.hasilKomulatif[i][0];
				this.hasilAkhir[1] = this.hasilKomulatif[i][1];
			}
		}
	}

	public void ambilHasil(String param) {
		SQLiteDatabase db;
		Cursor cursor;

		try {
			Log.i("batas error", "1");
			db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			cursor = db.rawQuery("select * from spesies where namalatin='"
					+ param + "'", null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					hasilAkhir[0] = "(" + cursor.getString(0) + ")";
					hasilAkhir[2] = cursor.getString(1);
					hasilAkhir[3] = cursor.getString(2);
					hasilAkhir[4] = cursor.getString(3);
					hasilAkhir[5] = cursor.getString(4);
					hasilAkhir[6] = cursor.getString(5);
					hasilAkhir[7] = cursor.getString(6);
					hasilAkhir[8] = cursor.getString(7);

					for (int i = 0; i < hasilAkhir.length; i++) {
						if (i == 1) {
							continue;
						}
						if (hasilAkhir[i].equalsIgnoreCase("")) {
							hasilAkhir[i] = "-";
						}
					}
				} while (cursor.moveToNext());
			}
			
			db.close();
		} catch (Exception e) {
			Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
