package com.garzoopvt.garzoo.Dashboard.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.garzoopvt.garzoo.Dashboard.Model.HomeGridModelClass;
import com.garzoopvt.garzoo.R;

import java.util.List;

public class RecycleAdapter_GridHome extends RecyclerView.Adapter<RecycleAdapter_GridHome.MyViewHolder> {
    Context context;


    private List<HomeGridModelClass> moviesList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        //        PorterShapeImageView
        ImageView image;
        TextView name;


        public MyViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);


        }

    }


    public RecycleAdapter_GridHome(Context mainActivityContacts, List<HomeGridModelClass> moviesList) {
        this.moviesList = moviesList;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_grid_list, parent, false);


        return new MyViewHolder(itemView);


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HomeGridModelClass movie = moviesList.get(position);
        holder.name.setText(movie.getName());
       //holder.image.setImageBitmap(UsefulIntent.decodeSampledBitmapFromResource(context.getResources(), movie.getImage(), 0, 50));
        Glide.with(context).load(movie.getImage()).into(holder.image);

       // holder.image.setImageResource(movie.getImage());
//        Glide.with(context).load(movie.getImage()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


}


