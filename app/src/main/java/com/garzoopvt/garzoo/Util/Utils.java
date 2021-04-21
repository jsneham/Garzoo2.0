package com.garzoopvt.garzoo.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.garzoopvt.garzoo.Chat.Activity.IndividualChatActivity;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Login.Activity.LoginActivity;
import com.garzoopvt.garzoo.R;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class Utils {
    public static ProgressDialog mProgressDialog;
    public static Toast toast = null;
    public static  String fileUri = null;




    public static void checkErrorPresent(Context context, EditText editText) {
        if (editText.getError() != null)
            editText.setError(null);

    }

    public static String getHeaderfromAPIResponseVolley(String header_key, Map<String, String> headers) {
        String data = headers.get(header_key);
        if (data != null) {
            return data;
        }
        return "";
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void connectionAvailable(View body, View conn) {
        // loader.setVisibility(View.VISIBLE);
        body.setVisibility(View.VISIBLE);
        conn.setVisibility(View.GONE);

    }


    public static void connectionOut(View body, View conn) {
        // loader.setVisibility(View.GONE);
        body.setVisibility(View.GONE);
        conn.setVisibility(View.VISIBLE);
    }
    /* public static void showToast(Context context, String message) {
         Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

     }
 */


    public static void showToast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void showLoadingDialog(Context cx) {
        //   if (mProgressDialog == null) {
        mProgressDialog = new ProgressDialog(cx);
//        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait while loading...");
        mProgressDialog.setCancelable(true); // enable dismiss by tapping outside of the dialog
        mProgressDialog.show();
        //  }
    }

    public static void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }


    public static String getInputValue(EditText editText) {
        return editText.getText().toString().trim();
    }


    public static String onlyFirstLetterUpperCase(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

//Utils.applyRippleEffectOnView(btnFillAddresss, 0xE1E0E0, 300);

    public static void log(String TAG, String title, String message) {

        Log.e(TAG, title + ": " + message);
    }


    public static String replaceSquareBrackets(String str) {
//        String str = "[Chrissman-@1]";
        return str.replaceAll("\\[", "").replaceAll("\\]", "");
    }


    //BEST ONE FOR DATE
    public static String formateDate(String date, String fromFormat, String toFormat) {
//        01 Aug 2017  dd MMM yyyy
        SimpleDateFormat sdfg = new SimpleDateFormat(fromFormat);
        Date d1;
        try {
            d1 = sdfg.parse(date);
            return new SimpleDateFormat(toFormat).format(d1).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String formateDate(String date) {
        String toFormat="dd MMM yyyy";
        String fromFormat="yyyy-MM-dd HH:mm:ss";

//        01 Aug 2017  dd MMM yyyy
        SimpleDateFormat sdfg = new SimpleDateFormat(fromFormat, new Locale ( "en" , "IN" ));
        Date d1;
        try {
            d1 = sdfg.parse(date);
            return new SimpleDateFormat(toFormat, new Locale ( "en", "IN" )).format(d1).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void openSnackBar(String response, View rootView) {
        Snackbar snackbar;
        snackbar = Snackbar.make(rootView, response, Snackbar.LENGTH_LONG);

        snackbar.show();
    }
    public static void shareIntent(Context context) {

        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            String data = URLs.BASE_URL + "<meta property=\"og:site_name\" content=\"" +URLs.BASE_URL
                   +"\"><meta property=\"og:title\" content=\"GarZoo\" /><meta property=\"og:image\" itemprop=\"image\" content=product_image><meta property=\"og:type\" content=\"Android\" />";

            i.putExtra(Intent.EXTRA_TEXT, URLs.Share_URL_);
            context.startActivity(Intent.createChooser(i, "Select App to Share Data"));

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static void shareIntent(Context context, String imagePath) {

//        try {
//            Picasso.get().load(imagePath).into(new Target() {
//                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("image/*");
//                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
//                    context.startActivity(Intent.createChooser(i, "Select App to Share"));
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//
//            });
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }



            Picasso.get().load(imagePath).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        File mydir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Garzoo");
                        if (!mydir.exists()) {
                            mydir.mkdirs();
                        }

                       fileUri = mydir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                        FileOutputStream outputStream = new FileOutputStream(fileUri);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    Uri uri= Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), BitmapFactory.decodeFile(fileUri),null,null));
                    // use intent to share image
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    context.startActivity(Intent.createChooser(share, "Share Image"));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

            });



    }

    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static void openCallFunction(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }



    public static void openLogin(Context context) {
       openLoginPopup(context);
    }

    private static void openLoginPopup(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.loginpopup, null);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvNo= view.findViewById(R.id.tvNo);
        Button login= view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent in= new Intent(context, LoginActivity.class);
                context.startActivity(in);
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });




    }



    public static void downLoadIntent(Context context, String image_path) {

        try {
//            new SaveFile().execute(URLs.IMAGE_URL + image_path);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }




}
