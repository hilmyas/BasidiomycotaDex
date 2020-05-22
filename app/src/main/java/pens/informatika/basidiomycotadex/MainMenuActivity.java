package pens.informatika.basidiomycotadex;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainMenuActivity extends Fragment {

	ImageButton btnkamera, btnload;
	Uri photoUri;

	public MainMenuActivity() {
		// konstruktor kosong diperlukan untuk subkelas fragmen
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_main_menu,
				container, false);

		btnkamera = (ImageButton) rootView.findViewById(R.id.buttonKamera);
		btnload = (ImageButton) rootView.findViewById(R.id.buttonLoad);

		btnkamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivityForResult(intent, 1);
				}
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

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: {
			if (resultCode == Activity.RESULT_OK) {

				Log.i("Aksi", "Mengambil gambar");
				try {
					// Describe the columns you'd like to have returned.
					// Selecting from the Thumbnails location gives you both the
					// Thumbnail Image ID, as well as the original image ID
					/*
					if (data.getData() == null) {
						String[] projection = new String[] {
								MediaStore.Images.ImageColumns._ID,
								MediaStore.Images.ImageColumns.DATA,
								MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
								MediaStore.Images.ImageColumns.DATE_TAKEN,
								MediaStore.Images.ImageColumns.MIME_TYPE };
						final Cursor cursor = getActivity()
								.getContentResolver()
								.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										projection,
										null,
										null,
										MediaStore.Images.ImageColumns.DATE_TAKEN
												+ " DESC");

						// Put it in the image view
						if (cursor.moveToFirst()) {

							// This will actually give yo uthe file path
							// location of
							// the image.
							String largeImagePath = cursor.getString(0);
							cursor.close();

							// These are the two URI's you'll be interested in.
							// They
							// give you a handle to the actual images

							photoUri = Uri
									.withAppendedPath(
											MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											String.valueOf(largeImagePath));
						}
					} else {
						photoUri = data.getData();
					}
					*/
					
					photoUri = data.getData();
					
					/*
					 * Intent ResultIntent = new
					 * Intent(MainMenuActivity.this,SegmentationActivity.class);
					 * ResultIntent.setData(photoUri);
					 * startActivity(ResultIntent);
					 */
					Intent cropIntent = new Intent(
							"com.android.camera.action.CROP");
					// indicate image type and Uri
					cropIntent.setDataAndType(photoUri, "image/*");
					// set crop properties
					//cropIntent.putExtra("crop", "true");
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
					// start the activity - we handle returning in
					// onActivityResult
					startActivityForResult(cropIntent, 2);
				} catch (Exception e) {
					Toast.makeText(getActivity(),
							"Pengambilan gambar error",
							Toast.LENGTH_SHORT).show();
					Toast.makeText(getActivity(),
							"Silakan memotret dengan aplikasi kamera Anda",
							Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		case 2: {
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				/*
				 * image.objBitmapTemp =
				 * Bitmap.createBitmap(thePic,0,0,thePic.getWidth(),
				 * thePic.getHeight()); gambarOri.setImageBitmap(thePic);
				 * btnReset.setEnabled(true);
				 */
				Intent SegmentationIntent = new Intent(getActivity(),
						SegmentationActivity.class);
				SegmentationIntent.putExtra("image", thePic);
				SegmentationIntent.putExtra("uri", photoUri);
				startActivity(SegmentationIntent);
			}
			break;
		}
		default:
			break;
		}
	}

}
