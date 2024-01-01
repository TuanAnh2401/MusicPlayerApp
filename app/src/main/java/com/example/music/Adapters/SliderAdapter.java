package com.example.music.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.music.Models.SliderModel;
import com.example.music.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<SliderModel> sliderModels;

    public SliderAdapter(Context context, ArrayList<SliderModel> sliderModels) {
        this.context = context;
        this.sliderModels = sliderModels;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_item, null);

        ImageView sliderImage = view.findViewById(R.id.viewSliderImage);
        TextView sliderTitle = view.findViewById(R.id.txtSlider);

        Glide.with(context).load(sliderModels.get(position).getImageUrl()).into(sliderImage);

        sliderTitle.setText(sliderModels.get(position).getSlideName());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(sliderModels != null){
            return sliderModels.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
