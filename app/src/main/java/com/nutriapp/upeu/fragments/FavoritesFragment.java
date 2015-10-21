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


public class FavoritesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        TabsAdapter tabsAdapter = new TabsAdapter(getActivity().getSupportFragmentManager());
        tabsAdapter.addFragment(new FavoriteTabFragment(1), "Favorite 1");
        tabsAdapter.addFragment(new FavoriteTabFragment(2), "Favorite 2");
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }
}
