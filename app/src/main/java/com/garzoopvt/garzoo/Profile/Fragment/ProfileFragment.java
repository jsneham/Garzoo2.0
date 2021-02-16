package com.garzoopvt.garzoo.Profile.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.LocaleHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {

    private String mLanguageCode = "en";
    private ProfileViewModel mViewModel;
    private View view;
    private TextView tvLanguage,tvSignOut;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment, container, false);

        init();

        return view;
    }

    private void init() {
        tvLanguage = view.findViewById(R.id.tvLanguage);
        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateLanguage();


            }
        });

        tvSignOut = view.findViewById(R.id.tvSignOut);
        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).logout();

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