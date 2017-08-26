package com.abc.mylibrary;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by hexagon on 31/5/17.
 */

public class StringValidation {


    String charSequence = "";
    public static int SIMPLE_TEXT = 0;
    public static int EMAIL = 1;
    public static int PHONE_NUMBER = 2;
    public static int ADDRESS = 3;
    public static int WEB_URL = 4;
    public static int DOMAIN_NAME = 5;
    public static int IP_ADDRESS = 6;

    boolean isValid(String charSequence, int StringType) {
        this.charSequence = charSequence;
        boolean isValid = false;
        switch (StringType) {
            case 0:
                isValid = isStringValid(charSequence);
                break;
            case 1:
                isValid = isValidEmail(charSequence);
                break;
            case 2:
                isValid = isValidPhone(charSequence);
                break;
            case 3:
                isValid = isValidAddress(charSequence);
                break;
            case 4:
                isValid = isValidWebURL(charSequence);
                break;
            case 5:
                isValid = isValidDomainName(charSequence);
                break;
            case 6:
                isValid = isValidIPaddress(charSequence);
                break;
            default:
                return isValid;

        }
        return isValid;

    }

    public static boolean isStringValid(String target) {
        return !TextUtils.isEmpty(target);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();

        /*
        *  "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
         */
    }

    public static boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches() && target.length() > 6;
        /*
        *   // sdd = space, dot, or dash
                "(\\+[0-9]+[\\- \\.]*)?"        // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"); // <digit><digit|sdd>+<digit>
                */
    }

    public static boolean isValidIPaddress(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches() && target.length() > 6;
        /*
        *  "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))")
            */
    }

    public static boolean isValidDomainName(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.DOMAIN_NAME.matcher(target).matches() && target.length() > 6;
    }

    public static boolean isValidWebURL(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.WEB_URL.matcher(target).matches() && target.length() > 6;
    }

    public static boolean isValidAddress(String target) {
        return target != null
                && !TextUtils.isEmpty(target)
                && !target.equalsIgnoreCase("null")
                && target.length() > 6;
    }
}
