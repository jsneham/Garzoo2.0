package com.garzoopvt.garzoo.Profile.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.LocaleHelper;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {


    //UI
    private ImageView ivEditNumber, ivEditBio;
    private TextView tvLanguage,tvName, tvAge, tvGender, tvMobile, tvLocation, tvMyListing, tvInterested, tvContactUs, tvSignOut, tvblockList,tvFeedback;
    private LinearLayout tvUpdate;
    private AdView adView;

    //Instance
    private Context context;
    private SessionManager sessionManager;
    private ProfileViewModel mViewModel;
    private View view;

    //variable
    private String user_id, username, gender, age, loacation, mobile,otp;
    private String mLanguageCode = "en";



    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment, container, false);

        init();
        getBannerAds();

        return view;
    }

    private void init() {

        context = getContext();
        sessionManager = new SessionManager(context);
        String is_update= sessionManager.getFromSessionManager(SessionManager.IS_UPDATE);

        tvLanguage = view.findViewById(R.id.tvLanguage);
        tvSignOut = view.findViewById(R.id.tvSignOut);
        tvUpdate = view.findViewById(R.id.tvUpdate);
        tvContactUs = view.findViewById(R.id.tvContactUs);
        tvFeedback = view.findViewById(R.id.tvFeedback);
        tvblockList = view.findViewById(R.id.tvblockList);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvMyListing = view.findViewById(R.id.tvMyListing);
        tvName = view.findViewById(R.id.tvName);
        tvInterested = view.findViewById(R.id.tvInterested);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvMobile = view.findViewById(R.id.tvMobile);
        ivEditBio = view.findViewById(R.id.ivEditBio);
        ivEditNumber = view.findViewById(R.id.ivEditNumber);



        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateLanguage();


            }
        });


        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).logout();

            }
        });


        ivEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent= new Intent(context, UserProfileEditActivity.class);
//                startActivity(intent);

            }
        });

        ivEditNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                openChnageNumberPopup();

            }
        });

        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ContactUsFragment(), "ContactUsFragment").commit();
            }
        });

        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new FeedbackFragment(),"FeedbackFragment").commit();
            }
        });

        tvMyListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListCategoryFragment(), "MyListCategoryFragment").commit();
            }
        });

        tvInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new InterestedFragment(),"InterestedFragment").commit();

            }
        });

        tvblockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new BlockedListFragment(),"InterestedFragment").commit();

            }
        });

    }


    private void getBannerAds() {
        adView = view.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                //Toast.makeText(context, adError.getCode() + ", "+ adError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                //Toast.makeText(context, "onAdOpened", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Toast.makeText(context, "onAdClicked", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(context, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Toast.makeText(context, "onAdClosed", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }


    public void updateLanguage() {




        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.Theme_AppCompat_Light_Dialog);
        builder.setTitle(R.string.lang);


        final String items[] = {"English", "Marathi", "Hindi"};

        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case 0:
                                mLanguageCode="en";
                                break;
                            case 1:
                                mLanguageCode="mr";
                                break;
                            case 2:
                                mLanguageCode="hi";
                                break;
                        }

                        LocaleHelper.setLocale(getContext(), mLanguageCode);
                        getActivity().recreate();
                        dialog.dismiss();
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic here
                        // dismiss dialog too
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
// display dialog
        dialog.show();


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}