package com.example.randompostfromreddit.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.randompostfromreddit.R;
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
    private static String CLIENT_ID = MainActivity.getContextOfApplication().getString(R.string.client_id);
    private static String REDIRECT_URI = MainActivity.getContextOfApplication().getString(R.string.redirect_uri);
    private static final String ACCESS_TOKEN_URL = MainActivity.getContextOfApplication().getString(R.string.access_token_url);
    private static String DEVICE_ID = UUID.randomUUID().toString();

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Context context = MainActivity.getContextOfApplication();
        final SharedPreferences pref = MainActivity.getContextOfApplication().getSharedPreferences(context.getString(R.string.app_pref), Context.MODE_PRIVATE);
        String code = pref.getString(context.getString(R.string.code),"");
        String newAccessToken = "";

        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);
        Request request = new Request.Builder()
                .addHeader(context.getString(R.string.user_agent), context.getString(R.string.app_name))
                .addHeader(context.getString(R.string.authorization), context.getString(R.string.basic) + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse(context.getString(R.string.url_encoded)),
                        context.getString(R.string.rq_body_1) + code + context.getString(R.string.rq_body_2) + DEVICE_ID +
                                context.getString(R.string.rq_body_3) + REDIRECT_URI))
                .build();
        OkHttpClient client = new OkHttpClient();
        Response token_response = client.newCall(request).execute();
        String json = token_response.body().string();
        JSONObject data = null;
        try {
            data = new JSONObject(json);
            newAccessToken = data.optString(context.getString(R.string.access_token));
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(context.getString(R.string.token), newAccessToken);
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response.request().newBuilder()
                .header(context.getString(R.string.authorization), context.getString(R.string.bearer) + newAccessToken)
                .build();
    }
}
