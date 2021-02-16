package com.garzoopvt.garzoo.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.garzoopvt.garzoo.Login.Activity.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class Utils {
    public static ProgressDialog mProgressDialog;
    public static Toast toast = null;




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

    public static void openSnackBar(String response, View rootView) {
        Snackbar snackbar;
        snackbar = Snackbar.make(rootView, response, Snackbar.LENGTH_SHORT);

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

    public static void openCallFunction(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }



    public static void openLogin(Context context) {
       Intent in= new Intent(context, LoginActivity.class);
       context.startActivity(in);
    }


}
