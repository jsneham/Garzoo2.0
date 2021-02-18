package com.garzoopvt.garzoo.ContactUs.Fragment;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.garzoopvt.garzoo.ContactUs.ViewModel.ContactUsViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Utility.SessionManager;
import com.garzoopvt.garzoo.Utility.URLs;
import com.garzoopvt.garzoo.Utility.UsefulIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FeedbackFragment extends Fragment {

    private ContactUsViewModel mViewModel;

    View view;
    private Context context;
    private RelativeLayout rlLayout;
    private EditText etName, etEmail, etMobile, etEnquiry;
    private final int REQ_CODE = 100;
    private Button btnSubmit;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_SPEECH_INPUT_Description = 200;
    SessionManager sessionManager;
    ImageView gifEnquiry, gifEmail;

    public FeedbackFragment() {
        //this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feedback_fragment, container, false);

        init();
        return view;
    }

    private void init() {
        context = getContext();

        sessionManager=new SessionManager(context);

        rlLayout =  view.findViewById(R.id.rlLayout);
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
                UsefulIntent.checkErrorPresent(context, etEnquiry);
                startVoiceInput("Enquiry", 2);
            }
        });

        gifEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsefulIntent.checkErrorPresent(context, etEmail);
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
                etName.setError(null);            }
        });

        etEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setError(null);            }
        });

        etEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEnquiry.setError(null);            }
        });

        etMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMobile.setError(null);            }
        });
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

//        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
//            etEmail.setError("वैध ईमेल प्रविष्ट करा");
//            etMobile.requestFocus();
//            return false;
//        } else

            if (TextUtils.isEmpty(etEnquiry.getText().toString())) {
            etEnquiry.setError("कृपया आपला बहुमूल्य प्रतिक्रीया लिहा");
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

        String URL = URLs.add_feedback;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response.equals("success")) {
                        etEmail.setText("");
                        etEnquiry.setText("");
                        etMobile.setText("");
                        etName.setText("");
                        //Toast.makeText(context, response,Toast.LENGTH_SHORT).show();

                        openSnackBar(true);
                    }
                    else openSnackBar(false);


                } catch (Exception e) {
                    Log.e("volley Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError) {
                }
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", sessionManager.getFromSessionManager(SessionManager.MOBILE));
                params.put("email", etEmail.getText().toString());
                params.put("message", etEnquiry.getText().toString());
                params.put("name", sessionManager.getFromSessionManager(SessionManager.USERNAME));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


    }


    private void openSnackBar(boolean response) {
        Snackbar snackbar;
        if(response)
            snackbar = Snackbar.make(rlLayout, "तुमचा प्रतिसाद नोंदविण्यात आला आहे ! धन्यवाद", Snackbar.LENGTH_SHORT);
        else
            snackbar = Snackbar.make(rlLayout, "काहीतरी चुकीचं घडलं. पुन्हा प्रयत्न करा", Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}