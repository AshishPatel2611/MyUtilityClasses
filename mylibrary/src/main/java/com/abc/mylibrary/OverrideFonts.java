package com.abc.mylibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by hexagon on 31/5/17.
 */

public class OverrideFonts {
    private Context CONTEXT;
    private String FONT_FILE_PATH;
    private View VIEW;

    /*********** Fonts Path **************/
    public static String LIGHT_FONTS = "fonts/light_font.ttf";
    /*************************************/



    OverrideFonts(Context context, View view, String path_of_fonts, boolean isViewGroup) {
        this.CONTEXT = context;
        this.FONT_FILE_PATH = path_of_fonts;
        this.VIEW = view;

        if (isViewGroup) {
            overrideFontsForViewGroup(VIEW);
        } else {
            overridefontsForTextView();
        }


    }

    private void overridefontsForTextView() {

        try {
            if (VIEW instanceof Button) {
                ((Button) VIEW).setTypeface(Typeface.createFromAsset(CONTEXT.getAssets(), FONT_FILE_PATH));
            } else if (VIEW instanceof TextView) {
                ((TextView) VIEW).setTypeface(Typeface.createFromAsset(CONTEXT.getAssets(), FONT_FILE_PATH));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void overrideFontsForViewGroup(View VIEW) {
        try {
            if (VIEW instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) VIEW;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFontsForViewGroup(child);
                }
            } else if (VIEW instanceof Button) {
                ((Button) VIEW).setTypeface(Typeface.createFromAsset(CONTEXT.getAssets(), FONT_FILE_PATH));
            } else if (VIEW instanceof TextView) {
                ((TextView) VIEW).setTypeface(Typeface.createFromAsset(CONTEXT.getAssets(), FONT_FILE_PATH));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// with font chechking...
	
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

	   private static Typeface getButtonBoldFont(Context context) {
        Typeface normalFont = null;
        if (normalFont == null) {
            normalFont = Typeface.createFromAsset(context.getAssets(), Const.BOLD_FONTS_FOR_BUTTONS);
        }
        return normalFont;
    }
  private static Typeface getNormalFont(Context context) {
        Typeface normalFont = null;
        if (normalFont == null) {
            normalFont = Typeface.createFromAsset(context.getAssets(), Const.REGULAR_FONTS);
        }
        return normalFont;
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

}
