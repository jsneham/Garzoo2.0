package com.garzoopvt.garzoo.Rent.Activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.Rotation;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.garzoopvt.garzoo.Adapter.FilterAdapter;
import com.garzoopvt.garzoo.Adapter.ImageListAdapter;
import com.garzoopvt.garzoo.Adapter.ImageVideo;
import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.MapsActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.garzoopvt.garzoo.Rent.ViewModel.RentViewModel;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ImageCompression;
import com.garzoopvt.garzoo.Util.MultiImageUtility;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Transaltion;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DbEditRentListingActivity extends BaseActivity implements View.OnClickListener, KeyboardView.OnKeyboardActionListener {

    private uplaodDataToServer myAsyncTask;
    private RentViewModel mViewModel;
    String TAG = "VCTrim";
    ProgressDialog progressDialog;
    private ImageView ivImage, ivVdeo;
    private RelativeLayout rlLayout;
    //    private TextInputEditText etTitle, etDescription, etPrice;
    private EditText etTitle, etDescription, etPrice;
    TextView tvCategoryName, etCategory;
    private TextInputLayout etCategoryText;
    private Button btnSubmit;
    private Context context = this;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_SPEECH_INPUT_Description = 200;

    private String actionType;
    private static final int IMAGE_CAPTURE = 2;
    private String imagePath = "";
    private String from_page = "";
    private SessionManager sessionManager;
    private static File outPutFile = null;
    private String category_id, category_name;
    private String[] PERMISSION_EXTERNAL_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA};
    private ArrayList<Category> filterCategoryArrayList;
    private RecyclerView list;
    private ImageListAdapter adapter;
    private ArrayList<ImageVideo> imageList = new ArrayList<>();
    private ArrayList<ImageVideo> removeImageList = new ArrayList<>();
    private ArrayList<ImageVideo> resourceList = new ArrayList();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> flat_images = new ArrayList<>();
    private ProgressDialog dialog;
    private String selectedImagePath = "";
    private StringBuffer description = new StringBuffer("");
    private StringBuffer title = new StringBuffer("");
    private StringBuffer price = new StringBuffer("");
    private static final int REQ_CODE_SPEECH_INPUT_PRICE = 300;
    ImageView gifDescription, gifPrice, gifTitle, gifTaluka, gifArea;
    private String mobile_status = "0";
    private String available_status = "0";
    private RadioGroup rbMobile, rbGroup;
    String dstFile;
    KeyboardView mKeyboardView;
    boolean requestCamera = false;
    int image_count = 10;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private DashboardList productArrayList;
    private String user_id;
    private String[] uploaded_images;
    private String[] image_id;
    String listing_status = "2";
    RadioButton rbDene, rbGhene, rbShow, rbHide;


    private RadioGroup rbLocation;
    private LinearLayout llAddress, llHomeAddress;
    private EditText tvTaluka, tvArea;
    private EditText tvHomeTaluka, tvHomeArea;
    private String Lat, Long;
    private String Taluka = "", Area = "";
    private int LocationSelectionMode = 1;
    private RadioGroup rbAVA;
    RadioButton rbNotAva, rbAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rent_listing);
        try {
            mViewModel = ViewModelProviders.of(this).get(RentViewModel.class);
            imagelist();
            init();
            getCategoryFromServer();
            getToolBar();
            setKeyboard();

            if (checkPermission()) requestCamera = true;
            else requestPermission();

            DisableCopyPaste();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DisableCopyPaste() {
        ActionMode.Callback callback = new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        };
        etTitle.setCustomSelectionActionModeCallback(callback);
        etDescription.setCustomSelectionActionModeCallback(callback);
        etPrice.setCustomSelectionActionModeCallback(callback);
    }

    private void getToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setTitle(productArrayList.getTitle());

    }

    private void init() {
        sessionManager = new SessionManager(context);
//        Lat = sessionManager.getFromSessionManager(SessionManager.LATITUDE_FIXED);
//        Long = sessionManager.getFromSessionManager(SessionManager.LONGITUDE_FIXED);
//
//        Area = sessionManager.getFromSessionManager(SessionManager.Login_CITY);
//        Taluka = sessionManager.getFromSessionManager(SessionManager.Login_TALUKA);

        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        productArrayList = (DashboardList) getIntent().getParcelableExtra("data");
        from_page = getIntent().getStringExtra("from_page");

        rbGroup = findViewById(R.id.rbGroup);
        rbDene = findViewById(R.id.rbDene);
        rbGhene = findViewById(R.id.rbGhene);
        etCategoryText = (TextInputLayout) findViewById(R.id.etCategoryText);
        etCategory = (TextView) findViewById(R.id.etCategory);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rlLayout = findViewById(R.id.rlLayout);
        rbMobile = findViewById(R.id.rbMobile);
        rbHide = findViewById(R.id.rbHide);
        rbShow = findViewById(R.id.rbShow);
        gifDescription = findViewById(R.id.gifDescription);
        gifPrice = findViewById(R.id.gifPrice);
        gifTitle = findViewById(R.id.gifTitle);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivVdeo = (ImageView) findViewById(R.id.ivVdeo);

        rbAVA = findViewById(R.id.rbAVA);
        rbAvailable = findViewById(R.id.rbAvailable);
        rbNotAva = findViewById(R.id.rbNotAva);





        setData();


    }

    private boolean imageValidation() {

        if(requestCamera){
            if(images.size() < image_count) return true;
            else  openSnackBar(context.getString(R.string.image_error));
        }
        else Toast.makeText(this, "Permission Denied, You cannot access camera.", Toast.LENGTH_LONG).show();

        return false;
    }

    private boolean videoValidation() {

        if(requestCamera){

            if(videoList.size() < 1)return true;
            else  openSnackBar(context.getString(R.string.image_error));
        }
        else Toast.makeText(this, "Permission Denied, You cannot access camera.", Toast.LENGTH_LONG).show();

        return false;
    }

    private void setData() {
        Lat= productArrayList.getLatitude();
        Long= productArrayList.getLongitude();
        String address[] = productArrayList.getAddress().split(",");
        Area = address[0];
        Taluka = address[1];

        category_id = productArrayList.getCategory_id();
        listing_status = productArrayList.getListing_status();
        if (listing_status.equals("2")) {
            rbDene.setChecked(true);
            rbGhene.setChecked(false);
        } else {
            rbGhene.setChecked(true);
            rbDene.setChecked(false);
        }
        mobile_status = productArrayList.getMobile_status();
        if (mobile_status != null) {
            if (mobile_status.equals("0")) {
                rbShow.setChecked(true);
                rbHide.setChecked(false);
            } else {
                rbHide.setChecked(true);
                rbShow.setChecked(false);
            }
        }

        available_status = productArrayList.getAvailable_status();
        if (available_status.equals("0")) {
            rbAvailable.setChecked(true);
            rbNotAva.setChecked(false);
        } else {
            rbNotAva.setChecked(true);
            rbAvailable.setChecked(false);
        }

        category_id = productArrayList.getCategory_id();
        etCategory.setText(productArrayList.getCategory());
        etTitle.setText(productArrayList.getTitle());
        etDescription.setText(productArrayList.getDescription());
        etPrice.setText(productArrayList.getPrice());
        uploaded_images = productArrayList.getImages().split(",");
        image_id = productArrayList.getImage_id().split(",");
        for (int i = 0; i < uploaded_images.length; i++) {
            if (uploaded_images[i].contains("video")) {
                fillImageList(URLs.IMAGE_URL + uploaded_images[i], "video", image_id[i]);
                videoList.add(URLs.IMAGE_URL + uploaded_images[i]);
            } else {
                fillImageList(URLs.IMAGE_URL + uploaded_images[i], "image", image_id[i]);
                images.add(URLs.IMAGE_URL + uploaded_images[i]);
                flat_images.add(URLs.IMAGE_URL + uploaded_images[i]);
            }
        }

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterSheet();
            }
        });

        rbAVA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                //boolean isChecked = checkedRadioButton.isChecked();
                if (checkedId == R.id.rbAvailable) {
                    available(productArrayList.getListing_id());
                } else if (checkedId == R.id.rbNotAva) {
                    unavailable(productArrayList.getListing_id());
                }


            }
        });


        rbMobile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                //boolean isChecked = checkedRadioButton.isChecked();
                if (checkedId == R.id.rbShow) {
                    mobile_status = "0";
                } else if (checkedId == R.id.rbHide) {
                    mobile_status = "1";
                }


            }
        });

        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (checkedId == R.id.rbDene) {
                    listing_status = "2";
                } else if (checkedId == R.id.rbGhene) {
                    listing_status = "3";
                }


            }
        });
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageValidation()) {
                    actionDialogBox(context);
                }

            }
        });

        ivVdeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( videoValidation()) {
                    actionDialogBoxForVideo(context);
                }


            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Validation()) {
                    uploadData(etTitle.getText().toString(), etTitle.getText().toString());
                }
//                else {
//                    openSnackBar("कृपया किमान एक फोटो अपलोड करा");
//                }

            }
        });
        gifArea = findViewById(R.id.gifArea);
        gifTaluka = findViewById(R.id.gifTaluka);
        gifArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, tvArea);
                startVoiceInput("gifArea", 555);
            }
        });

        gifTaluka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, tvTaluka);
                startVoiceInput("gifTaluka", 666);
            }
        });

        gifTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, etTitle);
                startVoiceInput("etTitle", REQ_CODE_SPEECH_INPUT);
            }
        });
        gifDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, etDescription);
                startVoiceInput("etDescription", REQ_CODE_SPEECH_INPUT_Description);
            }
        });
        gifPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.checkErrorPresent(context, etPrice);
                startVoiceInput("etPrice", REQ_CODE_SPEECH_INPUT_PRICE);
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etDescription.getText().toString().length() >= 1000) {
                    //Show toast here
                    etDescription.setError(getString(R.string.err_limit));
                }
            }
        });

        rbLocation = findViewById(R.id.rbLocation);
        llHomeAddress = findViewById(R.id.llHomeAddress);
        llAddress = findViewById(R.id.llAddress);
        tvHomeTaluka = findViewById(R.id.tvHomeTaluka);
        tvHomeArea = findViewById(R.id.tvHomeArea);
        tvTaluka = findViewById(R.id.tvTaluka);
        tvArea = findViewById(R.id.tvArea);

        tvHomeTaluka.setText(Taluka);
        tvHomeArea.setText(Area);
        Transaltion.translateListing(sessionManager.getFromSessionManager(SessionManager.TALUKA), tvTaluka, "Taluka", context);
        Transaltion.translateListing(sessionManager.getFromSessionManager(SessionManager.CITY), tvArea, "Area", context);
        Area = sessionManager.getFromSessionManager(SessionManager.CITY_List);
        Taluka = sessionManager.getFromSessionManager(SessionManager.TALUKA_List);


        rbLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                //boolean isChecked = checkedRadioButton.isChecked();
                if (checkedId == R.id.rbHome) {
                    LocationSelectionMode = 1;
                    llHomeAddress.setVisibility(View.VISIBLE);
                    llAddress.setVisibility(View.GONE);
                    String address[] = productArrayList.getAddress().split(",");
                    Area = address[0];
                    Taluka = address[1];
                    Lat= productArrayList.getLatitude();
                    Long= productArrayList.getLongitude();
                    tvHomeTaluka.setText(Taluka);
                    tvHomeArea.setText(Area);

                } else if (checkedId == R.id.rbCurrent) {
                    tvTaluka.setText("");
                    tvArea.setText("");
                    llHomeAddress.setVisibility(View.GONE);
                    llAddress.setVisibility(View.VISIBLE);
                    Lat = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
                    Long = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);
                    Transaltion.translateListing(sessionManager.getFromSessionManager(SessionManager.TALUKA), tvTaluka, "Taluka", context);
                    Transaltion.translateListing(sessionManager.getFromSessionManager(SessionManager.CITY), tvArea, "Area", context);
                    Area = sessionManager.getFromSessionManager(SessionManager.CITY_List);
                    Taluka = sessionManager.getFromSessionManager(SessionManager.TALUKA_List);
                    LocationSelectionMode = 2;
                } else if (checkedId == R.id.rbSelect) {
                    tvTaluka.setText("");
                    tvArea.setText("");
                    llHomeAddress.setVisibility(View.GONE);
                    llAddress.setVisibility(View.VISIBLE);
                    Intent in = new Intent(DbEditRentListingActivity.this, MapsActivity.class);
                    in.putExtra("latitude", sessionManager.getFromSessionManager(SessionManager.LATITUDE));
                    in.putExtra("longitude", sessionManager.getFromSessionManager(SessionManager.LONGITUDE));
                    startActivityForResult(in, 777);
                    LocationSelectionMode = 3;
                }


            }
        });


    }

    private void setKeyboard() {
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SoftKeyMethod((AppCompatEditText) etTitle);
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SoftKeyMethod((AppCompatEditText) etDescription);
            }
        });

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SoftKeyMethod((AppCompatEditText) etPrice);
            }
        });

//        etTitle.setShowSoftInputOnFocus(false);
//        etDescription.setShowSoftInputOnFocus(false);
//        etPrice.setShowSoftInputOnFocus(false);
//        mCustomKeyboard = new EditCustomKeyboard(EditRentListingActivity.this, R.id.keyboardview, R.xml.numeric);
//        mKeyboardView = (KeyboardView) findViewById(R.id.keyboardview);
//        mKeyboardView.setKeyboard(new Keyboard(EditRentListingActivity.this, R.xml.numeric));
//        mCustomKeyboard.registerEditText(R.id.etTitle);
//        mCustomKeyboard.registerEditText(R.id.etDescription);
//        mCustomKeyboard.registerEditText(R.id.etPrice);
//        tvArea.setShowSoftInputOnFocus(false);
//        tvTaluka.setShowSoftInputOnFocus(false);
//        mCustomKeyboard.registerEditText(R.id.tvArea);
//        mCustomKeyboard.registerEditText(R.id.tvTaluka);
//        mKeyboardView.setOnKeyboardActionListener(this);
    }

    private void imagelist() {
        list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        // The number of Columns
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(null);
        // Getting adapter by passing xml data ArrayList
        adapter = new ImageListAdapter(this, imageList, DbEditRentListingActivity.this);
        list.setAdapter(adapter);
    }


    private boolean Validation() {
        if (TextUtils.isEmpty(etTitle.getText().toString())|| etTitle.getText().toString().trim().equals("")) {
            etTitle.setError(getString(R.string.err_title));
            etTitle.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etDescription.getText().toString())|| etDescription.getText().toString().trim().equals("")) {
            etDescription.setError(getString(R.string.err_description));
            etDescription.requestFocus();
            return false;
        } else if (flat_images.size() == 0 && imageList.size() == 0) {
            openSnackBar("कृपया किमान एक फोटो अपलोड करा");
            return false;
        } else if (LocationSelectionMode == 1) {
            if (Area.equals("unnamed") || Area.equals("")) {
                openSnackBar(getString(R.string.err_address));
                return false;
            } else if (Taluka.equals("unnamed") || Taluka.equals("")) {
                openSnackBar(getString(R.string.err_address));
                return false;
            }
            else {
                return true;
            }
        } else if (LocationSelectionMode == 2 || LocationSelectionMode == 3) {
            if (tvArea.getText().toString().equals("unnamed") || tvArea.getText().toString().equals("")) {
                openSnackBar(getString(R.string.err_address));
                return false;
            } else if (tvTaluka.getText().toString().equals("unnamed") || tvTaluka.getText().toString().equals("")) {
                openSnackBar(getString(R.string.err_address));
                return false;
            } else {
                Area = tvArea.getText().toString();
                Taluka = tvTaluka.getText().toString();
                return true;
            }
        }
        else {
//            Area= tvArea.getText().toString();
//            Taluka=tvTaluka.getText().toString();
            return true;
        }
    }


    private void startVoiceInput(String type, int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        String  mLanguageCode = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageCode+ URLs.language);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.help_text));
        try {
            startActivityForResult(intent, code);

        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    selectedImagePath = getImagePath();
                    String ImagePath = ImageCompression.compressImage(selectedImagePath, context);
                    images.add(ImagePath);
                    fillImageList(ImagePath, "image", "");
                    flat_images.add(ImagePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                getMultipleImagesfromGallery(data);

//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                selectedImagePath = picturePath;
//                String ImagePath = ImageCompression.compressImage(selectedImagePath, context);
//                images.add(ImagePath);
//                fillImageList(ImagePath, "image", "");
//                flat_images.add(ImagePath);

            } else if (requestCode == 3) {
                Uri selectedVieo = data.getData();
                String filePath = generatePath(selectedVieo, context);
                selectedImagePath = filePath;
                selectedImagePath = trimVideo(filePath);

            } else if (requestCode == 4) {
                try {
                    Uri selectedVieo = data.getData();
                    String filePath = generatePath(selectedVieo, context);
                    selectedImagePath = filePath;
                    selectedImagePath = trimVideo(filePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK && null != data) {
                    int pos = etTitle.getSelectionStart();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    title.insert(pos, result.get(0));
                    title.append(" ");
                    etTitle.setText(title);
                    etTitle.setSelection(etTitle.getText().toString().length());
                    etTitle.requestFocus();
                }

            } else if (requestCode == REQ_CODE_SPEECH_INPUT_Description) {
                if (resultCode == RESULT_OK && null != data) {
                    int pos = etDescription.getSelectionStart();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    description.insert(pos, result.get(0));
                    description.append(" ");
                    etDescription.setText(description);
                    etDescription.setSelection(etDescription.getText().toString().length());
                    etDescription.requestFocus();
                }

            } else if (requestCode == REQ_CODE_SPEECH_INPUT_PRICE) {
                if (resultCode == RESULT_OK && null != data) {
                    int pos = etPrice.getSelectionStart();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    price.insert(pos, result.get(0));
                    price.append(" ");
                    etPrice.setText(price);
                    etPrice.setSelection(etPrice.getText().toString().length());
                    etPrice.requestFocus();
                }

            } else if (requestCode == 555) {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvArea.setText(result.get(0));
                    tvArea.setSelection(tvArea.getText().toString().length());
                    tvArea.requestFocus();
                    Area = tvArea.getText().toString();
                }

            } else if (requestCode == 666) {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvTaluka.setText(result.get(0));
                    tvTaluka.setSelection(tvTaluka.getText().toString().length());
                    tvTaluka.requestFocus();
                    Taluka = tvTaluka.getText().toString();
                }

            } else if (requestCode == 777) {
                Transaltion.translate(data.getStringExtra("taluka"), tvTaluka,context);
                Transaltion.translate(data.getStringExtra("area"), tvArea,context);
                Area = tvArea.getText().toString();
                Taluka = tvTaluka.getText().toString();
                Lat = data.getStringExtra("lat");
                Long = data.getStringExtra("lang");

            }


        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String filePath = "";
        String path = contentUri.toString();
        try {

            if (path.contains("com.android.providers")) {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            } else {
                String[] filePath1 = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(contentUri, filePath1, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath1[0]);
                filePath = c.getString(columnIndex);
                c.close();

            }

        } catch (Exception e) {

            return filePath;
        }
        return filePath;
    }

    private void getMultipleImagesfromGallery(Intent data) {

        int no_of_image = 0;
        ClipData clipData = data.getClipData();

        if (clipData != null) {
            //multiple images selecetd
            no_of_image = clipData.getItemCount() > 10 ? 10 : clipData.getItemCount();

            for (int i = 0; i < no_of_image; i++) {
                Uri imageUri = clipData.getItemAt(i).getUri();
                Log.d("URI", imageUri.toString());
                try {
                    selectedImagePath = getRealPathFromURI(imageUri);
                    String ImagePath = ImageCompression.compressImage(selectedImagePath, context);
                    images.add(ImagePath);
                    fillImageList(ImagePath, "image", "");
                    flat_images.add(ImagePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else {
            //single image selected
            Uri imageUri = data.getData();
            Log.d("URI", imageUri.toString());
            try {
                selectedImagePath = getRealPathFromURI(imageUri);
                String ImagePath = ImageCompression.compressImage(selectedImagePath, context);
                images.add(ImagePath);
                fillImageList(ImagePath, "image", "");
                flat_images.add(ImagePath);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void actionDialogBox(final Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        final CharSequence[] options1 = {"फोटो घ्या", "गॅलरीमधून निवडा", "रद्द करा"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DbEditRentListingActivity.this);
        builder.setTitle(getString(R.string.takephoto));
        builder.setItems(options1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".jpg");
                    imagePath = f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    // Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // startActivityForResult(intent, 2);

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, context.getString(R.string.image_selection)), 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void actionDialogBoxForVideo(final Context context) {
        final CharSequence[] options1 = {"व्हिडिओ घ्या", "गॅलरीमधून निवडा", "रद्द करा"};
        final CharSequence[] options = {"Take Video", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DbEditRentListingActivity.this);
        builder.setTitle(getString(R.string.takevideo));
        builder.setItems(options1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Video")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // File f = new File(android.os.Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".jpg");
                    // imagePath = f.getAbsolutePath();
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 4);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), 3);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public String getImagePath() {
        return imagePath;
    }


    private void uploadData(final String name, final String address) {

        if (removeImageList.size() > 0) {
            for (int i = 0; i < removeImageList.size(); i++) {
                deleteImage(removeImageList.get(i).getImage_id(), removeImageList.get(i).getPath());
            }
        }

        myAsyncTask = new uplaodDataToServer(etTitle, etDescription, etPrice);
        myAsyncTask.execute();

    }


    private void openFilterSheet() {
        View view = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);
        FilterAdapter list_adapter = new FilterAdapter(filterCategoryArrayList, context);
        lv_languages.setAdapter(list_adapter);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        lv_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etCategory.setText(filterCategoryArrayList.get(i).getName());
                category_id = filterCategoryArrayList.get(i).getId();
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void openSnackBar(String response) {
        Snackbar snackbar;
        snackbar = Snackbar.make(rlLayout, response, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
//        if (mCustomKeyboard.isCustomKeyboardVisible()) mCustomKeyboard.hideCustomKeyboard();
//        else

        this.finish();
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        View focusCurrent = getWindow().getCurrentFocus();
        if (focusCurrent == null || focusCurrent.getClass() != AppCompatEditText.class) return;
        AppCompatEditText edittext = (AppCompatEditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();

        switch (primaryCode) {
            case 55006:
                if (editable != null) editable.clear();
                DeleteMethod(edittext);
                break;
            case -5:
                if (editable != null && start > 0) editable.delete(start - 1, start);
                BackSpaceMethod(edittext);
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                SoftKeyMethod(edittext);
                break;
        }


    }

    private void BackSpaceMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();

        switch (editTextId) {
            case R.id.etTitle:
                title.delete(0, title.length());
                title.append(editTextData);
                title.append("");
                break;
            case R.id.etDescription:
                description.delete(0, description.length());
                description.append(editTextData);
                description.append("");
                break;

        }
    }

    private void SoftKeyMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();


        switch (editTextId) {
            case R.id.etTitle:
                title.delete(0, title.length());
                title.append(editTextData);
                title.append("");
                break;
            case R.id.etDescription:
                description.delete(0, description.length());
                description.append(editTextData);
                description.append("");
                break;
            case R.id.etPrice:
                price.delete(0, price.length());
                price.append(editTextData);
                price.append("");
                break;
        }

    }

    private void DeleteMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();

        switch (editTextId) {
            case R.id.etTitle:
                title.delete(0, title.length());
                break;
            case R.id.etDescription:
                description.delete(0, description.length());
                break;

        }

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }


    //    ---------------------------------Permission--------------------

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {


                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;


//                    &&locationAccepted1

                    if (cameraAccepted && readAccepted && writeAccepted) {
//                        Toast.makeText(this, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
                        //  DenyAndDontASk = true;
                        requestCamera = true;
                    } else {
                        // DenyAndDontASk = false;
//                        Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA) ||
                                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) ||
                                    shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DbEditRentListingActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                // .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //--------------------------------Permission End--------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove:
                final int i = (int) v.getTag(R.string.btn_view_position);
                removeImageFromList(i);
                break;
        }
    }

    private void removeImageFromList(int i) {
        try {
            ImageVideo path = imageList.get(i);
            if (path.getType().equals("video")) {
                videoList.remove(0);
            } else {
                images.remove(i);
            }

            if (imageList.size() > 0) imageList.remove(i);
            if (flat_images.size() > 0) flat_images.remove(i);
            adapter.notifyDataSetChanged();
            if (!path.getImage_id().equals(""))
                removeImageList.add(new ImageVideo(path.getPath(), "", path.getImage_id()));
            //deleteImage(path.getImage_id(), path.getPath());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteImage(String image_id, String path) {
        Call<ResponseBody> call=   mViewModel.deleteImage(image_id, path);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            for(int i=0; i<imageList.size();i++){
                                mViewModel.updateImage(imageList.get(i).getImage_id(), imageList.get(i).getPath(), productArrayList.getId(), from_page);
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


    private void getCategoryFromServer() {
        filterCategoryArrayList= new ArrayList<>();
        mViewModel.getCategory().observe(this, new Observer<Resource<List<Category>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Category>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if (listResource.data != null) {
                        // Testing.printRecipess("data: ", listResource.data);

                        switch (listResource.status) {
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                filterCategoryArrayList.addAll(listResource.data);
                                break;
                            }

                        }
                    }


                }
            }

        });
    }

    private void fillImageList(String selectedImagePath, String type, String image_id) {
        imageList.add(new ImageVideo(selectedImagePath, type, image_id));
        list.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

    }

    public String generatePath(Uri uri, Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }

    @TargetApi(19)
    private String generateFromKitkat(Uri uri, Context context) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }
        return filePath;
    }

    public String compressTrimVideo(String srcMp4Path, String destMp4Path) {
        new Mp4Composer(srcMp4Path, destMp4Path)
                .rotation(Rotation.NORMAL)
                .size(300, 300)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .trim(0, 60000)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d(TAG, "onProgress = " + progress);
                        runOnUiThread(() -> progressDialog.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCurrentWrittenVideoTime(long timeUs) {

                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            fillImageList(destMp4Path, "video", "");
                            videoList.add(destMp4Path);
                            // Toast.makeText(context, "codec complete path =" + destMp4Path, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Log.e(TAG, "onFailed()", exception);
                    }
                })
                .start();

        return destMp4Path;
    }

    private String trimVideo(String scrPath) {
        String finalFile = null;
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getString(R.string.video_uploading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
            File file = new File(scrPath);

            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            if (file_size > 10000) {
                File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String filePrefix = "trim_garzoo_video";
                String fileExtn = ".mp4";
                File dest = new File(moviesDir, filePrefix + fileExtn);
                int fileNo = 0;
                while (dest.exists()) {
                    fileNo++;
                    dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
                }
                String filePath = dest.getAbsolutePath();
                compressTrimVideo(scrPath, filePath);
            } else {
                finalFile = scrPath;
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    fillImageList(selectedImagePath, "video", "");
                    videoList.add(selectedImagePath);
                    // Toast.makeText(context, "codec complete path =" + destMp4Path, Toast.LENGTH_SHORT).show();
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalFile;
    }



    //--------------------------------Upload Start--------------------------
    private class uplaodDataToServer extends AsyncTask<String, Void, String> {
        private WeakReference<EditText> etTitle, etDescription, etPrice;
        private ProgressDialog progressDialog;


        public uplaodDataToServer(EditText title, EditText etDescription, EditText etPrice) {
            this.etTitle = new WeakReference<>(title);
            this.etDescription = new WeakReference<>(etDescription);
            this.etPrice = new WeakReference<>(etPrice);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            //write code here eg: ProgressBar, this is first method to execute
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                final String URL = URLs.api_rent_edit_record_2_0;
                MultiImageUtility multipartUtility = new MultiImageUtility(URL, "utf-8");
                multipartUtility.addHeaderField("User-Agent", "CodeJava");
                multipartUtility.addHeaderField("Connection", "close");

                File file_upload[] = new File[flat_images.size()];    //  For Multiple Images

                if (flat_images.size() > 0) {
                    for (int i = 0; i < flat_images.size(); i++) {
                        if (!flat_images.get(i).contains(URLs.IMAGE_URL)) {
                            File f = new File(flat_images.get(i));
                            file_upload[i] = f;
                            multipartUtility.addFilePart("image[]", file_upload[i]);   // for multiple image (check paramter (Key []))
                        }
                    }
                }

                if (videoList.size() > 0) {
                    if (!videoList.get(0).contains(URLs.IMAGE_URL)) {
                        File view_file = new File(videoList.get(0));
                        multipartUtility.addFilePart("video_file", view_file); //video
                    }
                }

                // if you want to send any other string value to serevr.
                multipartUtility.addFormField("user_id", sessionManager.getFromSessionManager(SessionManager.USER_ID));
                multipartUtility.addFormField("category_id", category_id);
                multipartUtility.addFormField("mobile_status", mobile_status);
                multipartUtility.addFormField("title", etTitle.get().getText().toString());
                multipartUtility.addFormField("description", etDescription.get().getText().toString());
                multipartUtility.addFormField("price", etPrice.get().getText().toString());
                multipartUtility.addFormField("latitude", Lat);
                multipartUtility.addFormField("longitude", Long);
                multipartUtility.addFormField("listing_status", listing_status); //
//                String address = checkAddress();
                String address = Area + " ," + Taluka;
                multipartUtility.addFormField("address", address);
                multipartUtility.addFormField("p", productArrayList.getListing_id());
                multipartUtility.addFormField("q", URLs.unique_id);


                return multipartUtility.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //  progressDialog.dismiss();
                Log.e("Toast", s);
                if (s.contains("success")) {
                    progressDialog.dismiss();
                    openSnackBar(getString(R.string.sucess_msg));
                    etTitle.get().setText("");
                    etDescription.get().setText("");
                    etPrice.get().setText("");
                    flat_images.clear();
                    videoList.clear();
                    imageList.clear();
                    adapter.notifyDataSetChanged();
                    finish();

                } else {
                    //Glide.with(context).load(R.drawable.ic_baseline_linked_camera_24).into(ivImage);
                    openSnackBar(getString(R.string.failed_msg));
                    progressDialog.dismiss();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void openSnackBar(String response) {
            Snackbar snackbar;
            snackbar = Snackbar.make(rlLayout, response, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private String checkAddress() {
        String address = null;
        String area = sessionManager.getFromSessionManager(SessionManager.CITY);
        String location = sessionManager.getFromSessionManager(SessionManager.LOCATION);
        if (area.equals("") || area.contains("unnamed") || location.contains("unnamed")) {
            address = String.format("%1$s , %2$s", sessionManager.getFromSessionManager(SessionManager.Login_CITY), sessionManager.getFromSessionManager(SessionManager.Login_TALUKA));
        } else {
            address = String.format("%1$s , %2$s", sessionManager.getFromSessionManager(SessionManager.CITY), sessionManager.getFromSessionManager(SessionManager.TALUKA));
        }
        return address;
    }

    //--------------------------------Upload End--------------------------


    @Override
    protected void onDestroy() {
        if (myAsyncTask != null) myAsyncTask.cancel(true);
        super.onDestroy();
    }

    private void unavailable(String record_id) {
        Call<ResponseBody> call = mViewModel.unavailable(URLs.unique_id, user_id,productArrayList.getListing_id()); //.get(0)

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            if (result.equals("success")) {
                                Utils.openSnackBar("success", rlLayout);
                                Log.d(TAG, "the message is ----> " + response.message());
                                //Log.e("main", "the error is ----> " + response.body().getError());
                                finish();
                            } else {
                                Utils.openSnackBar("Failed", rlLayout);
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


    private void available(String record_id) {
        Call<ResponseBody> call = mViewModel.available(URLs.unique_id, user_id,productArrayList.getListing_id()); //.get(0)

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            if (result.equals("success")) {
                                Utils.openSnackBar("success", rlLayout);
                                Log.d(TAG, "the message is ----> " + response.message());
                                //Log.e("main", "the error is ----> " + response.body().getError());
                                finish();
                            } else {
                                Utils.openSnackBar("Failed", rlLayout);
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

}