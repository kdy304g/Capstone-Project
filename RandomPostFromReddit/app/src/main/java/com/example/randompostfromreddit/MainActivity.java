package com.example.randompostfromreddit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static String CLIENT_ID = "8QWhUSXGUjcwpg";
    private static String CLIENT_SECRET ="";
    private static String REDIRECT_URI="https://github.com/kdy304g/Capstone-Project";
    private static String GRANT_TYPE="https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2="authorization_code";
    private static String TOKEN_URL ="access_token";
    private static String OAUTH_URL ="https://www.reddit.com/api/v1/authorize";
    private static String OAUTH_SCOPE="read";
    private static String DURATION = "permanent";

    WebView web;
    Button auth;
    SharedPreferences pref;
    TextView Access;
    Dialog auth_dialog;
    String DEVICE_ID = UUID.randomUUID().toString();
    String authCode;
    boolean authComplete = false;

    Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        Access =(TextView)findViewById(R.id.Access);
        auth = (Button)findViewById(R.id.auth);
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                String url = OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=TEST&redirect_uri=" + REDIRECT_URI + "&duration=temporary" + "&scope=" + OAUTH_SCOPE;
                web.loadUrl(url);
                Toast.makeText(getApplicationContext(), "" + url, Toast.LENGTH_LONG).show();

                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("Access-Control-Allow-Origin","https://www.reddit.com/login/?dest=https%3A%2F%2Fwww.reddit.com%2Fapi%2Fv1%2Fauthorize%3Fclient_id%3D8QWhUSXGUjcwpg%26response_type%3Dcode%26state%3DTEST%26redirect_uri%3Dhttps%253A%252F%252Fgithub.com%252Fkdy304g%252FCapstone-Project%26duration%3Dtemporary%26scope%3Dread");

                        view.loadUrl(url, map);
                        return false;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.i("","page started");

                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.i("","page finished");

                        if (url.contains("?code=") || url.contains("&code=")) {
                            Log.i("","url contains ?code=");
                            Uri uri = Uri.parse(url);
                            authCode = uri.getQueryParameter("code");
                            Log.i("", "CODE : " + authCode);
                            authComplete = true;
                            resultIntent.putExtra("code", authCode);
                            MainActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("Code", authCode);
                            edit.commit();
                            auth_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authorization Code is: " + pref.getString("Code", ""), Toast.LENGTH_SHORT).show();

//                            try {
//                                new RedditRestClient(getApplicationContext()).getToken(TOKEN_URL, GRANT_TYPE2, DEVICE_ID);
//                                Toast.makeText(getApplicationContext(), "Auccess Token: " + pref.getString("token", ""), Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

                        } else if (url.contains("error=access_denied")) {
                            Log.i("", "ACCESS_DENIED_HERE");
                            resultIntent.putExtra("code", authCode);
                            authComplete = true;
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                            auth_dialog.dismiss();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("Authorize");
                auth_dialog.setCancelable(true);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}