package com.abc.mylibrary;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.simpl.BaseApplication;
import com.simpl.BaseConstant;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Mandy on 4/11/2016.
 */
public class Utility {
    private static Utility instance;

    public static Utility getInstance() {
        if (instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    public int[] getScreenWidthHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth(); // deprecated
        int height = display.getHeight(); // deprecated

        int[] wh = {width, height};

        return wh;
    }

    public void ButtonClickEffect(final View v) {

        AlphaAnimation obja = new AlphaAnimation(1.0f, 0.3f);
        obja.setDuration(5);
        obja.setFillAfter(false);
        v.startAnimation(obja);
    }

    public Uri getOutputMediaFileUri(String dirName, int type) {
        File mediaStorageDir = new File(dirName);

        // megabytesAvailable(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Debug.trace(dirName, "Oops! Failed create " + dirName + " directory");
                return null;
            }
        }

        String ext = "";
        String fileType = "";

        switch (type) {

            case BaseConstant.MEDIA_AUDIO:
                fileType = "AUD";
                ext = ".mp3";
                break;
            case BaseConstant.MEDIA_VIDEO:
                fileType = "VID";
                ext = ".3gp";
                break;
            case BaseConstant.MEDIA_IMAGE:
                fileType = "IMG";
                ext = ".jpg";
                break;
        }

        // Create a media file name
        File mediaFile;
        int n = 10000;
        Random generator = new Random();

        n = generator.nextInt(n);

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileType + n + ext);
        return Uri.fromFile(mediaFile);
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null || target.length() < 1) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    //  Check for soft navigation bar availablity
    public boolean isNavigationBarAvailable() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return (!(hasBackKey && hasHomeKey));
    }

    public void setupOutSideTouchHideKeyboard(final View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view);
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupOutSideTouchHideKeyboard(innerView);
            }
        }
    }

    public void hideKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getDateInString(Date dates, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return dateFormat.format(dates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Date getDate(String strDate) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getFirstDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

      /*  Log.i(TAG, "getFirstDayOfQuarter: Calendar.MONTH ==" + Calendar.MONTH);
        Log.i(TAG, "getFirstDayOfQuarter: cal.get(Calendar.MONTH)==" + (cal.get(Calendar.MONTH)));


        Log.d(TAG, "((cal.get(Calendar.MONTH)/3)+1)  returned: " + ((cal.get(Calendar.MONTH) / 3) + 1));

        Log.e(TAG, "  cal.get(Calendar.MONTH) :" + cal.get(Calendar.MONTH));
        Log.e(TAG, "  cal.get(Calendar.YEAR) :" + cal.get(Calendar.YEAR));
        Log.e(TAG, "  cal.get(Calendar.DAY_OF_MONTH) :" + cal.get(Calendar.DAY_OF_MONTH));
        Log.e(TAG, "  cal.get(Calendar.DATE) :" + cal.get(Calendar.DATE));
        Log.e(TAG, "  cal.get(Calendar.DAY_OF_YEAR) :" + cal.get(Calendar.DAY_OF_YEAR));
        Log.e(TAG, "  cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) :" + cal.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        Log.e(TAG, "  cal.get(Calendar.WEEK_OF_MONTH) :" + cal.get(Calendar.WEEK_OF_MONTH));
        Log.e(TAG, "  cal.get(Calendar.WEEK_OF_YEAR) :" + cal.get(Calendar.WEEK_OF_YEAR));

        Log.i(TAG, "getFirstDayOfQuarter: ( cal.get(Calendar.MONTH) / 3) ==" + (cal.get(Calendar.MONTH) / 3));
        Log.i(TAG, "getFirstDayOfQuarter: (cal.get(Calendar.MONTH) / 3 * 3) ==" + (cal.get(Calendar.MONTH) / 3 * 3));
*/
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(cal.getTime());
    }

    public String getLastDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
       /* Log.w(TAG, "getLastDayOfQuarter: Calendar.MONTH ==" + Calendar.MONTH);
        Log.w(TAG, "getLastDayOfQuarter: cal.get(Calendar.MONTH)==" + (cal.get(Calendar.MONTH)));

        Log.w(TAG, "getLastDayOfQuarter: ( cal.get(Calendar.MONTH) / 3) ==" + (cal.get(Calendar.MONTH) / 3));

        Log.w(TAG, "getLastDayOfQuarter: ( cal.get(Calendar.MONTH) / 3 * 3 + 2) ==" + (cal.get(Calendar.MONTH) / 3 * 3 + 2));
      */  cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3 + 2);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(cal.getTime());
    }
}
