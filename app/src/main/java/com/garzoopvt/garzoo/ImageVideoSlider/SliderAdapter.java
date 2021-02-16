package com.garzoopvt.garzoo.ImageVideoSlider;


import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SliderAdapter extends PagerAdapter {

    private List<SliderItemView> itemViewList;

    public SliderAdapter() {
        this.itemViewList = new ArrayList<>();
    }

    public SliderAdapter(List<SliderItemView> list) {
        this.itemViewList = list;
    }

    public int addView (SliderItemView view) {
        return addView (view, itemViewList.size());
    }

    public int addView (SliderItemView view, int position) {
        itemViewList.add (position, view);
        return position;
    }

    public void removeAllSliders() {
        itemViewList.clear();
        notifyDataSetChanged();
    }


    public View getView(int position) {
        return itemViewList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SliderItemView itemView = itemViewList.get(position);
        if(itemView.getParent() != null) {
            ((ViewGroup)itemView.getParent()).removeView(itemView); // <- fix
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return itemViewList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)   {
        container.removeView(itemViewList.get(position % itemViewList.size()));
    }

}
