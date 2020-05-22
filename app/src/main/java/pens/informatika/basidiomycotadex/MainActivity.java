package pens.informatika.basidiomycotadex;

import pens.informatika.basidiomycotadex.model.NavDrawerItem;
import pens.informatika.basidiomycotadex.adapter.NavDrawerListAdapter;
import java.util.ArrayList;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	ListView menu;
	NavDrawerListAdapter menuadapter;
	ArrayList<NavDrawerItem> menudata;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	
	int utama;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// inisialisasi
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menu = (ListView) findViewById(R.id.drawer_kiri);

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		menudata = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		menudata.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Find People
		menudata.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Photos
		menudata.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Communities, Will add a counter here
		menudata.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		menu.setOnItemClickListener(new SlideMenuClickListener());

		menuadapter = new NavDrawerListAdapter(getApplicationContext(),
				menudata);
		menu.setAdapter(menuadapter);

		// mengaktifkan ikon ActionBar kiri-atas sebagai kendali drawer untuk
		// membuka-menutup jika disentuh
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// merupakan metode untuk mengaktifkan drawer saat ikon ActionBar
		// disentuh atau saat layar sebelah kiri digeser (sliding) ke kanan
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		// mDrawerLayout diberi event mDrawerToggle
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			pilihItem(0);
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (mDrawerLayout.isDrawerOpen(menu)) {
			mDrawerLayout.closeDrawer(menu);
		} else {
			if(utama==0){
				pilihItem(0);
			}
			else{
				super.onBackPressed();
			}
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			pilihItem(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// event saat ikon ActionBar disentuh: drawer akan membuka/menutup
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void pilihItem(int position) {
		// memperbarui konten utama dengan mengganti fragmen saat item drawer
		// terpilih
		Fragment fragmen;
		if (position == 1) {
			fragmen = new ListActivity();
			utama=0;
		} else if (position == 2) {
			fragmen = new SearchActivity();
			utama=0;
		} else if (position == 3) {
			fragmen = new InstructionActivity();
			utama=0;
		} else {
			fragmen = new MainMenuActivity();
			utama=1;
		}

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_konten, fragmen)
				.commit();

		// metode saat item buah-buahan dipilih; judul pada ActionBar akan
		// diperbarui kemudian drawer akan menutup
		menu.setItemChecked(position, true);
		menu.setSelection(position);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(menu);
	}

	// dua metode yang harus dipanggil saat menggunakan ActionBarDrawerToggle
	// untuk penyesuaian dan konfigurasi kondisi drawer (item terpilih)
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
