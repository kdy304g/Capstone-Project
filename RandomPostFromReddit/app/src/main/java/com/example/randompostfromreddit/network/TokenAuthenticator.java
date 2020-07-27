package com.example.randompostfromreddit.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.randompostfromreddit.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private static String CLIENT_ID = "8QWhUSXGUjcwpg";
    private static String REDIRECT_URI = "http://www.example.com/my_redirect";
    private static final String ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
    private static String DEVICE_ID = UUID.randomUUID().toString();

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        final SharedPreferences pref = MainActivity.getContextOfApplication().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String code = pref.getString("code","");
        String newAccessToken = "";

        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Random Post From Reddit")
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=https://oauth.reddit.com/grants/installed_client&code=" + code + "&device_id=" + DEVICE_ID +
                                "&redirect_uri=" + REDIRECT_URI))
                .build();
        OkHttpClient client = new OkHttpClient();
        Response token_response = client.newCall(request).execute();
        String json = token_response.body().string();
        JSONObject data = null;
        try {
            data = new JSONObject(json);
            newAccessToken = data.optString("access_token");
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("token", newAccessToken);
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response.request().newBuilder()
                .header("Authorization", "bearer " + newAccessToken)
                .build();
    }
}
