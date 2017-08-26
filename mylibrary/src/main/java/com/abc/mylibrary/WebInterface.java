package com.abc.mylibrary;

import android.content.Context;
import android.net.ConnectivityManager;

import com.abc.iccons.Beans.ApiResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


@SuppressWarnings("ALL")
public class WebInterface {
    static String TAG = "WebInterface";

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm == null || !cm.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return true;
        }
    }

    public static ApiResponse doPostString(String url, String param) {

        ApiResponse apiResponse = null;
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(param, HTTP.UTF_8));
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        apiResponse = new ApiResponse();
        apiResponse.status_code = httpResponse.getStatusLine().getStatusCode();

        if (apiResponse.status_code == HttpStatus.SC_OK) {
            httpEntity = httpResponse.getEntity();
            try {
                apiResponse.response = EntityUtils.toString(httpEntity);
                // myLog.d("mytag", apiResponse.response +
                // "you get from serverresponse......!! " +httpEntity);
                //  android.util.Log.e(TAG, "doPostString: " + apiResponse.response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpEntity = null;
        httpResponse = null;
        httpPost = null;
        httpClient = null;

        return apiResponse;
    }

    public static ApiResponse doPost(String url, List<BasicNameValuePair> param) {

        ApiResponse apiResponse = null;
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        apiResponse = new ApiResponse();
        apiResponse.status_code = httpResponse.getStatusLine().getStatusCode();

        if (apiResponse.status_code == HttpStatus.SC_OK) {
            httpEntity = httpResponse.getEntity();
            try {
                apiResponse.response = EntityUtils.toString(httpEntity);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpEntity = null;
        httpResponse = null;
        httpPost = null;
        httpClient = null;

        return apiResponse;
    }

    public static ApiResponse doPost(String url) throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {

        ApiResponse apiResponse = null;
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;

        HttpClient httpClient = new DefaultHttpClient();

        /*********************** dont use if not necessory**********************/
        /*****it's for avoiding "SSLPeerUnverifiedException" exception only ******/
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                new SecureRandom());
        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = httpClient.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));
        DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                httpClient.getParams());
        /**************************************************************************/

        HttpPost httpPost = new HttpPost(url);

        try {
            // httpPost.setEntity(new UrlEncodedFormEntity(param));
            httpResponse = sslClient.execute(httpPost);
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        apiResponse = new ApiResponse();
        apiResponse.status_code = httpResponse.getStatusLine().getStatusCode();

        if (apiResponse.status_code == HttpStatus.SC_OK) {
            httpEntity = httpResponse.getEntity();
            try {
                apiResponse.response = EntityUtils.toString(httpEntity);
                //  android.util.Log.e(TAG, "doPost: " + apiResponse.response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpEntity = null;
        httpResponse = null;
        httpPost = null;
        httpClient = null;

        return apiResponse;
    }

    public static ApiResponse doWS(String url, Map params) {
        InputStream inputStream = null;
        ApiResponse apiResponse = null;
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            /*********************** dont use if not necessory**********************/
            /*****it's for avoiding "SSLPeerUnverifiedException" exception only ******/
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());
            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpclient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    httpclient.getParams());
            /***************************************************************************/

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            //    Log.i(TAG, "doWS: ");
            //convert parameters into JSON object
            JSONObject jObj = new JSONObject(params);

            //  Log.w(TAG, "doWS: JSON STRING :" + jObj.toString());
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(jObj.toString());

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the
            // content
            //  httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = sslClient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            apiResponse = new ApiResponse();
            if (inputStream != null)
                apiResponse.response = convertInputStreamToString(inputStream);
            else
                apiResponse.response = "Did not work!";

        } catch (Exception e) {
            e.printStackTrace();

        }

        // 11. return result
        return apiResponse;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /*********************** dont use if not necessory**********************/
    /*****it's for avoiding "SSLPeerUnverifiedException" exception only ******/

    public static class CustomSSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public CustomSSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new CustomX509TrustManager();

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        public CustomSSLSocketFactory(SSLContext context)
                throws KeyManagementException, NoSuchAlgorithmException,
                KeyStoreException, UnrecoverableKeyException {
            super(null);
            sslContext = context;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port,
                    autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    public static class CustomX509TrustManager implements X509TrustManager {


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs,
                                       String authType) throws CertificateException {

            // Here you can verify the servers certificate. (e.g. against one which is stored on mobile device)

            // InputStream inStream = null;
            // try {
            // inStream = MeaApplication.loadCertAsInputStream();
            // CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // X509Certificate ca = (X509Certificate)
            // cf.generateCertificate(inStream);
            // inStream.close();
            //
            // for (X509Certificate cert : certs) {
            // // Verifing by public key
            // cert.verify(ca.getPublicKey());
            // }
            // } catch (Exception e) {
            // throw new IllegalArgumentException("Untrusted Certificate!");
            // } finally {
            // try {
            // inStream.close();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

/****************************************************************************/
}
