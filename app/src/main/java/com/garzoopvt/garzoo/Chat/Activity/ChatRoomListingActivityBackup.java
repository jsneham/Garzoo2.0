package com.garzoopvt.garzoo.Chat.Activity;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.Chat.Adapter.ChatTextAdapter;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Receiver.GCMRegistrationIntentService;
import com.garzoopvt.garzoo.Chat.Services.ChatIndividualResponse;
import com.garzoopvt.garzoo.Chat.ViewModel.ChatRoomViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ImageCompression;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomListingActivityBackup extends AppCompatActivity implements View.OnClickListener {

    //Data
    private String TAG = "IndividualChatActivity";
    private SessionManager sessionManager;
    private Context context = this;
    private String fuid, tuid, to_name, record_id, phone_no;
    private String user_id;
    private ChatUser chatArrayList;
    private String file_type = "t";
    private String message = "";
    private ArrayList<ChatIndividual> chatIndividualsList = new ArrayList<>();

    //View
    private RecyclerView rvList;
    private EditText edittext;
    private RelativeLayout rlLayout;
    private ImageView ivImage, btnAttach, btnSend, btnMic;

    //instances
    private ChatRoomViewModel mViewModel;
    private ChatTextAdapter mAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        mViewModel = ViewModelProviders.of(this).get(ChatRoomViewModel.class);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        // chatArrayList = (ChatUser) getIntent().getParcelableExtra("data");
        fuid = user_id;
        tuid = getIntent().getStringExtra("tuid");
        phone_no = getIntent().getStringExtra("phone_no");
        to_name = getIntent().getStringExtra("to_name");
        init();
        getToolBar();
        subscribeObservers();
        getChats();
        getBannerAds();

        registerReciever();
    }


    private void getToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setTitle(to_name);


    }

    private void init() {
        rlLayout = (RelativeLayout) findViewById(R.id.rlLayout);
        rvList = (RecyclerView) findViewById(R.id.recyclerView);
        rvList.setHasFixedSize(true);
        mAdapter = new ChatTextAdapter(context, user_id, this);
        rvList.setNestedScrollingEnabled(true);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(mAdapter);


        edittext = findViewById(R.id.edittext);
        btnMic = findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startVoiceInput();
            }
        });

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file_type = "t";
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

        mViewModel.getChats().observe(this, new Observer<Resource<List<ChatIndividual>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatIndividual>> listResource) {
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
                                //     Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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
        mViewModel.getChatListApi(user_id, 1, tuid);
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


        MultipartBody.Part list[] = new MultipartBody.Part[images.size()];
        for (int j = 0; j < images.size(); j++) {
            MultipartBody.Part imageRequest = prepareFilePart("image[]", Uri.parse(images.get(j)), images.get(j));
            list[j] = imageRequest;
            file_type = "i";
        }

        String sentAt = getTimeStamp();
        RequestBody unique_id = RequestBody.create(MultipartBody.FORM, URLs.unique_id);
        RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody rb_to_user_id = RequestBody.create(MultipartBody.FORM, tuid);
        RequestBody rb_message = RequestBody.create(MultipartBody.FORM, message);
        RequestBody rb_file_type = RequestBody.create(MultipartBody.FORM, file_type);

        Call<ChatIndividualResponse> call = mViewModel.addChat(unique_id, rb_user_id, rb_to_user_id,
                rb_message, rb_file_type, list);

        call.enqueue(new Callback<ChatIndividualResponse>() {
            @Override
            public void onResponse(Call<ChatIndividualResponse> call, Response<ChatIndividualResponse> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {


                            edittext.setText("");
//                                    ChatIndividual messagObject = new ChatIndividual("0", tuid, fuid, message, "t", "0", sentAt, to_name ,"","","",0);
//                                    chatIndividualsList.add(messagObject);
                            mAdapter.updateList(response.body().getChat());
                            senNotification();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }

            }

            @Override
            public void onFailure(Call<ChatIndividualResponse> call, Throwable throwable) {
                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());

            }
        });
    }


    private void senNotification() {
        Call<ResponseBody> call   = mViewModel.sendSinglePush(getString(R.string.app_name), message,fuid,tuid,"chat");

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

        return true;
    }

    @Override
    public void onClick(View view) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomListingActivityBackup.this);
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


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2) {
            getMultipleImagesfromGallery(data);

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
                } else if (intent.getAction().equals(URLs.PUSH_NOTIFICATION)) {
                    //Getting message data
                    String name = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");
                    String notificationCount = intent.getStringExtra("notificationCount");
                    String id = tuid;//intent.getStringExtra("id");

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
        String sentAt = getTimeStamp();
        ChatIndividual messagObject = new ChatIndividual("0", fuid, tuid, message, "t", "0", sentAt, name, "", "", "", 0);
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

}