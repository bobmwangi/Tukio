package ke.co.tukio.tukio;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ke.co.tukio.tukio.HomeFragment;

public class FragmentHome2 extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    public static FragmentHome2 newInstance() {
        FragmentHome2 fragment = new FragmentHome2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_main, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

//        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getActivity(setSupportActionBar(toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new AllEventsFragment(), "All events");
        adapter.addFrag(new FavouriteEventsFragment(), "Favourites");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
