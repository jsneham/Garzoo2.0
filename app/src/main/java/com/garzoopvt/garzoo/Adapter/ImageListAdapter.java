package com.garzoopvt.garzoo.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.URLs;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import static android.graphics.BitmapFactory.decodeFile;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {


    private Context activity;
    private ArrayList<ImageVideo> data;
    private static LayoutInflater inflater = null;
    //public ImageLoader imageLoader;
    View.OnClickListener listener;
    public ImageListAdapter(Context a, ArrayList<ImageVideo> d, View.OnClickListener listener) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
        // imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        try {
            ArrayList<ImageVideo> imageList= data;
            holder.remove.setOnClickListener(listener);
            holder.remove.setTag(R.string.btn_view_position, i);

            if(imageList.get(i).getType().equals("video")){
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imageList.get(i).getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                if(thumb!=null)
                    holder.list_image.setImageBitmap(thumb);
                else {
                    Picasso.get().load(imageList.get(i-1).getPath()).into(holder.list_image);
                }
            }
            else{
                if(imageList.get(i).getPath().contains(URLs.IMAGE_URL)){
                    Picasso.get().load(imageList.get(i).getPath()).into(holder.list_image);
                }
                else{
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    holder.list_image.setImageBitmap(decodeFile(imageList.get(i).getPath(), options));

//                    holder.list_image.setImageBitmap(
//                            decodeSampledBitmapFromResource(imageList.get(i).getPath(), 100, 100));
                }


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView list_image, remove;
        public VideoView list_video;


        public ViewHolder(View vi) {
            super(vi);

            list_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image
            list_video = (VideoView) vi.findViewById(R.id.list_video); // thumb image
            remove = (ImageView) vi.findViewById(R.id.remove); // thumb image

        }


    }

    public class ImageFileFilter implements FileFilter {
        File file;
        private final String[] okFileExtensions = new String[] {
                "jpg",
                "png",
                "gif",
                "jpeg"
        };

        public ImageFileFilter(File newfile) {
            this.file = newfile;
        }

        public boolean accept(File file) {
            for (String extension: okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }

    }

    public static Bitmap decodeSampledBitmapFromResource(String resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}