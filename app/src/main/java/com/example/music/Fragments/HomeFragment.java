package com.example.music.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.music.Adapters.CategoryAdapter;
import com.example.music.Adapters.ListAdapter;
import com.example.music.Adapters.SliderAdapter;
import com.example.music.Models.AlbumModel;
import com.example.music.Models.CategoryModel;
import com.example.music.Models.ListModel;
import com.example.music.Models.SliderModel;
import com.example.music.Models.SongModel;
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
    TextView edtSearch;
    private RecyclerView rcvCategory;
    private CategoryAdapter categoryAdapter;
    private CategoryAdapter searchAdapter;
    private RecyclerView rcvList;
    private ListAdapter listAdapter;
    private RecyclerView rcvSearch;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        slider = view.findViewById(R.id.viewSlide);
        sliderIndicator = view.findViewById(R.id.slider_indicator);

        rcvSearch = view.findViewById(R.id.rcvSearch);

        rcvCategory = view.findViewById(R.id.rcvCategory);
        categoryAdapter = new CategoryAdapter(getContext());
        searchAdapter = new CategoryAdapter(getContext());

        rcvList = view.findViewById(R.id.rcvList);
        listAdapter = new ListAdapter(getContext());

        sliderList = new ArrayList<>();
        timer = new Timer();

        edtSearch = view.findViewById(R.id.txtSearch);
        LinearLayoutManager linearLayoutManagerSearch = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvSearch.setLayoutManager(linearLayoutManagerSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString().trim();

                homeViewModel.getSearchResultsData(keyword).observe(getViewLifecycleOwner(), new Observer<List<CategoryModel>>() {
                    @Override
                    public void onChanged(List<CategoryModel> categoryModels) {
                        searchAdapter.setData(categoryModels);
                    }
                });
                rcvSearch.setAdapter(searchAdapter);
            }
        });

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
        homeViewModel.getAllSongsData().observe(getViewLifecycleOwner(), allSongs -> {
            categoryAdapter.setAllSongs(allSongs);
        });
        LinearLayoutManager linearLayoutManagerCategory = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManagerCategory);

        homeViewModel.getCategoryData().observe(getViewLifecycleOwner(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                categoryAdapter.setData(categoryModels);
            }
        });

        rcvCategory.setAdapter(categoryAdapter);

        LinearLayoutManager linearLayoutManagerList = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvList.setLayoutManager(linearLayoutManagerList);

        homeViewModel.getAlbumData().observe(getViewLifecycleOwner(), new Observer<List<ListModel>>() {
            @Override
            public void onChanged(List<ListModel> listModels) {
                listAdapter.setData(listModels);
            }
        });

        listAdapter.setOnAlbumClickListener(new ListAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(AlbumModel selectedAlbum) {
                navigateToAlbumFragment(selectedAlbum);
            }
        });

        rcvList.setAdapter(listAdapter);

        return view;
    }
    private void navigateToAlbumFragment(AlbumModel selectedAlbum) {
        if (selectedAlbum != null) {
            AlbumFragment albumFragment = new AlbumFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedAlbum", selectedAlbum);
            bundle.putParcelableArrayList("allSongs", new ArrayList<>(selectedAlbum.getSongs()));
            albumFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, albumFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}