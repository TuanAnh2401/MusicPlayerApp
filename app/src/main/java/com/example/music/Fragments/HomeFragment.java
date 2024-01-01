package com.example.music.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.music.Adapters.CategoryAdapter;
import com.example.music.Adapters.SliderAdapter;
import com.example.music.Models.CategoryModel;
import com.example.music.Models.SliderModel;
import com.example.music.R;
import com.example.music.Utils.SliderTimer;
import com.example.music.Viewmodel.HomeViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class HomeFragment extends Fragment {

    private ViewPager slider;
    private ArrayList<SliderModel> sliderList;
    private SliderAdapter sliderAdapter;
    private TabLayout sliderIndicator;
    private Timer timer;

    private RecyclerView rcvCategory;
    private CategoryAdapter categoryAdapter;

    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rcvCategory = view.findViewById(R.id.rcvCategory);
        categoryAdapter = new CategoryAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);

        homeViewModel.getCategoryData().observe(getViewLifecycleOwner(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                categoryAdapter.setData(categoryModels);
            }
        });

        rcvCategory.setAdapter(categoryAdapter);

        slider = view.findViewById(R.id.viewSlide);
        sliderIndicator = view.findViewById(R.id.slider_indicator);

        sliderList = new ArrayList<>();
        timer = new Timer();

        homeViewModel.getSliderData().observe(getViewLifecycleOwner(), new Observer<List<SliderModel>>() {
            @Override
            public void onChanged(List<SliderModel> sliderModels) {
                sliderList = new ArrayList<>(sliderModels);
                sliderAdapter = new SliderAdapter(getContext(), sliderList);
                slider.setAdapter(sliderAdapter);
                sliderIndicator.setupWithViewPager(slider);

                timer.scheduleAtFixedRate(new SliderTimer(getActivity(), slider, sliderList.size()), 3000, 7000);
            }
        });

        return view;
    }
}
