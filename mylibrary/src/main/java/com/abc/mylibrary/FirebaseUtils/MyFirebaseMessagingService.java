package com.hexa.stylist.FirebaseUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.hexa.stylist.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hexa.stylist.MainActivity;
import com.hexa.stylist.bean.chatMessageBean;
import com.hexa.stylist.utils.DataBaseHelper;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static com.hexa.stylist.ChatEmojiActivity.ChatMessageList;
import static com.hexa.stylist.ChatEmojiActivity.updateArraylistOfMEssage;

/**
 * Created by ashish.patel on 9/19/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static boolean isChatFragmentAvailable = false;
    private static final String TAG = "MyFirebaseMsgService";
    String toId;
    String fromID;
    String userName;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional


        if (remoteMessage.getData().size() > 0) {
            //  Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
            }
        }

        //Calling method to generate notification
        //  sendNotification(remoteMessage.getNotification().getData());
    }

    private String UnicodeDecodedMessageBody;

    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        //  Log.e(TAG, "Notification JSON : " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");
            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            toId = data.getString("toId");
            fromID = data.getString("fromId");
            String notification_message = data.getString("notification_message");
            String timestamp = data.getString("timestamp");
            String type = data.getString("type");
            String image = data.getString("image");
            userName = data.getString("userName");

            if (type.equalsIgnoreCase("Chat")) {
                UnicodeDecodedMessageBody = StringEscapeUtils.unescapeJava(notification_message.replace("\\\\", "\\"));
                chatMessageBean bean = new chatMessageBean();
                bean.message = UnicodeDecodedMessageBody;
                bean.toId = fromID;
                bean.fromId = toId;
                bean.sentAt = timestamp;
                DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                dbHelper.updateChat(bean);

                if (isChatFragmentAvailable) {
                    try {
                        if (ChatMessageList != null) {
                            ChatMessageList.add(bean);
                            updateArraylistOfMEssage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //
                    }
                } else {
                    sendNotification(title, notification_message, type, image);
                }
            } else {
                sendNotification(title, notification_message, type, image);
            }


        } catch (JSONException e) {
        } catch (Exception e) {
        }
    }

    //This method is only generating push notification

    private void sendNotification(String title, String messageBody, String type, String image) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (type.equalsIgnoreCase("Chat")) {
            intent.setAction("CHATTING");
            intent.putExtra("image", image);
            intent.putExtra("toId", toId);
            intent.putExtra("fromID", fromID);
            intent.putExtra("userName", userName);
        } else if (type.equalsIgnoreCase("Review")) {
            intent.setAction("REVIEW");
            intent.putExtra("image", image);
            intent.putExtra("toId", toId);
            intent.putExtra("fromID", fromID);
            intent.putExtra("userName", userName);
        } else if (type.equalsIgnoreCase("appointment_received")) {
            intent.setAction("APPOINTMENT");
        } else if (type.equalsIgnoreCase("Invite")) {
            intent.setAction("INVITE");
        }


        Bitmap myBitmap = null;
        InputStream in;
        try {
            URL url = new URL(image);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.custom_notification);
        rv.setImageViewBitmap(R.id.remoteview_notification_icon, myBitmap);
//        rv.setImageViewResource(R.id.remoteview_notification_icon, R.mipmap.future_studio_launcher);

        UnicodeDecodedMessageBody = StringEscapeUtils.unescapeJava(messageBody.replace("\\\\", "\\"));

        rv.setTextViewText(R.id.remoteview_notification_headline, title);
        rv.setTextViewText(R.id.remoteview_notification_short_message, UnicodeDecodedMessageBody.toString());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(rv)
                .setContentTitle(title)
                .setContentText(UnicodeDecodedMessageBody.toString())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        final Notification notification = notificationBuilder.build();

        // set big content view for newer androids
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = rv;
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        notificationManager.notify(m, notification);
    }


}
