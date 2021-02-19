package com.garzoopvt.garzoo.Profile.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Profile.Activity.UserProfileEditActivity;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.LocaleHelper;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Transaltion;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileFragment extends Fragment {


    //UI
    private ImageView ivEditNumber, ivEditBio;
    private TextView tvLanguage,tvName, tvAge, tvGender, tvMobile, tvLocation, tvMyListing, tvInterested, tvContactUs, tvSignOut, tvblockList,tvFeedback;
    private LinearLayout tvUpdate;
    private AdView adView;
    private EditText otp1, otp2, otp3, otp4;

    //Instance
    private Context context;
    private SessionManager sessionManager;
    private ProfileViewModel mViewModel;
    private View view;

    //variable
    private String user_id, username, gender, age, loacation, mobile,otp;
    private String mLanguageCode = "en";
    private String latitude, longitude;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
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

                Intent intent= new Intent(context, UserProfileEditActivity.class);
                startActivity(intent);

            }
        });

        ivEditNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openChnageNumberPopup();

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

        if(is_update.equals("1")) tvUpdate.setVisibility(View.VISIBLE);
        else tvUpdate.setVisibility(View.GONE);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApkVersionPopUpComplusory(false);

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


    private void getApkVersionPopUpComplusory(boolean compulsory) {

        try {
            androidx.appcompat.app.AlertDialog alertDialog=null;
            alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setIcon(R.drawable.garzoo_tm)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.update))
                    .setPositiveButton(R.string.ho,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(URLs.Share_URL_));
                                    startActivity(i);

                                }

                            })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!compulsory) dialog.dismiss();
                        }
                    }).show();

            if(compulsory)alertDialog.setCancelable(true); else alertDialog.setCancelable(false);
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.theme2));
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.black));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }



    private void setData() {
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        username = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        mobile = sessionManager.getFromSessionManager(SessionManager.MOBILE);
        gender = sessionManager.getFromSessionManager(SessionManager.GENDER);
        age = sessionManager.getFromSessionManager(SessionManager.AGE);
        loacation = sessionManager.getFromSessionManager(SessionManager.LOCATION_final);
        Transaltion.translate(loacation, tvLocation);

        tvMobile.setText(mobile);
        tvName.setText(username);
        tvGender.setText(gender);
        tvAge.setText(age);
        // tvLocation.setText(loacation);
    }


    private void openChnageNumberPopup() {

        View view = getLayoutInflater().inflate(R.layout.changenumber, null);
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        Button btnNumber = (Button) view.findViewById(R.id.btnNumber);
        EditText etMobile = (EditText) view.findViewById(R.id.etMobile);
        otp1 = (EditText) view.findViewById(R.id.editTextone);
        otp2 = (EditText) view.findViewById(R.id.editTexttwo);
        otp3 = (EditText) view.findViewById(R.id.editTextthree);
        otp4 = (EditText) view.findViewById(R.id.editTextfour);

        otp1.addTextChangedListener(new Custom_dialogbox());
        otp2.addTextChangedListener(new Custom_dialogbox());
        otp3.addTextChangedListener(new Custom_dialogbox());
        otp4.addTextChangedListener(new Custom_dialogbox());

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etMobile.getText().toString())) {
                    userMobileCheck(etMobile);

                } else {
                    etMobile.setError(getString(R.string.err_phone));
                    etMobile.setFocusable(true);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibleNameLayout(etMobile, alertDialog);

            }
        });
    }
    public class Custom_dialogbox implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 1) {
                if (otp1.length() == 1) {
                    otp2.requestFocus();
                }

                if (otp2.length() == 1) {
                    otp3.requestFocus();
                }
                if (otp3.length() == 1) {
                    otp4.requestFocus();
                }
            } else if (editable.length() == 0) {
                if (otp4.length() == 0) {
                    otp3.requestFocus();
                }
                if (otp3.length() == 0) {
                    otp2.requestFocus();
                }
                if (otp2.length() == 0) {
                    otp1.requestFocus();
                }

            }
        }
    }

    private void userMobileCheck(EditText etMobile) {

        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);

        mViewModel.user_mobile_change(etMobile.getText().toString(), latitude, longitude,user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        //if (!(response.equals("") || response.equals("empty"))) {
                        response.body(); // have your all data
                        otp = response.body().string();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Utils.showToast(context, getString(R.string.no_internet_msg));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    public void visibleNameLayout(EditText etMobile, android.app.AlertDialog alertDialog) {

        String enteredOtp = otp1.getText().toString().trim() +
                otp2.getText().toString().trim()
                + otp3.getText().toString().trim()
                + otp4.getText().toString().trim();


        if (enteredOtp.trim().equals(otp)) {
            userMobileUpdate(etMobile,alertDialog);

        } else {
            otp1.setError(getString(R.string.err_otp));
            otp2.setError(getString(R.string.err_otp));
            otp3.setError(getString(R.string.err_otp));
            otp4.setError(getString(R.string.err_otp));
        }


    }

    private void userMobileUpdate(EditText etMobile, android.app.AlertDialog alertDialog) {

        mViewModel.user_mobile_update(etMobile.getText().toString(), user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body().string().equals("success")) {
                            alertDialog.dismiss();
                            mobile = etMobile.getText().toString();
                            sessionManager.setToSessionManager(SessionManager.MOBILE, mobile);
                            tvMobile.setText(mobile);
                        }
                        else {
                            Toast.makeText(context, "अयशस्वी, कृपया पुन्हा प्रयत्न करा",Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Utils.showToast(context, getString(R.string.no_internet_msg));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}