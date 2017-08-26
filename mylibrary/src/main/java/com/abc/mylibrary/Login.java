package com.hexa.stylist;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hexa.stylist.bean.SocialRegisterBean;
import com.hexa.stylist.process.ForgotPasswordProcess;
import com.hexa.stylist.process.IsUserExistedProcess;
import com.hexa.stylist.process.LoginProcess;
import com.hexa.stylist.process.UpdateGCMtokenProcess;
import com.hexa.stylist.utils.Const;
import com.hexa.stylist.utils.Prefs;
import com.hexa.stylist.utils.Utils;
import com.hexa.stylist.utils.WebInterface;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class Login extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = "Login";
    private ImageView imgLoginEmail;
    private ImageView imgLoginPassword;
    private TextView txtLoginForgotPassword;
    private ImageView imgLoginGoogle;
    private ImageView imgLoginFacebook;
    private ImageView imgLoginTwitter;
    LoginButton loginButton;

    private TextView txtLoginSignup;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignIn;
    private InputMethodManager objInputMethodManager;

    //For Facebook
    private CallbackManager mCallbackManager;

    //For GooGle Login
    private static final int RC_SIGN_IN = 9001;
    public static GoogleApiClient mGoogleApiClient;

    //For Twitter Login
    private TwitterLoginButton twLoginButton;

    public static SocialRegisterBean beanSocialRegister;
    String providers = "email";

    RelativeLayout rl_login;
    ImageView img_noInternetError;

    boolean isBackPressInternet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_login);
        objInputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);

        imgLoginEmail = (ImageView) findViewById(R.id.img_login_email);
        imgLoginPassword = (ImageView) findViewById(R.id.img_login_password);
        findViewById(R.id.btn_login_signIn).setOnClickListener(this);
        txtLoginForgotPassword = (TextView) findViewById(R.id.txt_login_forgotPassword);
        imgLoginGoogle = (ImageView) findViewById(R.id.img_login_google);
        imgLoginFacebook = (ImageView) findViewById(R.id.img_login_facebook);
        imgLoginTwitter = (ImageView) findViewById(R.id.img_login_twitter);
        twLoginButton = (TwitterLoginButton) findViewById(R.id.twbtn_login_twitter);
        txtLoginSignup = (TextView) findViewById(R.id.txt_login_signup);

        edtEmail = (EditText) findViewById(R.id.edt_login_email);
        edtPassword = (EditText) findViewById(R.id.edt_login_pass);
        btnSignIn = (Button) findViewById(R.id.btn_login_signIn);

        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        img_noInternetError = (ImageView) findViewById(R.id.img_login_noInternet);

        txtLoginForgotPassword.setOnClickListener(this);
        imgLoginGoogle.setOnClickListener(this);
        imgLoginFacebook.setOnClickListener(this);
        imgLoginTwitter.setOnClickListener(this);
        txtLoginSignup.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);


        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.fbbtn_login_facebook);
        loginButton.setBackgroundResource(R.drawable.icon_fb);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                // Application code
                                try {
                                    getFacebookProfile(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,first_name,last_name,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                //handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        /**
         * FOR GOOGLE LOGIN CODE
         */
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * FOR TWITTER LOGIN CODE
         */
        twLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = result.data;


                Call<User> userResult = Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false);
                userResult.enqueue(new Callback<User>() {

                    @Override
                    public void failure(TwitterException e) {

                    }

                    @Override
                    public void success(Result<User> userResult) {

                        User user = userResult.data;
                        getTwitterProfile(user);
                    }

                });
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_login_google:
                if (WebInterface.isOnline(Login.this)) {
                    signInGoogle();
                } else {
                    isBackPressInternet = true;
                    rl_login.setVisibility(View.GONE);
                    img_noInternetError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_login_facebook:
                if (WebInterface.isOnline(Login.this)) {
                    loginButton.performClick();
                } else {
                    isBackPressInternet = true;
                    rl_login.setVisibility(View.GONE);
                    img_noInternetError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_login_twitter:
                if (WebInterface.isOnline(Login.this)) {
                    twLoginButton.performClick();
                } else {
                    isBackPressInternet = true;
                    rl_login.setVisibility(View.GONE);
                    img_noInternetError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_login_signup:
                Intent iReg = new Intent(Login.this, Register.class);
                startActivity(iReg);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.txt_login_forgotPassword:
                forgotPasswordDialog();
                break;

            case R.id.btn_login_signIn:
                //TODO implement
                beanSocialRegister = null;
                providers = "email";
                logIn(edtEmail.getText().toString(), edtPassword.getText().toString().trim());
               /* Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
*/
                break;
        }
    }

    private void logIn(String email, String password) {
        if (fieldValidation()) {
            try {
                objInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
            if (WebInterface.isOnline(Login.this)) {
                try {
                    new LoginProcess(Login.this,
                            email.toString(),
                            password.toString().trim(),
                            providers, handler).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    // myLog.e(TAG, "signUp: " + e);
                }
            } else {
                isBackPressInternet = true;
                rl_login.setVisibility(View.GONE);
                img_noInternetError.setVisibility(View.VISIBLE);
                //Utils.showAlert(Login.this, "Network", "Network not available.", "OK");
            }
        }
    }

    /*To check validation in login form*/
    private boolean fieldValidation() {
        boolean flag = true;

        if (Utils.isStringValid(edtEmail.getText().toString())) {
            flag = false;
            edtEmail.setError("Enter Email Id!");
        } else if (Utils.isStringValid(edtPassword.getText().toString())) {
            flag = false;
            edtPassword.setError("Enter Password!");
        } else if (Utils.isEmailValid(edtEmail.getText().toString())) {
            flag = false;
            edtEmail.setError("Enter Valid Email address!");
        }
        return flag;
    }

    /*    for handeling response from asynctask of loginProcess.class */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                if (msg.arg1 == 1) {
                    if (msg.what == 1) { // Login
                        if (Prefs.getValue(Login.this, Const.GCM_DEVICE_TOKEN, "").equals("") ||
                                Prefs.getValue(Login.this, Const.GCM_DEVICE_TOKEN, "") == null) {
                            updateGCMToken(); // FCM token for pushh notification
                        } else {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        //  Utils.showAlert(Register.this, "", msg.obj.toString(), "Ok");
                    } else if (msg.what == 2) { // fcm token updated successfully
                        Prefs.setValue(Login.this, Const.GCM_DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (msg.what == 3) { // IS USER EXISTED OR NOT
                        String email = "";
                        if (msg.obj.toString().split("#").length > 1) {
                            if (!msg.obj.toString().split("#")[1].toString().equals(""))
                                email = msg.obj.toString().split("#")[1];
                            else
                                email = "";
                        }
                        if (Prefs.getValue(Login.this, Const.IS_USER_EXISTED, "").equals("true")
                                && !email.toString().equals("")) {
                            //Go to Login Activity
                            //logIn(beanSocialRegister.email, "");
                            if (WebInterface.isOnline(Login.this)) {
                                try {
                                    new LoginProcess(Login.this,
                                            email.toString(),
                                            "",
                                            providers, handler).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // myLog.e(TAG, "signUp: " + e);
                                }
                            } else {
                                Utils.showAlert(Login.this, "Network", "Network not available.", "OK");
                            }
                        } else {
                            //Go to Register Activity
                            Intent iReg = new Intent(Login.this, Register.class);
                            startActivity(iReg);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }
                    } else if (msg.what == 4) { // Forgot Password
                        alertDialog.dismiss();
                        Toast.makeText(Login.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                    }
                } else if (msg.arg1 == 0) { // fail
                    if (Prefs.getValue(Login.this, Const.IS_USER_EXISTED, "").equals("false")) {
                        //Go to Register Activity
                        Intent iReg = new Intent(Login.this, Register.class);
                        startActivity(iReg);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } else {
                        Utils.showAlert(Login.this, "", msg.obj.toString(), "Ok");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateGCMToken() {
        if (WebInterface.isOnline(Login.this)) {
            try {
                //Log.e(TAG, "updateGCMToken: FirebaseInstanceId.getInstance().getToken() : " + FirebaseInstanceId.getInstance().getToken());
                new UpdateGCMtokenProcess(Login.this,
                        Prefs.getValue(Login.this, Const.USERID, ""),
                        FirebaseInstanceId.getInstance().getToken()
                        , handler).execute();
            } catch (Exception e) {
                e.printStackTrace();
                // myLog.e(TAG, "signUp: " + e);
            }
        } else {
            Utils.showAlert(Login.this, "Network", "Network not available.", "OK");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Make sure that the twLoginButton hears the result from any
        // Activity that it triggered.
        twLoginButton.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful
                GoogleSignInAccount account = result.getSignInAccount();
                getGooGleProfile(account);
            } else {

            }
        }
    }


    public void signOutFacebook() {
        LoginManager.getInstance().logOut();
    }
    // ----------FINISH FACEBOOK FUNCTION CODE --------

    /**
     * FOR GOOGLE LOGIN FUNCTION CODE
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOutGoogle() {
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    private void revokeAccess() {

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    /**
     * FOR TWITTER LOGIN FUNCTIONS
     */


    private void signOutTwitter() {
        Twitter.logOut();
    }
    // *********** TWITTER FUNCTION FINISHED*************

    /**
     * Facebook Function
     *
     * @param objUser
     */
    private void getFacebookProfile(JSONObject objUser) throws JSONException {
        if (objUser != null) {

            String imgUrl = "https://graph.facebook.com/" + objUser.getString("id") + "/picture?type=large";
            beanSocialRegister = new SocialRegisterBean();
            beanSocialRegister.id = objUser.getString("id");
            beanSocialRegister.displayName = objUser.getString("name");
            beanSocialRegister.firstName = objUser.getString("first_name");
            beanSocialRegister.lastName = objUser.getString("last_name");
            beanSocialRegister.email = objUser.getString("email");
            beanSocialRegister.photoUrl = imgUrl;
            beanSocialRegister.providers = "fb";
            providers = "fb";

            if (WebInterface.isOnline(Login.this)) {
                try {
                    new IsUserExistedProcess(Login.this,
                            beanSocialRegister.id,
                            beanSocialRegister.providers
                            , handler).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    // myLog.e(TAG, "signUp: " + e);
                }
            }

        } else {

        }
    }

    /**
     * GooGle Function
     *
     * @param user
     */
    private void getGooGleProfile(GoogleSignInAccount user) {
        if (user != null) {

            beanSocialRegister = new SocialRegisterBean();
            beanSocialRegister.id = user.getId();
            beanSocialRegister.displayName = user.getDisplayName();
            beanSocialRegister.firstName = user.getGivenName();
            beanSocialRegister.lastName = user.getFamilyName();
            beanSocialRegister.email = user.getEmail();
            beanSocialRegister.photoUrl = user.getPhotoUrl().toString();
            beanSocialRegister.providers = "gmail";
            providers = "gmail";

            if (WebInterface.isOnline(Login.this)) {
                try {
                    new IsUserExistedProcess(Login.this,
                            beanSocialRegister.id,
                            beanSocialRegister.providers
                            , handler).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    // myLog.e(TAG, "signUp: " + e);
                }
            }

        } else {
        }
    }

    /**
     * Twitter Function
     *
     * @param usertw
     */
    private void getTwitterProfile(User usertw) {
        if (usertw != null) {

            beanSocialRegister = new SocialRegisterBean();
            beanSocialRegister.id = usertw.getId() + "";
            beanSocialRegister.displayName = usertw.name;
            beanSocialRegister.firstName = "";
            beanSocialRegister.lastName = "";
            beanSocialRegister.email = usertw.email;
            beanSocialRegister.photoUrl = usertw.profileImageUrl.toString();
            beanSocialRegister.providers = "tw";
            providers = "tw";

            if (WebInterface.isOnline(Login.this)) {
                try {
                    new IsUserExistedProcess(Login.this,
                            beanSocialRegister.id,
                            beanSocialRegister.providers
                            , handler).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    // myLog.e(TAG, "signUp: " + e);
                }
            }

        } else {
        }
    }

    Dialog alertDialog;

    private void forgotPasswordDialog() {

        alertDialog = new Dialog(this);
        View alertLayout = null;
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertLayout = getLayoutInflater().inflate(R.layout.forgot_password_dialog, null);
        alertDialog.setContentView(alertLayout);

        TextView txtbtn_send = (TextView) alertLayout.findViewById(R.id.txtbtn_forgot_send);
        TextView txtbtn_cancel = (TextView) alertLayout.findViewById(R.id.txtbtn_forgot_cancle);
        final EditText edt_forget_email = (EditText) alertLayout.findViewById(R.id.edt_forgot_email);
        txtbtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        txtbtn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_forget_email.getText().toString().equals("")) {

                    if (WebInterface.isOnline(Login.this)) {
                        try {
                            new ForgotPasswordProcess(Login.this,
                                    edt_forget_email.getText().toString().trim(),
                                    handler).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utils.showAlert(Login.this, "Network", "Network not available.", "OK");
                    }
                } else {
                    edt_forget_email.setError("Enter Email Id!");
                }
            }


        });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {

        if (isBackPressInternet == true) {
            rl_login.setVisibility(View.VISIBLE);
            img_noInternetError.setVisibility(View.GONE);
            isBackPressInternet = false;
        } else {
            super.onBackPressed();
        }
    }
}