package com.garzoopvt.garzoo.Profile.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileEditActivity extends AppCompatActivity {

    private TextInputEditText etName, etLname, etAge, etLoation;
    private AppCompatAutoCompleteTextView etDistrict, etTaluka, etArea, etState;
    private String latitude, longitude;
    private SessionManager sessionManager;
    private String user_id, username;
    private RadioButton rbmale, rbfemale;
    private RadioGroup rbGender;
    private String gender = "Male";
    private String gender_id = "1";
    private Context context = this;
    private ImageView giffName, giflName, gifAge, gifState, gifDist, gifTalk, gifArea, gifAddress;
    private ProfileViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        init();
    }

    private void init() {

        sessionManager = new SessionManager(getApplicationContext());


        etName = (TextInputEditText) findViewById(R.id.etFname);
        etLname = (TextInputEditText) findViewById(R.id.etLname);
        etLoation = (TextInputEditText) findViewById(R.id.etLoation);
        etAge = (TextInputEditText) findViewById(R.id.etAge);
        etState = findViewById(R.id.etState);
        etDistrict = findViewById(R.id.etDistrict);
        rbGender = findViewById(R.id.rbGender);
        rbfemale = findViewById(R.id.rbfemale);
        rbmale = findViewById(R.id.rbmale);
        etTaluka = findViewById(R.id.etTaluka);
        etArea = findViewById(R.id.etArea);

        gifAddress = findViewById(R.id.gifAddress);
        gifArea = findViewById(R.id.gifArea);
        gifAge = findViewById(R.id.gifAge);
        gifTalk = findViewById(R.id.gifTalk);
        gifDist = findViewById(R.id.gifDist);
        gifState = findViewById(R.id.gifState);
        giflName = findViewById(R.id.giflName);
        giffName = findViewById(R.id.giffName);
        gender = getString(R.string.male);

        gifAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etLoation);
                startVoiceInput("Address", 8);
            }
        });

        gifArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etArea);
                startVoiceInput("Area", 7);
            }
        });

        gifAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etAge);
                startVoiceInput("Age", 3);
            }
        });

        gifTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etTaluka);
                startVoiceInput("Taluka", 6);
            }
        });

        gifDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etDistrict);
                startVoiceInput("District", 5);
            }
        });

        gifState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etState);
                startVoiceInput("State", 4);
            }
        });

        giflName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etLname);
                startVoiceInput("FName", 2);
            }
        });

        giffName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(getApplicationContext(), etName);
                startVoiceInput("LName", 1);
            }
        });


        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);


        rbGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbfemale) {
                    gender = getString(R.string.Female);
                    gender_id = "2";
                } else if (checkedId == R.id.rbmale) {
                    gender = getString(R.string.male);
                    gender_id = "1";
                }


            }
        });

//        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.xml.numeric);
//
//        mCustomKeyboard.registerEditText(R.id.etFname);
//        mCustomKeyboard.registerEditText(R.id.etLname);
//        mCustomKeyboard.registerEditText(R.id.etAge);
//        mCustomKeyboard.registerEditTextAppCompatAutoCompleteTextView(R.id.etState);
//        mCustomKeyboard.registerEditTextAppCompatAutoCompleteTextView(R.id.etDistrict);
//        mCustomKeyboard.registerEditTextAppCompatAutoCompleteTextView(R.id.etArea);
//        mCustomKeyboard.registerEditTextAppCompatAutoCompleteTextView(R.id.etTaluka);
//        mCustomKeyboard.registerEditText(R.id.etLoation);

        setData();
    }

    private void setData() {
        try {
            user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
            username = sessionManager.getFromSessionManager(SessionManager.USERNAME);
            gender = sessionManager.getFromSessionManager(SessionManager.GENDER);
            String age = sessionManager.getFromSessionManager(SessionManager.AGE);
            String area = sessionManager.getFromSessionManager(SessionManager.Login_CITY);
            String taluka = sessionManager.getFromSessionManager(SessionManager.Login_TALUKA);
            String district = sessionManager.getFromSessionManager(SessionManager.DISTRICT);
            String state = sessionManager.getFromSessionManager(SessionManager.STATE);
            String address = sessionManager.getFromSessionManager(SessionManager.LOCATION_final);
//        String loacation = sessionManager.getFromSessionManager(SessionManager.LOCATION_final);
//        Transaltion.translate(loacation, tvLocation);

            String name[] = username.split(" ");
            etName.setText(name[0]);
            etLname.setText(name[1]);
            if (gender.equals(getString(R.string.Female))) rbfemale.setChecked(true);
            else rbmale.setChecked(true);
            etAge.setText(age);
            etState.setText(state);
            etDistrict.setText(district);
            etTaluka.setText(taluka);
            etArea.setText(area);
            etLoation.setText(address);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validation() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError(getString(R.string.rfname));
            etName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etLname.getText().toString())) {
            etLname.setError(getString(R.string.rlname));
            etLname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etAge.getText().toString())) {
            etAge.setError(getString(R.string.rage));
            etAge.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etState.getText().toString())) {
            etState.setError(getString(R.string.rstate));
            etState.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etDistrict.getText().toString())) {
            etDistrict.setError(getString(R.string.rcity));
            etDistrict.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etTaluka.getText().toString())) {
            etTaluka.setError(getString(R.string.rtaluka));
            etTaluka.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etArea.getText().toString())) {
            etArea.setError(getString(R.string.rgav));
            etArea.requestFocus();
            return false;
        }

//        else if (TextUtils.isEmpty(etLoation.getText().toString())) {
//            etLoation.setError(getString(R.string.raddress));
//            etLoation.requestFocus();
//            return false;
//        }
//
        else {
            return true;
        }

    }


    private void saveDataInSession() {
        sessionManager.setUid(SessionManager.USER_ID, user_id);
        sessionManager.setToSessionManager(SessionManager.USER_ID, user_id);
        sessionManager.setToSessionManager(SessionManager.USERNAME, username);
        sessionManager.setToSessionManager(SessionManager.AGE, etAge.getText().toString());
        sessionManager.setToSessionManager(SessionManager.GENDER, gender);
        sessionManager.setToSessionManager(SessionManager.STATE, etState.getText().toString());
        sessionManager.setToSessionManager(SessionManager.DISTRICT, etDistrict.getText().toString());


        String address = String.format("%1$s %2$s %3$s %4$s",  etArea.getText().toString(), etTaluka.getText().toString(),
                etDistrict.getText().toString(), etState.getText().toString());

//          %2$s  %3$s  %4$s  %5$s,
//                etArea.getText().toString(),
//                etTaluka.getText().toString(),
//                etDistrict.getText().toString(), etState.getText().toString());

        if (!address.equals("")) {
            sessionManager.setToSessionManager(SessionManager.LOCATION, address);
            sessionManager.setToSessionManager(SessionManager.LOCATION_final, address);
            sessionManager.setToSessionManager(SessionManager.Login_CITY, etArea.getText().toString());
            sessionManager.setToSessionManager(SessionManager.Login_TALUKA, etTaluka.getText().toString());
        }

    }


    private void userRegister(final String fname, final String lname) {
        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE_FIXED);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE_FIXED);

        mViewModel.register(user_id, fname, lname, gender_id, etAge.getText().toString(),
                etState.getText().toString(), etArea.getText().toString(),
                etTaluka.getText().toString(), etDistrict.getText().toString(), latitude, longitude)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                if (result.equals("success")) {
                                    username = etName.getText().toString() + " " + etLname.getText().toString();
                                    saveDataInSession();
                                    finish();

                                } else {
                                    Utils.showToast(context, "अयशस्वी, कृपया पुन्हा प्रयत्न करा");
                                }

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


    @Override
    protected void onResume() {
        super.onResume();
        // mKeyboardView.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
//        if(mKeyboardView.getVisibility()== View.VISIBLE)
//            mKeyboardView.setVisibility(View.GONE);
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
//        if (mCustomKeyboard.isCustomKeyboardVisible()) mCustomKeyboard.hideCustomKeyboard();
//        else

        this.finish();
    }


    private void startVoiceInput(String type, int i) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, URLs.language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.help_text));
        try {
            startActivityForResult(intent, i);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            switch (requestCode) {
                case 1:
                    etName.setText(result.get(0));
                    break;
                case 2:
                    etLname.setText(result.get(0));
                    break;
                case 3:
                    etAge.setText(result.get(0));
                    break;
                case 4:
                    etState.setText(result.get(0));
                    break;
                case 5:
                    etDistrict.setText(result.get(0));
                    break;
                case 6:
                    etTaluka.setText(result.get(0));
                    break;
                case 7:
                    etArea.setText(result.get(0));
                    break;
                case 8:
                    etLoation.setText(result.get(0));
                    break;


            }


        }
    }


    public void WelcomeLayout(View view) {

        if (validation()) {
            String fName = etName.getText().toString();
            String lName = etLname.getText().toString();
            userRegister(fName, lName);
        }

    }
}