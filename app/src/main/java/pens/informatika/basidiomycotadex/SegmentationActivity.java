package pens.informatika.basidiomycotadex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SegmentationActivity extends Activity {

	ImageView gambarOri;
	ImageLoader image;
	Button btnSegmentation, btnContinue, btnCrop;
	Uri photoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segmentation);

		gambarOri = (ImageView) findViewById(R.id.imageOri);
		btnSegmentation = (Button) findViewById(R.id.buttonSegmentation);
		btnContinue = (Button) findViewById(R.id.buttonContinue);
		btnCrop = (Button) findViewById(R.id.buttonCrop);

		// load image
		image = new ImageLoader();
		loadSegmented();

		btnSegmentation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Aksi", "Segmentasi");
				new Clustering().execute();
			}

		});

		btnCrop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				// indicate image type and Uri
				cropIntent.setDataAndType(photoUri, "image/*");
				// set crop properties
				// cropIntent.putExtra("crop", "true");
				/*
				 * //indicate aspect of desired crop
				 * cropIntent.putExtra("aspectX", 1);
				 * cropIntent.putExtra("aspectY", 1);
				 * 
				 * //indicate output X and Y cropIntent.putExtra("outputX",
				 * 320); cropIntent.putExtra("outputY", 320);
				 */
				// retrieve data on return
				cropIntent.putExtra("return-data", true);
				// start the activity - we handle returning in onActivityResult
				startActivityForResult(cropIntent, 1);
			}

		});

		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Aksi", "Lanjut");
				Intent ResultIntent = new Intent(SegmentationActivity.this,
						ResultActivity.class);
				ResultIntent.putExtra("image", image.objBitmapTemp);
				ResultIntent.putExtra("namalatin", "");
				startActivity(ResultIntent);
				finish();
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				image.objBitmapTemp = Bitmap.createBitmap(thePic, 0, 0,
						thePic.getWidth(), thePic.getHeight());
				image.objBitmapTempSeg = Bitmap.createBitmap(thePic, 0, 0,
						thePic.getWidth(), thePic.getHeight());
				gambarOri.setImageBitmap(thePic);
			}
			break;
		}
		default:
			break;
		}
	}

	/*
	 * protected void loadUri() { photoUri = getIntent().getData(); if (photoUri
	 * != null) { image.dataGambar = photoUri; // image.ori = photoUri; try {
	 * Context context = getApplicationContext(); image.setContext(context);
	 * image.origin(); image.objBitmapTemp =
	 * Bitmap.createBitmap(image.objBitmapOri,0,0,image.objBitmapOri.getWidth(),
	 * image.objBitmapOri.getHeight());
	 * gambarOri.setImageBitmap(image.objBitmapTemp);
	 * Log.i("Aksi","Menerima gambar pada act"); } catch (Exception e) {
	 * Toast.makeText(getApplicationContext(), e.toString(),
	 * Toast.LENGTH_LONG).show(); } } }
	 */

	protected void loadSegmented() {
		try {
			Context context = getApplicationContext();

			image.setContext(context);
			image.objBitmapOri = getIntent().getParcelableExtra("image");
			photoUri = getIntent().getParcelableExtra("uri");
			image.objBitmapTemp = Bitmap.createBitmap(image.objBitmapOri, 0, 0,
					image.objBitmapOri.getWidth(),
					image.objBitmapOri.getHeight());
			image.objBitmapTempSeg = Bitmap.createBitmap(image.objBitmapOri, 0,
					0, image.objBitmapOri.getWidth(),
					image.objBitmapOri.getHeight());
			gambarOri.setImageBitmap(image.objBitmapTemp);
			Log.i("Aksi", "Menerima gambar pada act");
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Gambar tidak diterima",
					Toast.LENGTH_LONG).show();
		}
	}

	private class Clustering extends AsyncTask<String, String, Boolean> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SegmentationActivity.this);
			pDialog.setMessage("Clustering colors ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {
			image.clustering();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			if (result != null) {
				Log.i("Aksi", "Segmentasi selesai");
				if (image.hasilCluster == 1) {
					gambarOri.setImageBitmap(image.objBitmapCluster1);
					image.objBitmapTemp = Bitmap.createBitmap(
							image.objBitmapCluster1, 0, 0,
							image.objBitmapCluster1.getWidth(),
							image.objBitmapCluster1.getHeight());
				} else {
					gambarOri.setImageBitmap(image.objBitmapCluster2);
					image.objBitmapTemp = Bitmap.createBitmap(
							image.objBitmapCluster2, 0, 0,
							image.objBitmapCluster2.getWidth(),
							image.objBitmapCluster2.getHeight());
				}
			}
		}
	}
}
