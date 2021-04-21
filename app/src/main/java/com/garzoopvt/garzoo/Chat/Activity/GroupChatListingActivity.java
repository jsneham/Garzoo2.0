package com.garzoopvt.garzoo.Chat.Activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.garzoopvt.garzoo.Adapter.ImageListAdapter;
import com.garzoopvt.garzoo.Adapter.ImageVideo;
import com.garzoopvt.garzoo.BuySell.Activity.AddSellListingActivity;
import com.garzoopvt.garzoo.Chat.Adapter.ChatTextAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.GroupChatListAdapter;
import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Receiver.GCMRegistrationIntentService;
import com.garzoopvt.garzoo.Chat.ViewModel.ChatRoomViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ImageCompression;
import com.garzoopvt.garzoo.Util.MultiImageUtility;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

public class GroupChatListingActivity extends AppCompatActivity implements View.OnClickListener, com.facebook.ads.AdListener , KeyboardView.OnKeyboardActionListener {

    private uplaodDataToServer myAsyncTask;
    private View view;
    private static final int PERMISSION_REQUEST_CODE = 200;
    com.facebook.ads.AdView bannerAdView;
    private StringBuffer text_entered = new StringBuffer("");
    //Data
    private String TAG = "GroupChatListingActivity";
    private SessionManager sessionManager;
    private Context context = this;
    private String fuid, tuid, title, to_name, phone_no;
    private String user_id;
    private ChatUser chatArrayList;
    private String file_type = "1";
    private String message = "";
    private ArrayList<ChatGroupList> chatIndividualsList = new ArrayList<>();

    //View
    private RecyclerView rvList;
    private EditText edittext;
    private RelativeLayout rlLayout;
    private ImageView ivImage, btnAttach, btnSend, btnMic;

    //instances
    private ChatRoomViewModel mViewModel;
    private GroupChatListAdapter mAdapter;
    private ChatGroupList ch;
    private int pos;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private RecyclerView list;
    private ImageListAdapter adapter;
    private ArrayList<ImageVideo> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        mViewModel = ViewModelProviders.of(this).get(ChatRoomViewModel.class);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        // chatArrayList = (ChatUser) getIntent().getParcelableExtra("data");
        fuid = user_id;
        tuid = getIntent().getStringExtra("id");
        // phone_no = getIntent().getStringExtra("phone_no");
        title = getIntent().getStringExtra("title");
        to_name = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        init();
        getToolBar();
        setKeyboard();

        if (checkPermission())
            requestCamera = true;
        else
            requestPermission();
        subscribeObservers();
        getChats();
        getBannerAds();
        imagelist();
        registerReciever();
        getFbAd();
    }

    private void imagelist() {
        list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        // The number of Columns
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(mLayoutManager);
        // Getting adapter by passing xml data ArrayList
        adapter = new ImageListAdapter(this, imageList, GroupChatListingActivity.this);
        list.setAdapter(adapter);
    }

    private void getToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setTitle(title);


    }

    private void init() {
        rlLayout = (RelativeLayout) findViewById(R.id.rlLayout);
        rvList = (RecyclerView) findViewById(R.id.recyclerView);
        rvList.setHasFixedSize(true);
        mAdapter = new GroupChatListAdapter(context, user_id, this);
//        rvList.setNestedScrollingEnabled(true);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(mAdapter);


        edittext = findViewById(R.id.edittext);
        btnMic = findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startVoiceInput(100);
            }
        });

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = edittext.getText().toString();
                // messageText.delete(0, messageText.length());
                addChat();
            }
        });

        btnAttach = findViewById(R.id.btnAttach);
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file_type = "i";
                ivImageClicked();
            }
        });

    }

    private void getBannerAds() {
        AdView adView = findViewById(R.id.adView);
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
                // Toast.makeText(context, adError.getCode() + ", "+ adError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Toast.makeText(context, "onAdOpened", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                //Toast.makeText(context, "onAdClicked", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Toast.makeText(context, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
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


    private void subscribeObservers() {

        mViewModel.getGroupChats().observe(this, new Observer<Resource<List<ChatGroupList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatGroupList>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if (listResource.data != null) {
                        // Testing.printRecipess("data: ", listResource.data);

                        switch (listResource.status) {
                            case LOADING: {
                                if (mViewModel.getPageNumber() > 1) {
                                    mAdapter.displayLoading();
                                } else {
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                scrollToBottom();
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                //      Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

                                if (listResource.message.equals(ChatRoomViewModel.QUERY_EXHAUSTED)) {
                                    //  mAdapter.setQueryExhausted();
                                }
                                break;
                            }
                        }
                    }


                }
            }

        });


    }

    private void getChats() {
        mViewModel.getGroupChatListApi(user_id, 1, tuid);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_block:
                blockUser();
                break;

            case R.id.action_call:
                CallUser();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void blockUser() {

        Call<ResponseBody> call = mViewModel.block(user_id, tuid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            Utils.openSnackBar(result, rlLayout);
                            finish();

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

    private void CallUser() {

        if (!(user_id.equals("0") || user_id.isEmpty())) {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone_no));
            startActivity(intent);


        }

    }


    //This method will return current timestamp
    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri, String filename) {
        // create RequestBody instance from file
        File file = new File("" + fileUri);
        file.getName();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filename);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void addChat() {

        if (message.isEmpty()) {
            if (images.size() == 0) return;
        }

        myAsyncTask = new uplaodDataToServer(edittext);
        myAsyncTask.execute();


//        MultipartBody.Part list[] = new MultipartBody.Part[images.size()];
//        for (int j = 0; j < images.size(); j++) {
//            MultipartBody.Part imageRequest = prepareFilePart("image[]", Uri.parse(images.get(j)), images.get(j));
//            list[j] = imageRequest;
//            file_type = "i";
//        }
//
//        String sentAt = getTimeStamp();
//        RequestBody unique_id = RequestBody.create(MultipartBody.FORM, URLs.unique_id);
//        RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
//        RequestBody rb_to_user_id = RequestBody.create(MultipartBody.FORM, tuid);
//        RequestBody rb_message = RequestBody.create(MultipartBody.FORM, message);
//        RequestBody rb_file_type = RequestBody.create(MultipartBody.FORM, file_type);
//
//        Call<ChatGroupListResponse> call = mViewModel.addChat(unique_id, rb_user_id, rb_to_user_id,
//                rb_message, rb_file_type, list);
//
//        call.enqueue(new Callback<ChatGroupListResponse>() {
//            @Override
//            public void onResponse(Call<ChatGroupListResponse> call, Response<ChatGroupListResponse> response) {
//                if (response != null) {
//                    if (response.isSuccessful()) {
//                        try {
//
//
//                            edittext.setText("");
////                                    ChatGroupList messagObject = new ChatGroupList("0", tuid, fuid, message, "t", "0", sentAt, to_name ,"","","",0);
////                                    chatIndividualsList.add(messagObject);
//                            mAdapter.updateList(response.body().getChat());
//                            senNotification();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ChatGroupListResponse> call, Throwable throwable) {
//                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());
//
//            }
//        });
    }


    private void senNotification() {
        Call<ResponseBody> call = mViewModel.sendSinglePush(getString(R.string.app_name), message, fuid, tuid, "chat");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                           // Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        MenuItem action_call = menu.findItem(R.id.action_call);
        action_call.setVisible(false);

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove:
                final int i = (int) v.getTag(R.string.btn_view_position);
                removeImageFromList(i);
                break;

            case R.id.android_gridview_image:
                pos = (int) v.getTag(R.string.btn_view_position);
                if (isStoragePermissionGranted()) {
                    try {
                        ch = mAdapter.getSelected(pos);
                        openImagePopup(ch.getComment());
//                        new SaveFile().execute(URLs.IMAGE_URL + ch.getComment());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    public void openImagePopup(String image_path) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_view, null);
        //if (!imageVideo[id-1].contains("video")) {

        ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
        Button ivDownload = (Button) view.findViewById(R.id.ivDownload);
        Button ivShare = (Button) view.findViewById(R.id.ivShare);

        Picasso.get().load(URLs.BASE_URL + image_path).into(ivImage);
//            Glide.with(context).load(URLs.BASE_URL + image_path).into(ivImage);

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SaveFile().execute(URLs.IMAGE_URL + image_path);



            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.shareIntent(context, URLs.IMAGE_URL + image_path);
            }
        });

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();



    }

    private void removeImageFromList(int i) {
        try {
            images.remove(i);
            imageList.remove(i);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fillImageList(String selectedImagePath, String type, String image_id) {

        imageList.add(new ImageVideo(selectedImagePath, type, image_id));
        adapter.notifyDataSetChanged();


    }

    private boolean requestCamera = true;
    private ArrayList<String> images = new ArrayList<>();
    private int image_count = 10;
    private String imagePath = "";
    private String selectedImagePath = "";


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
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatListingActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            try {
                selectedImagePath = getImagePath();
                String ImagePath = ImageCompression.compressImage(selectedImagePath, context);
                images.add(ImagePath);
                fillImageList(ImagePath, "image", "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2) {
            getMultipleImagesfromGallery(data);

        }
        else if (requestCode == 100) {
            if (resultCode == RESULT_OK && null != data) {
                int pos = edittext.getSelectionStart();
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                text_entered.insert(pos, result.get(0));
                text_entered.append(" ");
                edittext.setText(text_entered);
                edittext.setSelection(edittext.getText().toString().length());
                edittext.requestFocus();

            }

        }

    }

    public String getImagePath() {
        return imagePath;
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


    private void registerReciever() {
        //Creating broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {

                    //When gcm registration is success do something here if you need

                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT)) {

                    //When the registration token is sent to ther server displaying a toast
                    Toast.makeText(getApplicationContext(), "Chatroom Ready...", Toast.LENGTH_SHORT).show();

                    //When we received a notification when the app is in foreground
                } else if (intent.getAction().equals(URLs.PUSH_NOTIFICATION_GROUP)) {
                    //Getting message data
                    String name = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");
                    String id = tuid;

                    //processing the message to add it in current thread
                    processMessage(name, message, id);
                }
            }
        };

        //if the google play service is not in the device app won't work
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }


    }

    //Registering broadcast receivers
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(URLs.PUSH_NOTIFICATION));


    }


    //Unregistering receivers
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    private void processMessage(String name, String message, String tuid) {
        chatIndividualsList.clear();
        String sentAt = getTimeStamp();
        ChatGroupList messagObject = new ChatGroupList("0", fuid, tuid, message, "t", "0", sentAt, name, "", "", "", 0);
        chatIndividualsList.add(messagObject);
        mAdapter.updateList(chatIndividualsList);
        scrollToBottom();
    }

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1)
            rvList.getLayoutManager().smoothScrollToPosition(rvList, null, mAdapter.getItemCount() - 1);
    }


    private class uplaodDataToServer extends AsyncTask<String, Void, String> {
        private WeakReference<EditText> edittext;
        private ProgressDialog progressDialog;


        public uplaodDataToServer(EditText edittext) {
            this.edittext = new WeakReference<>(edittext);

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

                final String URL = URLs.api_add_pd_comment_2_0;
                MultiImageUtility multipartUtility = new MultiImageUtility(URL, "utf-8");
                multipartUtility.addHeaderField("User-Agent", "CodeJava");
                multipartUtility.addHeaderField("Connection", "close");

                File file_upload[] = new File[images.size()];    //  For Multiple Images

                if (images.size() > 0) {
                    for (int i = 0; i < images.size(); i++) {
                        File f = new File(images.get(i));
                        file_upload[i] = f;
                        multipartUtility.addFilePart("image[]", file_upload[i]);   // for multiple image (check paramter (Key []))
                    }
                }


                // if you want to send any other string value to serevr.
                multipartUtility.addFormField("user_id", user_id);
//                multipartUtility.addFormField("user_name", to_name+ "("+ title+ ")" );
                multipartUtility.addFormField("unique_id", URLs.unique_id);
                multipartUtility.addFormField("pd_id", tuid);
                if (file_type.equals("1"))
                    multipartUtility.addFormField("comment", message);
                multipartUtility.addFormField("file_type", file_type);
                return multipartUtility.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return "failed";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                //  progressDialog.dismiss();
                Log.e("Toast", response);
                if(response.equals("failed")) {
                    openSnackBar(getString(R.string.Failed));
                    progressDialog.dismiss();
                    return;
                }

                if (!response.equals("") || response.isEmpty()) {
                    progressDialog.dismiss();
                    edittext.get().setText("");
                    imageList.clear();
                    images.clear();
                    adapter.notifyDataSetChanged();

                    updateChaList(response);
//                    ChatGroupList messagObject = new ChatGroupList("0", tuid, fuid, message, file_type, "0", getTimeStamp(), to_name, "", "", "", 0);
//                    chatIndividualsList.add(messagObject);
//                    mAdapter.updateList(chatIndividualsList);

                } else {
                    //Glide.with(context).load(R.drawable.ic_baseline_linked_camera_24).into(ivImage);
                    openSnackBar("Failed");
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

    private void updateChaList(String response) {
        try {
            chatIndividualsList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("chat");

            for (int d = 0; d < jsonArray.length(); d++) {
                String message = null;
                String id = jsonArray.getJSONObject(d).getString("id");
                String pd_uid = jsonArray.getJSONObject(d).getString("pd_id");
                String user_id = jsonArray.getJSONObject(d).getString("user_id");
                String status = jsonArray.getJSONObject(d).getString("status");
                String dt = jsonArray.getJSONObject(d).getString("dt");
                String ufname = jsonArray.getJSONObject(d).getString("fname");
                String ulname = jsonArray.getJSONObject(d).getString("lname");
                String last_modified = jsonArray.getJSONObject(d).getString("last_modified");
                String mobile = jsonArray.getJSONObject(d).getString("mobile");

                String file_type = jsonArray.getJSONObject(d).getString("file_type").replaceAll("\\r\\n|\\r|\\n", "");
                if (file_type.trim().equals("1"))
                    message = jsonArray.getJSONObject(d).getString("comment");
                else message = URLs.IMAGE_URL + jsonArray.getJSONObject(d).getString("comment");

                ChatGroupList messagObject = new ChatGroupList(id, pd_uid, user_id, message, file_type, status, dt, last_modified, ufname,ulname,mobile,0);
                chatIndividualsList.add(messagObject);


            }
            mAdapter.updateList(chatIndividualsList);
            scrollToBottom();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/garzoo_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Bitmap viewToBitmap(View view, int widh, int hight) {
        Bitmap bitmap = Bitmap.createBitmap(widh, hight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 2: {
//
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//                    ch = mAdapter.getSelected(pos);
//                    new SaveFile().execute(URLs.IMAGE_URL + ch.getComment());
//                } else {
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//        }
//    }

    class SaveFile extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;
        @Override
        public void onPreExecute() {
            super .onPreExecute();
//        progressDialog = new ProgressDialog(getC);
//        progressDialog.setMessage("Please wait");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                File mydir = new File(context.getExternalFilesDir("") + "/Garzoo");
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(url[0]);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
                String date = dateFormat.format(new Date());

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, date + ".jpg");

                manager.enqueue(request);
                return mydir.getAbsolutePath() + File.separator + date + ".jpg";
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            //  progressDialog.dismiss();
            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
        }



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

            case 2: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    ch = mAdapter.getSelected(pos);
                    new SaveFile().execute(URLs.IMAGE_URL + ch.getComment());
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(GroupChatListingActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                // .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    //--------------------------------Permission End--------------------------
    private void startVoiceInput(int code) {
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

    private void getFbAd() {
        RelativeLayout bannerAdContainer = (RelativeLayout) findViewById(R.id.bannerAdContainer);
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }


        // Create a banner's ad view with a unique placement ID (generate your own on the Facebook
        // app settings). Use different ID for each ad placement in your app.
        bannerAdView =
                new com.facebook.ads.AdView(
                        this,
                        "390694838957593_416544323039311",
                        com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        // Reposition the ad and add it to the view hierarchy.
        bannerAdContainer.addView(bannerAdView);

        // Initiate a request to load an ad.
        bannerAdView.loadAd(bannerAdView.buildLoadAdConfig().withAdListener(this).build());
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "onError:- " + adError.getErrorCode());
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.d(TAG, "onAdLoaded: ");
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "onAdClicked: ");
    }

    @Override
    public void onLoggingImpression(Ad ad) {

        Log.d(TAG, "onLoggingImpression: ");
    }


    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

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

    @Override
    public void onText(CharSequence charSequence) {

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

    private void BackSpaceMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();

        switch (editTextId) {
            case R.id.edittext:
                text_entered.delete(0, title.length());
                text_entered.append(editTextData);
                text_entered.append("");
                break;


        }
    }

    private void SoftKeyMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();


        switch (editTextId) {
            case R.id.edittext:
                text_entered.delete(0, title.length());
                text_entered.append(editTextData);
                text_entered.append("");
                break;

        }

    }

    private void DeleteMethod(AppCompatEditText edittext) {
        int editTextId = edittext.getId();
        String editTextData = edittext.getText().toString();

        switch (editTextId) {
            case R.id.etTitle:
                text_entered.delete(0, title.length());
                break;



        }

    }

    private void setKeyboard() {
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SoftKeyMethod((AppCompatEditText) edittext);
            }
        });


    }

}