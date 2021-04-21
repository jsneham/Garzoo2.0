package com.garzoopvt.garzoo.Login.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Login.ViewModel.LoginViewModel;
import com.garzoopvt.garzoo.Login.data.LoginResult;

import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Transaltion;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.garzoopvt.garzoo.services.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Context context=this;
    public SmsBroadcastReceiver smsBroadcastReceiver;
    private static final int REQ_USER_CONSENT = 200;
    private LoginResult responseToStore=null;

    //UI
    private RelativeLayout container;
    private EditText etMobile;
    private Button loginButton;
    private CheckBox cbTerms;
    private LinearLayout llWelcome, llOtp, llName, llHead, llroot, llInfo;
    TextView tvTitle, tvEnglish, tvMarathi, btnResend, countdownTimerText;
    private TextView tvMobile;
    private EditText otp1, otp2, otp3, otp4;
    private ImageView giffName, giflName, gifAge, gifState, gifDist, gifTalk, gifArea, gifAddress;
    private TextInputEditText  etName, etLname, etAge, etLoation;
    private AppCompatAutoCompleteTextView etDistrict, etTaluka, etArea, etState;
    private RadioGroup rbGender;

    //Data
    private String latitude, longitude;
    private SessionManager sessionManager;
    private String user_id, otp, otp_id, mobile;
    private String username = "";
    private String gender = "Male";
    private String gender_id = "1";
    private String age = "0";
    private int noOfMinutes = 1 * 45 * 1000;
    private CountDownTimer countDownTimer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(getApplicationContext());
        init();

        startSmsUserConsent();
//        etMobile.addTextChangedListener(afterTextChangedListener);

        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int length=etMobile.getText().toString().trim().length();
                if(b&& length>=10 ){
                    loginButton.setEnabled(true);
                    loginButton.setBackground(getDrawable(R.drawable.btn_shape_login));
                }
                else{
                    loginButton.setEnabled(false);
                    loginButton.setBackground(getDrawable(R.drawable.btn_shape_login_disable));
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(cbTerms.isChecked()){
                    if (Utils.isConnectedToInternet(getApplicationContext())) {
                        login();
                    } else {
                        Utils.showToast(getApplicationContext(), getString(R.string.no_internet_msg));
                    }
                }

                else
                    Utils.openSnackBar(getString(R.string.terms),container );
            }
        });
    }



    private void init() {
        llName = findViewById(R.id.llName);
        llInfo = findViewById(R.id.llInfo);
        llroot = findViewById(R.id.llroot);
        llOtp = findViewById(R.id.llOtp);
        llHead = findViewById(R.id.llHead);
        llWelcome = findViewById(R.id.llWelcome);
        etMobile = findViewById(R.id.etMobile);
        cbTerms = findViewById(R.id.cbTerms);
        loginButton = findViewById(R.id.login);
        container = findViewById(R.id.container);

        btnResend = findViewById(R.id.btnResend);
        countdownTimerText = findViewById(R.id.countdownTimerText);
        cbTerms = findViewById(R.id.cbTerms);
        tvTitle = findViewById(R.id.tvTitle);
        tvMarathi = findViewById(R.id.tvMarathi);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvMobile = findViewById(R.id.tvMobile);

        otp1 = (EditText) findViewById(R.id.editTextone);
        otp2 = (EditText) findViewById(R.id.editTexttwo);
        otp3 = (EditText) findViewById(R.id.editTextthree);
        otp4 = (EditText) findViewById(R.id.editTextfour);
        otp1.addTextChangedListener(new Custom_dialogbox());
        otp2.addTextChangedListener(new Custom_dialogbox());
        otp3.addTextChangedListener(new Custom_dialogbox());
        otp4.addTextChangedListener(new Custom_dialogbox());

        etName = findViewById(R.id.etFname);
        etLname =  findViewById(R.id.etLname);
        etLoation = findViewById(R.id.etLoation);
        etAge =  findViewById(R.id.etAge);
        etState = findViewById(R.id.etState);
        etDistrict = findViewById(R.id.etDistrict);
        rbGender = findViewById(R.id.rbGender);
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

    }



    private void login() {

        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);

        Call<LoginResult> call= loginViewModel.login(etMobile.getText().toString(),latitude, longitude);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {

                            responseToStore = response.body();
                            saveDataInSessionAfterMobileNumberEnter(response);



                            Log.d("otp", otp);

                            tvTitle.setVisibility(View.VISIBLE);
                            llOtp.setVisibility(View.VISIBLE);
                            llWelcome.setVisibility(View.GONE);
                            llHead.setVisibility(View.GONE);
                            llroot.setVisibility(View.GONE);


                            tvMobile.setText(getString(R.string.entno) +": " + mobile);

                            startTimer(noOfMinutes, countdownTimerText, btnResend);

                        } catch (Exception e) {
                            Utils.openSnackBar(getString(R.string.err_login), container);
                            e.printStackTrace();
                        }
                    }
                    else {
                        Utils.openSnackBar(getString(R.string.err_login), container);
                    }


                }

            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable throwable) {
                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());

            }
        });

    }

    private void saveDataInSessionAfterMobileNumberEnter(Response<LoginResult> response) {

        user_id = String.valueOf(response.body().getUser_id());
        username = response.body().getUsername();
        otp = response.body().getOtp();
        otp_id = response.body().isOtp_id();
        latitude = response.body().getLatitude();
        longitude = response.body().getLongitude();
        String area = response.body().getArea();
        age = response.body().getAge();
        mobile = etMobile.getText().toString();
        String taluka = response.body().getTaluka();
        String address = response.body().getAddress();

        sessionManager.setToSessionManager(SessionManager.LATITUDE_FIXED, latitude);
        sessionManager.setToSessionManager(SessionManager.LONGITUDE_FIXED, longitude);
        sessionManager.setToSessionManager(SessionManager.Login_TALUKA, taluka);
        sessionManager.setToSessionManager(SessionManager.Login_CITY, area);
        sessionManager.setToSessionManager(SessionManager.LOCATION_final, address);
      //  sessionManager.setToSessionManager(SessionManager.AGE, age);
        sessionManager.setToSessionManager(SessionManager.OTP, otp);
       // sessionManager.setToSessionManager(SessionManager.USER_ID, user_id);
       // sessionManager.setToSessionManager(SessionManager.USERNAME, username);
       // mobile = etMobile.getText().toString();
       // sessionManager.setToSessionManager(SessionManager.MOBILE, mobile);
        sessionManager.setToSessionManager(SessionManager.OTP_STATE, "0");
    }

    public void openBrowser(View view) {
        String url = URLs.TCondition;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void visibleMobileScreen(View view) {
        stopCountdown();
        llHead.setVisibility(View.VISIBLE);
        llOtp.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        llWelcome.setVisibility(View.VISIBLE);
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }
    private void startTimer(int noOfMinutes, final TextView countdownTimerText, final TextView btnResend) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d" + getString(R.string.otp_text),
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownTimerText.setText(hms);//set
                btnResend.setVisibility(View.GONE);
                countdownTimerText.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                btnResend.setVisibility(View.VISIBLE);
                countdownTimerText.setVisibility(View.GONE);
                stopCountdown();//On finish change timer text
            }
        }.start();

    }

    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    TextWatcher afterTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable text) {

            if(!text.toString().isEmpty() &&  text.toString().length()==10) {
                loginButton.setEnabled(true);
                loginButton.setBackground(getDrawable(R.drawable.btn_shape_login));
            }




        }
    };

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
    public void visibleNameLayout(View view) {
        try {

            hideKeyboard();
            String enteredOtp = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim()
                    + otp3.getText().toString().trim()
                    + otp4.getText().toString().trim();


            if (enteredOtp.trim().equals(otp)) {
                if (username.trim().equals("")) {
                    setLocationData();
                    tvTitle.setVisibility(View.GONE);
                    llHead.setVisibility(View.GONE);
                    llroot.setVisibility(View.GONE);
                    llInfo.setVisibility(View.VISIBLE);
                    llOtp.setVisibility(View.GONE);
                    stopCountdown();
                } else {
                    stopCountdown();
                    saveDataInSession();

                    goToHome();
                }

            } else {
                otp1.setError(getString(R.string.err_otp));
                otp2.setError(getString(R.string.err_otp));
                otp3.setError(getString(R.string.err_otp));
                otp4.setError(getString(R.string.err_otp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void saveDataInSession() {
        sessionManager.setToSessionManager(SessionManager.OTP_STATE, "1");
        sessionManager.setUid(SessionManager.USER_ID, user_id);
        sessionManager.setToSessionManager(SessionManager.USER_ID, user_id);
        sessionManager.setToSessionManager(SessionManager.USERNAME, username);
        sessionManager.setToSessionManager(SessionManager.OTP_ID, otp_id);
        sessionManager.setToSessionManager(SessionManager.MOBILE, mobile);
        if (age.equals("0"))
            sessionManager.setToSessionManager(SessionManager.AGE, etAge.getText().toString());
        else
            sessionManager.setToSessionManager(SessionManager.AGE, age);
        sessionManager.setToSessionManager(SessionManager.GENDER, gender);

        String address = String.format("%1$s", etLoation.getText().toString());

     if (!address.equals("")) {
            sessionManager.setToSessionManager(SessionManager.LOCATION, address);
            sessionManager.setToSessionManager(SessionManager.LOCATION_final, address);
            sessionManager.setToSessionManager(SessionManager.Login_CITY, etArea.getText().toString());
            sessionManager.setToSessionManager(SessionManager.Login_TALUKA, etTaluka.getText().toString());
        }

    }

    private void setLocationData() {
        try {

            Transaltion.translate(sessionManager.getFromSessionManager(SessionManager.STATE), etState,context);
            Transaltion.translate(sessionManager.getFromSessionManager(SessionManager.DISTRICT), etDistrict,context);
            Transaltion.translate(sessionManager.getFromSessionManager(SessionManager.TALUKA), etTaluka,context);
            Transaltion.translate(sessionManager.getFromSessionManager(SessionManager.CITY), etArea,context);
            Transaltion.translate(sessionManager.getFromSessionManager(SessionManager.LOCATION), etLoation,context);

            sessionManager.setToSessionManager(SessionManager.LOCATION, etLoation.getText().toString());
            sessionManager.setToSessionManager(SessionManager.STATE, etState.getText().toString());
            sessionManager.setToSessionManager(SessionManager.Login_CITY, etArea.getText().toString());
            sessionManager.setToSessionManager(SessionManager.DISTRICT, etDistrict.getText().toString());
            sessionManager.setToSessionManager(SessionManager.Login_TALUKA, etTaluka.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void goToHome() {
        sessionManager = null;
        finish();
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.putExtra("user_id", user_id);
        i.putExtra("username", username);
        startActivity(i);
    }

    public void visibleRegisterLayout(View view) {
        llName.setVisibility(View.VISIBLE);
        llInfo.setVisibility(View.GONE);
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
                case  REQ_USER_CONSENT:
                    if ((resultCode == RESULT_OK) && (data != null)) {
                        //That gives all message to us.
                        // We need to get the code from inside with regex
                        String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                       // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                textViewMessage.setText(
//                        String.format("%s - %s", getString(R.string.received_message), message));
                        getOtpFromMessage(message);
                    }
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
        } else {
            return true;
        }
//        else if (TextUtils.isEmpty(etState.getText().toString())) {
//            etState.setError(getString(R.string.rstate));
//            etState.requestFocus();
//            return false;
//        } else if (TextUtils.isEmpty(etDistrict.getText().toString())) {
//            etDistrict.setError(getString(R.string.rcity));
//            etDistrict.requestFocus();
//            return false;
//        } else if (TextUtils.isEmpty(etTaluka.getText().toString())) {
//            etTaluka.setError(getString(R.string.rtaluka));
//            etTaluka.requestFocus();
//            return false;
//        } else if (TextUtils.isEmpty(etArea.getText().toString())) {
//            etArea.setError(getString(R.string.rgav));
//            etArea.requestFocus();
//            return false;
//        }

//        else if (TextUtils.isEmpty(etLoation.getText().toString())) {
//            etLoation.setError(getString(R.string.raddress));
//            etLoation.requestFocus();
//            return false;
//        }
//


    }


    private void userRegister(final String fname, final String lname) {
        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE_FIXED);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE_FIXED);

        Call<ResponseBody> call= loginViewModel.register(user_id, fname, lname, gender_id, etAge.getText().toString(),
                etState.getText().toString(), etArea.getText().toString(),
                etTaluka.getText().toString(), etDistrict.getText().toString(), latitude, longitude);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            if (result.equals("success")) {
                                username = etName.getText().toString() + " " + etLname.getText().toString();
                                saveDataInSession();
                                goToHome();
                            } else {
                                llName.setVisibility(View.GONE);
                                llWelcome.setVisibility(View.VISIBLE);
                                Toast.makeText(context, getString(R.string.failed_msg), Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());

            }
        });




    }




    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }




    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void getOtpFromMessage(String message) {
        // This will match any 4 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{4}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
              otp1.setText(String.valueOf(matcher.group(0).charAt(0)));
            otp2.setText(String.valueOf(matcher.group(0).charAt(1)));
            otp3.setText(String.valueOf(matcher.group(0).charAt(2)));
            otp4.setText(String.valueOf(matcher.group(0).charAt(3)));

//            Log.d("getOtpFromMessage:1 ", String.valueOf(matcher.group(0).charAt(0)));
//            Log.d("getOtpFromMessage:2 ", String.valueOf(matcher.group(0).charAt(1)));
//            Log.d("getOtpFromMessage:3 ", String.valueOf(matcher.group(0).charAt(2)));
//            Log.d("getOtpFromMessage:4 ", String.valueOf(matcher.group(0).charAt(3)));
        }
    }

}