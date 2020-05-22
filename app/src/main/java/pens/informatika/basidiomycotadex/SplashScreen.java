package pens.informatika.basidiomycotadex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	private String DB_PATH = "/data/data/pens.informatika.basidiomycotadex/";
	private String DB_NAME = "BasidiomycotaDexDB";
	int baru = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Generating().execute();

	}

	private class Generating extends AsyncTask<String, String, Boolean> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SplashScreen.this);
			pDialog.setMessage("Generating database ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {
			try {
				CopyDataBaseFromAsset();
			} catch (IOException e) {
				Log.e("Error Copy Database", e.getMessage());
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			if (result != null) {
				new Handler().postDelayed(new Runnable() {

					/*
					 * Showing splash screen with a timer. This will be useful
					 * when you want to show case your app logo / company
					 */

					@Override
					public void run() {
						// This method will be executed once the timer is over
						// Start your app main activity

						Intent i;
						if (baru == 1) {
							i = new Intent(SplashScreen.this,
									NewInstalledActivity.class);
						} else {
							i = new Intent(SplashScreen.this,
									MainActivity.class);
						}

						startActivity(i);

						// close this activity
						finish();
					}
				}, SPLASH_TIME_OUT);
			}
		}
	}

	public void CopyDataBaseFromAsset() throws IOException {

		File databaseFolder = new File(DB_PATH);

		if (!checkDatabase()) {

			if (!databaseFolder.exists()) {
				databaseFolder.mkdir();
			}

			String outputFileName = DB_PATH + DB_NAME;
			InputStream in = getApplicationContext().getAssets().open(
					"db/" + DB_NAME);
			OutputStream out = new FileOutputStream(outputFileName);

			byte[] buffer = new byte[1024];
			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			out.flush();
			out.close();
			in.close();
			baru = 1;
		}

	}

	private boolean checkDatabase() {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e) {
			// database doesn't exist yet.
		}
		return checkDB != null;
	}
}
