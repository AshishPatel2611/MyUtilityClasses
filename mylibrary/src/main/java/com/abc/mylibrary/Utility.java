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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    } private static final String TAG = "Utility_method";

    public static boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().isConnected();
    }


    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.PHONE.matcher(target).matches() && target.length() > 6;
    }

    public static boolean isValidPassword(CharSequence password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static boolean isStringValid(String object) {
        boolean flag = false;
        if (object != null && !object.equalsIgnoreCase("null")
                && object.trim().length() > 0) {
            flag = true;
        }
        return flag;
    }

    public static boolean isNameValid(String object) {
        boolean flag = false;
        if (object != null && !object.equalsIgnoreCase("null")
                && object.trim().length() > 3) {
            flag = true;
        }
        return flag;
    }

    public static void systemUpgrade(Context context) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        int level = Integer.parseInt(Prefs.getValue(context, "LEVEL", "0"));

        if (level == 0) {
            dbHelper.upgrade(level);
            // Create not confirmed order
            level++;
        }
        Prefs.setValue(context, "LEVEL", level + "");
    }

    public static String getDateDiffStringInDays(Date dateOne, Date dateTwo) {

        long timeOne = dateOne.getTime();
        long timeTwo = dateTwo.getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;

        if (delta > 0) {
            return (delta + 1) + " Days";
        } else {
            delta *= -1;
            return "-" + delta + " Days";
        }
    }


    public static void showAlert(Context context, String title, String message, String btnText) {

        // DashboardPopup(((Activity) context), title, message, null, Const.Up,
        // false, false, false, null);

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(btnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public static void showAlert(Context context, String title, String message, String btnText1, String btnText2) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(btnText1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setButton2(btnText2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public static String convertDateToString(Date objDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStringToDate(String dateformet, Date date) {
        String newString = null;
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(dateformet);
            newString = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return newString;
    }

    public static String convertDateStringToString(String strDate, String currentFormat, String parseFormat)

    {
        try {
            return convertDateToString(convertStringToDate(strDate, currentFormat), parseFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String UnicodetoUTF8(String str) {
        String stringUTF = "";
        try {
            // Convert from Unicode to UTF-8
            stringUTF = str;
            byte[] utf8 = stringUTF.getBytes("UTF-8");

            // Convert from UTF-8 to Unicode
            stringUTF = new String(utf8, "UTF-8");
            // Log.print(stringUTF);
        } catch (UnsupportedEncodingException e) {
        }
        return stringUTF;
    }

    public static String millisToDate(long millis, String format) {

        return new SimpleDateFormat(format).format(new Date(millis));
    }

    public static String usingDateFormatter(long input) {
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd hh:mm:ss z");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }

    public static String getTimeWithAM_PM(long input) {
        Date date = new Date(input);
        Calendar cal = Calendar.getInstance();/* = new GregorianCalendar(TimeZone.getTimeZone("GMT+5:30"));*/
        cal.setTime(date);
        return (pad(cal.get(Calendar.HOUR))
                + ":" + pad(cal.get(Calendar.MINUTE))
                + " " + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM")
        );

    }

    public static String getDatewithMonth(long input) {
        Date date = new Date(input * 1000);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return (cal.get(Calendar.DATE)
                + " " + getMonthForInt(cal.get(Calendar.MONTH))
                + " " + cal.get(Calendar.YEAR));

    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num].substring(0, 3);
        }
        return month;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    public static Date convertStringToDate(String strDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDifference(Date sessionStart, Date sessionEnd) {
        if (sessionStart == null)
            return "";

        if (sessionEnd == null)
            return "";
        Calendar startDateTime = Calendar.getInstance();
        startDateTime.setTime(sessionStart);

        Calendar endDateTime = Calendar.getInstance();
        endDateTime.setTime(sessionEnd);

        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;

        long hours = diff / (60 * 60 * 1000);
        long minutes = diff / (60 * 1000);
        minutes = minutes - 60 * hours;
        long seconds = diff / (1000);

        if (hours > 0) {
            return hours + " hours " + minutes + " minutes";
        } else {
            if (minutes > 0)
                return minutes + " minutes";
            else {
                return seconds + " seconds";
            }
        }
    }

    public static long datetimeToMillis(String Date, String Time, String parseFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(parseFormat);
        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date date = null;
        try {
            date = sdf.parse(Date + " " + Time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long stringToMilisec(String date, String parseFormat) {
        return Utility_method.convertStringToDate(date, parseFormat).getTime();
    }

    // public static Bitmap StringToBitMap(String encodedString) {
    // try {
    // byte[] encodeByte = Base64.decode(encodedString);
    // Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
    // encodeByte.length);
    // return bitmap;
    // } catch (Exception e) {
    // e.getMessage();
    // return null;
    // }
    // }

    // public static String[] arrListToArray(ArrayList<ModeBean> arrList, int
    // Mode) {
    // String[] objStr = null;
    // try {
    // if (arrList != null) {
    // objStr = new String[arrList.size()];
    // int index = 0;
    // for (ModeBean obj : arrList) {
    // if (Mode == 1) {
    // objStr[index] = obj.modeName;
    // } else if (Mode == 2) {
    // objStr[index] = obj.modeId + "";
    // }
    // index++;
    // }
    // }
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // return objStr;
    //
    // }

    public static int indexOfArray(String[] strArray, String strFind) {
        int index;

        for (index = 0; index < strArray.length; index++)
            if (strArray[index].equals(strFind))
                break;

        return index;
    }

    public static void turnGPSOn(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);

        }
    }

    public static void turnGPSOff(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps")) { // if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static String getPath(Uri uri, Activity context) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    public static Bitmap resize(Bitmap bitMap, int width, int height) {
        int per;
        int bitWidth = bitMap.getWidth();
        int bitHeight = bitMap.getHeight();

        if (bitHeight < bitWidth) {
            per = (height * 100) / bitHeight;
            bitHeight = height;
            bitWidth = (bitWidth * per) / 100;
        } else {
            per = (width * 100) / bitWidth;
            bitWidth = width;
            bitHeight = (bitHeight * per) / 100;
        }


        return Bitmap.createScaledBitmap(bitMap, bitWidth, bitHeight, false);
    }

    public static long dateToMilisec(String parseFormat) {
        return Utility_method.convertStringToDate(convertDateToString(new Date(), parseFormat), parseFormat).getTime();
    }

    public static String pad(int c) {
        return c >= 10 ? String.valueOf(c) : "0" + String.valueOf(c);
    }


    public static void changeLang(String lang, Context context) {
        Locale myLocale;
        if (lang.equalsIgnoreCase(""))
            return;
        String newLang = "";
        if (lang.toString().equals("tr_TR")) {
            newLang = "ku";
        } else if (lang.toString().equals("en_US")) {
            newLang = "en";
        } else if (lang.toString().equals("ar_SY")) {
            newLang = "ar";
        }
        if (newLang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(newLang);

        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void getKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.i(TAG, "getKeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }


    public static void overrideLightFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideLightFonts(context, child);
                }
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.LIGHT_FONTS));
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.LIGHT_FONTS));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void overrideRegularFonts(final Context context, final ViewGroup group) {

        try {
            Typeface font;
            int count = group.getChildCount();
            View v;
//        Log.i(TAG, "overrideRegularFonts: ");
            for (int i = 0; i < count; i++) {
                v = group.getChildAt(i);
                if (v instanceof TextView) {
                    if (((TextView) v) != null) {
                        if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isBold()) {
                            ((TextView) v).setTypeface(getBoldFont(context));
                        } else if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isItalic()) {
                            Log.d(TAG, "overrideRegularFonts: button");
                            ((TextView) v).setTypeface(getButtonBoldFont(context));
                        } else {
                            ((TextView) v).setTypeface(getNormalFont(context));
                        }
                    }
                } else if (v instanceof EditText) {
                    if (((EditText) v) != null) {
                        if (((EditText) v).getTypeface() != null && ((EditText) v).getTypeface().isBold()) {
                            ((EditText) v).setTypeface(getBoldFont(context));
                        } else if (((EditText) v).getTypeface() != null && ((EditText) v).getTypeface().isItalic()) {
                            Log.d(TAG, "overrideRegularFonts: button ");
                            ((EditText) v).setTypeface(getButtonBoldFont(context));
                        } else {
                            ((EditText) v).setTypeface(getNormalFont(context));
                        }
                    }
                } else if (v instanceof ViewGroup) setFont((ViewGroup) v, context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void overrideBoldFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideBoldFonts(context, child);
                }
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS));
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void BoldFont(final Context context, final View v) {
        if (v instanceof Button) {
//            Log.i(TAG, "BoldFont: ");
            ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS));

        } else if (v instanceof TextView) {
//            Log.i(TAG, "BoldFont: ");
            ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS));

        }
    }


    public static void setFont(ViewGroup group, Context context) {
        Typeface font;
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView) {
                if (((TextView) v) != null) {
                    if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isBold()) {
                        ((TextView) v).setTypeface(getBoldFont(context));
                    } else if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isItalic()) {
                        Log.d(TAG, "overrideRegularFonts: button ");
                        ((TextView) v).setTypeface(getButtonBoldFont(context));
                    } else {
                        ((TextView) v).setTypeface(getNormalFont(context));
                    }
                }
            } else if (v instanceof EditText) {
                if (((EditText) v) != null) {
                    if (((EditText) v).getTypeface() != null && ((EditText) v).getTypeface().isBold()) {
                        ((EditText) v).setTypeface(getBoldFont(context));
                    } else if (((EditText) v).getTypeface() != null && ((EditText) v).getTypeface().isItalic()) {
                        Log.d(TAG, "overrideRegularFonts: button ");
                        ((EditText) v).setTypeface(getButtonBoldFont(context));
                    } else {
                        ((EditText) v).setTypeface(getNormalFont(context));
                    }
                }
            } else if (v instanceof ViewGroup) setFont((ViewGroup) v, context);
        }
    }


    private static Typeface getNormalFont(Context context) {
        Typeface normalFont = null;
        if (normalFont == null) {
            normalFont = Typeface.createFromAsset(context.getAssets(), Const.REGULAR_FONTS);
        }
        return normalFont;
    }

    private static Typeface getButtonBoldFont(Context context) {
        Typeface normalFont = null;
        if (normalFont == null) {
            normalFont = Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS_FOR_BUTTONS);
        }
        return normalFont;
    }

    private static Typeface getBoldFont(Context context) {
        Typeface boldFont = null;
        if (boldFont == null) {
            boldFont = Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS);
        }
        return boldFont;
    }


    public static void replaceFragment(FragmentManager fm, Fragment targetFragment, int container, String tag) {
        try {
            FragmentTransaction myFragmentTransaction = fm.beginTransaction();
            boolean fragmentPopped = fm.popBackStackImmediate(tag, 0);
            if (!fragmentPopped) {
                myFragmentTransaction
                        .addToBackStack(tag)
                        .add(container, targetFragment, tag)
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
