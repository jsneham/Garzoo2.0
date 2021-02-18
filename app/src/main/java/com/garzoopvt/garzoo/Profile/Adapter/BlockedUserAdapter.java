package com.garzoopvt.garzoo.Profile.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAd;
import com.garzoopvt.garzoo.Adapter.EndListViewHolder;
import com.garzoopvt.garzoo.Adapter.LoadingViewHolder;
import com.garzoopvt.garzoo.Adapter.SearchExhaustedViewHolder;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;
import com.garzoopvt.garzoo.R;

import java.util.ArrayList;
import java.util.List;

public class BlockedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int ITEM_BANNER = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int EndList_TYPE = 5;

    public final int ITEM_PER_ADV = 8;

    private List<BlockedPeople> mBlockedPeople;
    private List<NativeAd> mAdItems;
    private Context mContext;
    private String user_id;

    public EventListener listener;

    public interface EventListener {
        void onEvent(int data);
    }


    //    public MyListingAdapter(Context context, ArrayList<DashboardList> userArrayList, String user_id, View.OnClickListener listener ) {
    public BlockedUserAdapter(Context mContext, String user_id, EventListener listener) {
        this.mContext = mContext;
        // this.userArrayList = userArrayList;
        this.user_id = user_id;
        this.listener = listener;
        mBlockedPeople = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;

        switch (viewType) { // viewType is the view type constant
            case LIST_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blocked_row, viewGroup, false);
                return new RecyclerViewViewHolder(view);
            }
            case LOADING_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            case EXHAUSTED_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }
            case EndList_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_endlist, viewGroup, false);
                return new EndListViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blocked_row, viewGroup, false);
                return new RecyclerViewViewHolder(view);
            }
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        try {
            int itemViewType = getItemViewType(i);
            if (itemViewType == LIST_TYPE) {
                RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;
                BlockedPeople user = (BlockedPeople) mBlockedPeople.get(i);

                viewHolder.name.setText(String.format("%1$s %2$s", user.getName()));

                viewHolder.submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // RemoveBlock(user.getBlock_record_id(), mContext);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return mBlockedPeople == null ? 0 : mBlockedPeople.size();
    }


    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (mBlockedPeople.size() > position) {
            if (mBlockedPeople.get(position).getName().equals("LOADING...")) {
                return LOADING_TYPE;
            } else if (mBlockedPeople.get(position).getName().equals("EXHAUSTED...")) {
                return EXHAUSTED_TYPE;
            } else {
                return LIST_TYPE;
            }
        } else {
            type = EndList_TYPE;
        }
        return type;
    }


      /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void setList(List<BlockedPeople> rents) {
        mBlockedPeople = rents;
        notifyDataSetChanged();
    }

    public void setQueryExhausted() {
        hideLoading();
        BlockedPeople exhausted = new BlockedPeople();
        exhausted.setName("EXHAUSTED...");
        mBlockedPeople.add(exhausted);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        if (isLoading()) {
            if (mBlockedPeople.get(0).getName().equals("LOADING...")) {
                mBlockedPeople.remove(mBlockedPeople.size() - 1);
            }
        }
        if (isLoading()) {
            if (mBlockedPeople.get(mBlockedPeople.size() - 1).getName().equals("LOADING...")) {
                mBlockedPeople.remove(mBlockedPeople.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void displayOnlyLoading() {
        clearRecipesList();
        BlockedPeople recipe = new BlockedPeople();
        recipe.setName("LOADING...");
        mBlockedPeople.add(recipe);
        notifyDataSetChanged();
    }


    private void clearRecipesList() {
        if (mBlockedPeople == null) {
            mBlockedPeople = new ArrayList<>();
        } else {
            mBlockedPeople.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading() {
        if (mBlockedPeople == null) {
            mBlockedPeople = new ArrayList<>();
        }
        if (!isLoading()) {
            BlockedPeople recipe = new BlockedPeople();
            recipe.setName("LOADING...");
            mBlockedPeople.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    private boolean isLoading() {
        if (mBlockedPeople != null) {
            if (mBlockedPeople.size() > 0) {
                if (mBlockedPeople.get(mBlockedPeople.size() - 1).getName().equals("LOADING...")) {
                    return true;
                }
            }
        }
        return false;
    }

    public BlockedPeople getSelected(int position) {
        if (mBlockedPeople != null) {
            if (mBlockedPeople.size() > 0) {
                return mBlockedPeople.get(position);
            }
        }
        return null;
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


    public void openAlertPoup(String result, Context context) {
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