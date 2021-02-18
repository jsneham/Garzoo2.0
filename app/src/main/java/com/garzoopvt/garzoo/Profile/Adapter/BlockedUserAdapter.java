package com.garzoopvt.garzoo.MyListing.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.garzoopvt.garzoo.Activity.ExoPlayerActivity;
import com.garzoopvt.garzoo.Adapter.MultipleImagesAdapterCopy;
import com.garzoopvt.garzoo.Apis.RetrofitService;
import com.garzoopvt.garzoo.Dashboard.API.DashboardListApi;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.MyListing.Activity.MyListingInnerActivity;
import com.garzoopvt.garzoo.MyListing.Model.BlockUser;
import com.garzoopvt.garzoo.Promotion.Activity.GroupCommentListingActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Utility.URLs;
import com.garzoopvt.garzoo.Utility.UsefulIntent;
import com.garzoopvt.garzoo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class BlockedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Object> userArrayList;
    String user_id;

    View rootView;
    private static final int ITEM_PRODUCT = 0;
    private static final int ITEM_BANNER = 1;
    private static final int LOADING = 2;
    private ArrayList<Object> recyclerItems = new ArrayList<>();
    private boolean isLoadingAdded = false;

   public EventListener listener;

    public interface EventListener {
        void onEvent(int data);
    }


    //    public MyListingAdapter(Context context, ArrayList<DashboardList> userArrayList, String user_id, View.OnClickListener listener ) {
    public BlockedUserAdapter(Context context, String user_id, EventListener listener) {
        this.context = context;
        // this.userArrayList = userArrayList;
        this.user_id = user_id;
        this.listener = listener;
        this.userArrayList = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case LOADING:
                View v2 = LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false);
                return new LoadingVH(v2);
            case ITEM_PRODUCT:
                rootView = LayoutInflater.from(context).inflate(R.layout.blocked_row, parent, false);
                return new RecyclerViewViewHolder(rootView);
            case ITEM_BANNER:
            default:
                View banner = LayoutInflater.from(context).inflate(R.layout.banner_ad_container, parent, false);
                return new BannerViewViewHolder(banner);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case LOADING:
//                Do nothing
                    break;

                case ITEM_PRODUCT:
                    RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;
                    BlockUser user = (BlockUser) userArrayList.get(position);

                    viewHolder.name.setText(String.format("%1$s %2$s",  user.getFname() ,user.getLname()));
                    if (user.getLname().equals(""))
                        viewHolder.tvImage.setText(user.getFname().charAt(0) + "");
                    else
                        viewHolder.tvImage.setText(user.getFname().charAt(0) + " " + user.getLname().charAt(0));

                    viewHolder.submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RemoveBlock(user.getBlock_record_id(), context);
                        }
                    });




                    break;

                case ITEM_BANNER:
                default:
                    BannerViewViewHolder bannerViewViewHolder = (BannerViewViewHolder) holder;
                    AdView adView = (AdView) userArrayList.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerViewViewHolder.itemView;
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }

                    if (adCardView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    adCardView.addView(adView);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return userArrayList == null ? 0 : userArrayList.size();
//        return userArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
//        if (position % DashBoardFragment.ITEM_PER_ADV == 0) {
//            return ITEM_BANNER;
//        }
        if (position == userArrayList.size() - 1 && isLoadingAdded) {
            return LOADING;
        } else {
            return ITEM_PRODUCT;
        }
    }


      /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Object mc) {
        userArrayList.add(mc);
        notifyItemInserted(userArrayList.size() - 1);
    }

    public void addAll(List<Object> mcList) {
        for (Object mc : mcList) {
            add(mc);
        }
    }

    public void remove(Object city) {
        int position = userArrayList.indexOf(city);
        if (position > -1) {
            userArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Object());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = userArrayList.size() - 1;
        Object item = getItem(position);

        if (item != null) {
            userArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Object getItem(int position) {
        return userArrayList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */


    class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

        TextView tvImage, name, submit;


        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImage = itemView.findViewById(R.id.tvImage);
            name = itemView.findViewById(R.id.name);
            submit = itemView.findViewById(R.id.submit);


        }


    }

    class BannerViewViewHolder extends RecyclerView.ViewHolder {

        public BannerViewViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    private void openInnerActivity(DashboardList productArrayList) {
        Intent intent = new Intent(context, MyListingInnerActivity.class);
        intent.putExtra("data", productArrayList);
        context.startActivity(intent);
    }


    private void openShareActivity() {
        UsefulIntent.shareIntent(context);
    }

    private void openVideoActivity(Context context, DashboardList productArrayList) {
        Intent mIntent = ExoPlayerActivity.getStartIntent(context, productArrayList.getVideo());
        mIntent.putExtra("data", productArrayList.getVideo());
        mIntent.putExtra("listing_status", productArrayList.getListing_status());
        mIntent.putExtra("data_type", productArrayList.getData_type());
        mIntent.putExtra("emp_status", productArrayList.getEmp_status());
        mIntent.putExtra("pd_status", productArrayList.getPd_status());
        mIntent.putExtra("getCategory_id", productArrayList.getCategory_id());
        mIntent.putExtra("username", productArrayList.getFname() + " " + productArrayList.getLname());
        mIntent.putExtra("location", productArrayList.getAddress());
        mIntent.putExtra("description", productArrayList.getDescription());
        mIntent.putExtra("title", productArrayList.getTitle());
        mIntent.putExtra("price", productArrayList.getPrice());
        mIntent.putExtra("timestamp", Utils.formateDate(productArrayList.getDt()));


//        VideoViewPlay.openVideo(context, list.getVideo(),list.getListing_status(),list.getData_type(),list.getPd_status(),list.getCategory_id(),
//                list.getFname() + " " + list.getLname(),list.getAddress(),list.getDescription(),
//                list.getTitle(),list.getPrice(),Utils.formateDate(list.getDt()), list.getEmp_status());
        context.startActivity(mIntent);
    }


    private void createGallery(String[] images, RecyclerView rvImages, DashboardList productArrayList) {
        GridLayoutManager _sGridLayoutManager = new GridLayoutManager(context, 2);
        ArrayList<String> imagesList = new ArrayList<>();

        for (String img : images) {
            if (img.contains("video")) productArrayList.setVideo(URLs.IMAGE_URL + img);
            else imagesList.add(img);
        }

        _sGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (imagesList.size() == 1) return 2;
                else if (imagesList.size() == 3) {
                    if (position == 2) return 2;
                    else return 1;
                } else
                    return 1;

            }
        });
//        }
        rvImages.setLayoutManager(_sGridLayoutManager);
        MultipleImagesAdapterCopy rcAdapter = new MultipleImagesAdapterCopy(context, imagesList);
        rvImages.setAdapter(rcAdapter);
        rvImages.setLayoutFrozen(true);


    }

    private void openChatActivity(DashboardList productArrayList) {
        String title = productArrayList.getTitle();
        String id_ = productArrayList.getUser_id();
        String pd_id = productArrayList.getId();
        String name_ = String.format("%1$s %2$s", productArrayList.getFname(),
                productArrayList.getLname());

        if (productArrayList.getData_type().equals("P")) {
            Intent intent = new Intent(context, GroupCommentListingActivity.class);
            intent.putExtra("pd_id", pd_id);
            intent.putExtra("to_id", id_);
            intent.putExtra("to_name", name_);
            intent.putExtra("title", title);
            context.startActivity(intent);
        }
    }







    public void RemoveBlock(String record_id ,  Context context){

        DashboardListApi api = RetrofitService.cteateService(DashboardListApi.class);
        api.RemoveBlock(record_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                openAlertPoup(result,context );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

    }
    public  void openAlertPoup(String result, Context context) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(result);
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            listener.onEvent(0);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.theme2));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

}