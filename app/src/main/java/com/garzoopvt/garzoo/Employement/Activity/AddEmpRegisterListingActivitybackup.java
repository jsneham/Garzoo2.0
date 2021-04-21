package com.garzoopvt.garzoo.Employement.Activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.Rotation;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.garzoopvt.garzoo.Adapter.ImageListAdapter;
import com.garzoopvt.garzoo.Adapter.ImageVideo;
import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.Employement.ViewModel.EmploymentViewModel;
import com.garzoopvt.garzoo.MapsActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.ImageCompression;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Transaltion;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

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

public class AddEmpRegisterListingActivitybackup extends BaseActivity implements View.OnClickListener, View.OnKeyListener {

    private String TAG = "AddEmpRegisterListingActivity";

    //View
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.ivVdeo)
    ImageView ivVdeo;
    @BindView(R.id.rlLayout)
    RelativeLayout rlLayout;
    @BindView(R.id.rbMobile)
    RadioGroup rbMobile;
//    @BindView(R.id.rbGroup)
//    RadioGroup rbGroup;
    @BindView(R.id.rbLocation)
    RadioGroup rbLocation;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.gifDescription)
    ImageView gifDescription;
    @BindView(R.id.gifPrice)
    ImageView gifPrice;
    @BindView(R.id.gifTitle)
    ImageView gifTitle;
    @BindView(R.id.gifTaluka)
    ImageView gifTaluka;
    @BindView(R.id.gifArea)
    ImageView gifArea;
    @BindView(R.id.tvTaluka)
    EditText tvTaluka;
    @BindView(R.id.tvArea)
    EditText tvArea;

    @BindView(R.id.llHomeAddress)
    LinearLayout llHomeAddress;
    @BindView(R.id.llAddress)
    LinearLayout llAddress;


    //class
    private ImageListAdapter adapter;
    private EmploymentViewModel mViewModel;

    //Data

    private ArrayList<ImageVideo> imageList = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> flat_images = new ArrayList<>();
    private Context context = this;
    private StringBuffer description = new StringBuffer("");
    private StringBuffer title = new StringBuffer("");
    private StringBuffer price = new StringBuffer("");
    private ProgressDialog progressDialog;

    //constant
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_SPEECH_INPUT_Description = 200;
    private static final int REQ_CODE_SPEECH_INPUT_PRICE = 300;
    private String imagePath = "";
    private String selectedImagePath = "";
    private String user_id;
    private String category_id="0";
    private String mobile_status = "0";
    private String emp_status = "2";
    private int image_count = 10;
    private String Lat, Long;
    private String Taluka = "", Area = "";
    private SessionManager sessionManager;
    private int LocationSelectionMode = 2;
    private boolean requestCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emp_rgister_listing);
        sessionManager = new SessionManager(this);
        mViewModel = ViewModelProviders.of(this).get(EmploymentViewModel.class);

        ButterKnife.bind(this);
        getSessionData();
        getToolBar();
        imagelist();

    }

    private void getSessionData() {
        Lat = sessionManager.getFromSessionManager(SessionManager.LATITUDE_FIXED);
        Long = sessionManager.getFromSessionManager(SessionManager.LONGITUDE_FIXED);
        Area = sessionManager.getFromSessionManager(SessionManager.Login_CITY);
        Taluka = sessionManager.getFromSessionManager(SessionManager.Login_TALUKA);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        tvArea.setText(Area);
        tvTaluka.setText(Taluka);
    }

    private void getToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.empregister_);

    }


    private void imagelist() {
        list.setHasFixedSize(true);
        // The number of Columns
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(mLayoutManager);
        // Getting adapter by passing xml data ArrayList
        adapter = new ImageListAdapter(this, imageList, AddEmpRegisterListingActivitybackup.this);
        list.setAdapter(adapter);
    }

    @OnClick(R.id.btnSubmit)
    public void Submit() {
        if (Validation()) {

            uploadImage();


        }
    }

    private void uploadImage() {
        try {
            MultipartBody.Part videos = null;
            if (videoList.size() > 0) {
                MultipartBody.Part videoRequest = prepareFilePart("video_file", Uri.parse(videoList.get(0)), videoList.get(0));
                videos= videoRequest;
            }


            MultipartBody.Part list[] = new MultipartBody.Part[flat_images.size()];

//            for (String uri : flat_images) {
            for (int j = 0; j < flat_images.size(); j++) {
                // MultipartBody.Part imageRequest = prepareFilePart("file[]", Uri.parse(uri), uri);
                // MultipartBody.Part imageRequest = prepareFilePart("picture", Uri.parse(uri), uri);
                MultipartBody.Part imageRequest = prepareFilePart("image[]", Uri.parse(flat_images.get(j)), flat_images.get(j));
                //list.add(imageRequest);
                list[j] = imageRequest;
            }

            RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
            RequestBody rb_mobile_status = RequestBody.create(MultipartBody.FORM, mobile_status);
            RequestBody rb_emp_status = RequestBody.create(MultipartBody.FORM, emp_status);
            RequestBody rb_category_id = RequestBody.create(MultipartBody.FORM, category_id);
            RequestBody title = RequestBody.create(MultipartBody.FORM, etTitle.getText().toString());
            RequestBody description = RequestBody.create(MultipartBody.FORM, etDescription.getText().toString());
            RequestBody price = RequestBody.create(MultipartBody.FORM, etPrice.getText().toString());
            RequestBody address = RequestBody.create(MultipartBody.FORM, Area + " ," + Taluka);
            RequestBody rb_Lat = RequestBody.create(MultipartBody.FORM, Lat);
            RequestBody rb_Long = RequestBody.create(MultipartBody.FORM, Long);

            Call<ResponseBody> call = mViewModel.uploadData(list, rb_user_id, rb_mobile_status, rb_category_id, title,
                    description, price, rb_Lat, rb_Long, address, videos, rb_emp_status); //.get(0)

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

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "uploadImage:" + e.getMessage());
        }

    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri, String filename) {
        // create RequestBody instance from file
        File file = new File("" + fileUri);
        file.getName();

//        MediaType m=   MediaType.parse(getContentResolver().getType(fileUri));
//        Log.d(TAG, "prepareFilePart: "+ m);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filename);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }





    @OnClick(R.id.gifTitle)
    public void gifTitleOnclick() {
        Utils.checkErrorPresent(context, etTitle);
        startVoiceInput("etTitle", REQ_CODE_SPEECH_INPUT);
    }

    @OnClick(R.id.gifDescription)
    public void gifDescriptionOnclick() {
        Utils.checkErrorPresent(context, etDescription);
        startVoiceInput("etDescription", REQ_CODE_SPEECH_INPUT_Description);
    }

    @OnClick(R.id.gifPrice)
    public void gifPriceOnclick() {
        Utils.checkErrorPresent(context, etPrice);
        startVoiceInput("etPrice", REQ_CODE_SPEECH_INPUT_PRICE);
    }

    @OnClick(R.id.gifArea)
    public void gifAreaOnclick() {
        Utils.checkErrorPresent(context, tvArea);
        startVoiceInput("gifArea", 555);
    }


    @OnClick(R.id.gifTaluka)
    public void gifTalukaOnclick() {
        Utils.checkErrorPresent(context, tvTaluka);
        startVoiceInput("gifTaluka", 666);
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

    @OnTextChanged(value = R.id.etTitle, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etTitleChange(CharSequence text) {
        SoftKeyMethod((AppCompatEditText) etTitle);
    }


    @OnTextChanged(value = R.id.etDescription, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etDescriptionChange(CharSequence text) {
        SoftKeyMethod((AppCompatEditText) etDescription);
    }


    @OnTextChanged(value = R.id.etPrice, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etPriceChange(CharSequence text) {
        SoftKeyMethod((AppCompatEditText) etPrice);
    }

    @OnClick(R.id.ivImage)
    public void ivImageClicked() {
        if (requestCamera && images.size() < image_count) {
            actionDialogBox(context);
        } else {
            Utils.openSnackBar(context.getString(R.string.image_error), rlLayout);
        }
    }

    public void actionDialogBox(final Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        final CharSequence[] options1 = {getString(R.string.takephoto),getString(R.string.choosegallery),getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEmpRegisterListingActivitybackup.this);
        builder.setTitle(getString(R.string.takephoto));
        builder.setItems(options1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".jpg");
                    imagePath = f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
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


    @OnClick(R.id.ivVdeo)
    public void ivVdeoClicked() {
        if (requestCamera && images.size() < image_count) {
            actionDialogBoxForVideo(context);
        } else {
            Utils.openSnackBar(context.getString(R.string.image_error), rlLayout);
        }
    }

    public void actionDialogBoxForVideo(final Context context) {
        final CharSequence[] options1 = {getString(R.string.takevideo),getString(R.string.choosegallery),getString(R.string.cancel)};
        final CharSequence[] options = {"Take Video", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEmpRegisterListingActivitybackup.this);
        builder.setTitle(getString(R.string.takevideo));
        builder.setItems(options1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Video")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
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




    @OnClick({R.id.rbShow, R.id.rbHide})
    public void setMobileStatus(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.rbShow:
                if (checked) {
                    mobile_status = "0";
                }
                break;
            case R.id.rbHide:
                if (checked) {
                    mobile_status = "1";
                }
                break;
        }
    }

    @OnClick({R.id.rbCurrent, R.id.rbSelect})
    public void setLocation(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.rbCurrent:
                if (checked) {
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
                }
                break;
            case R.id.rbSelect:
                if (checked) {
                    tvTaluka.setText("");
                    tvArea.setText("");
                    llHomeAddress.setVisibility(View.GONE);
                    llAddress.setVisibility(View.VISIBLE);
                    Intent in = new Intent(AddEmpRegisterListingActivitybackup.this, MapsActivity.class);
                    in.putExtra("latitude", sessionManager.getFromSessionManager(SessionManager.LATITUDE));
                    in.putExtra("longitude", sessionManager.getFromSessionManager(SessionManager.LONGITUDE));
                    startActivityForResult(in, 777);
                    LocationSelectionMode = 3;
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
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


        } else if (requestCode == 1) {
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
        }

    }

    public String getImagePath() {
        return imagePath;
    }


    private void fillImageList(String selectedImagePath, String type, String image_id) {
        imageList.add(new ImageVideo(selectedImagePath, type, image_id));
        adapter.notifyDataSetChanged();

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

                Path path = null;
                long bytes = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    path = Paths.get(ImagePath);
                    bytes = Files.size(path);
                }

                Log.d(TAG, "Filesize: " + bytes);

            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        this.finish();
    }

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
            if (path.getType().equals("video")) videoList.remove(0);
            else images.remove(i);
            imageList.remove(i);
            adapter.notifyDataSetChanged();
            flat_images.remove(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        View focusCurrent = getWindow().getCurrentFocus();
        if (focusCurrent == null || focusCurrent.getClass() != AppCompatEditText.class)
            return false;
        AppCompatEditText edittext = (AppCompatEditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();

        switch (keyCode) {
            case 55006:
                if (editable != null) editable.clear();
                DeleteMethod(edittext);
                break;
            case -5:
                if (editable != null && start > 0) editable.delete(start - 1, start);
                BackSpaceMethod(edittext);
                break;
            default:
                editable.insert(start, Character.toString((char) keyCode));
                SoftKeyMethod(edittext);
                break;
        }

        return false;
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
            case R.id.etPrice:
                price.delete(0, price.length());
                break;

        }

    }


    private boolean Validation() {
        if (TextUtils.isEmpty(etTitle.getText().toString()) || etTitle.getText().toString().trim().equals("")) {
            etTitle.setError(getString(R.string.err_title));
            etTitle.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etDescription.getText().toString()) || etDescription.getText().toString().trim().equals("")) {
            etDescription.setError(getString(R.string.err_description));
            etDescription.requestFocus();
            return false;
        } else if (flat_images.size() == 0) {
            Utils.openSnackBar("कृपया किमान एक फोटो अपलोड करा", rlLayout);
            return false;
        }
        else if (tvArea.getText().toString().equals("unnamed") || tvArea.getText().toString().equals("")) {
            Utils.openSnackBar(getString(R.string.err_address), rlLayout);
            return false;
        } else if (tvTaluka.getText().toString().equals("unnamed") || tvTaluka.getText().toString().equals("")) {
            Utils.openSnackBar(getString(R.string.err_address), rlLayout);
            return false;
        } else {
            Area = tvArea.getText().toString();
            Taluka = tvTaluka.getText().toString();
            return true;
        }

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
}