package com.garzoopvt.garzoo.Profile.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import com.garzoopvt.garzoo.Login.ViewModel.LoginViewModel;
import com.garzoopvt.garzoo.Login.data.LoginResult;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ContactUsFragment extends Fragment {

    private ProfileViewModel mViewModel;

    View view;
    private Context context;
    private RelativeLayout rlLayout;
    private EditText etName, etEmail, etMobile, etEnquiry;
    private final int REQ_CODE = 100;
    private Button btnSubmit;

    SessionManager sessionManager;
    ImageView gifEnquiry, gifEmail;

    public ContactUsFragment() {
        //this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_us_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        init();

        return view;
    }

    private void init() {
        context = getContext();
        sessionManager = new SessionManager(context);

        rlLayout = view.findViewById(R.id.rlLayout);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        etName = (EditText) view.findViewById(R.id.etName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etMobile = (EditText) view.findViewById(R.id.etMobile);
        etEnquiry = (EditText) view.findViewById(R.id.etEnquiry);
        gifEnquiry = view.findViewById(R.id.gifEnquiry);
        gifEmail = view.findViewById(R.id.gifEmail);


        gifEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, etEnquiry);
                startVoiceInput("Enquiry", 2);
            }
        });

        gifEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, etEmail);
                startVoiceInput("Email", 1);
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateRecord())
                    addRecord();
            }
        });

        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setError(null);
            }
        });
        etEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setError(null);
            }
        });
        etEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEnquiry.setError(null);
            }
        });
        etMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMobile.setError(null);
            }
        });
    }


    private void startVoiceInput(String type, int i) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String  mLanguageCode = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageCode+ URLs.language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.help_text));
        try {
            startActivityForResult(intent, i);
        } catch (ActivityNotFoundException a) {

        }
    }


    private boolean validateRecord() {
//        if (TextUtils.isEmpty(etName.getText().toString()) || etName.getText().toString().equals("")) {
//            etName.setError("Enter name");
//            etName.requestFocus();
//            return false;
//        } else if (TextUtils.isEmpty(etMobile.getText().toString()) || etMobile.getText().toString().equals("")
//                || etMobile.getText().toString().length() < 10) {
//            etMobile.setError("Enter Phone number");
//            etMobile.requestFocus();
//            return false;
//        } else

//            if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
//            etEmail.setError("वैध ईमेल प्रविष्ट करा");
//            etMobile.requestFocus();
//            return false;
//        } else

        if (TextUtils.isEmpty(etEnquiry.getText().toString())) {
            etEnquiry.setError(getString(R.string.contact_error));
            etEnquiry.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etEmail.setText(result.get(0));
                }
                break;
            }

            case 2: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etEnquiry.setText(result.get(0));
                }
                break;
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(ContactUsViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void addRecord() {

        Call<ResponseBody> call = mViewModel.contactUs(sessionManager.getFromSessionManager(SessionManager.MOBILE),
                etEmail.getText().toString(), etEnquiry.getText().toString(),
                sessionManager.getFromSessionManager(SessionManager.USERNAME));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            if (response.equals("success")) {
                                etEmail.setText("");
                                etEnquiry.setText("");
                                etMobile.setText("");
                                etName.setText("");
                                //Toast.makeText(context, response,Toast.LENGTH_SHORT).show();

                                Utils.openSnackBar(getString(R.string.sucess), view);
                            } else Utils.openSnackBar(getString(R.string.err_login), view);

                        } catch (Exception e) {
                            Utils.openSnackBar(getString(R.string.err_login), view);
                            e.printStackTrace();
                        }
                    } else {
                        Utils.openSnackBar(getString(R.string.err_login), view);
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());

            }
        });


    }


}