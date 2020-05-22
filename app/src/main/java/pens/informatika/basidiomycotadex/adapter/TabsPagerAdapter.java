package pens.informatika.basidiomycotadex.adapter;

import pens.informatika.basidiomycotadex.Step1;
import pens.informatika.basidiomycotadex.Step2;
import pens.informatika.basidiomycotadex.Step3;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new Step1();
        case 1:
            // Games fragment activity
            return new Step2();
        case 2:
            // Movies fragment activity
            return new Step3();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}
