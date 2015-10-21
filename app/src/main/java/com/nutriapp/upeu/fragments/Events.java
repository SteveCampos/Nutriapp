package com.nutriapp.upeu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutriapp.upeu.R;
import com.nutriapp.upeu.adapters.TabsAdapter;

/**
 * Created by Kelvin on 29/09/2015.
 */
public class Events extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        TabsAdapter tabsAdapter = new TabsAdapter(getActivity().getSupportFragmentManager());
        tabsAdapter.addFragment(new FavoriteTabFragment(1), "Upeu");
        tabsAdapter.addFragment(new FavoriteTabFragment(2), "Local");
        tabsAdapter.addFragment(new FavoriteTabFragment(3), "Nacional");
        tabsAdapter.addFragment(new FavoriteTabFragment(4), "Internacional");
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        return rootView;
    }
}
