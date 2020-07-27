package com.example.randompostfromreddit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.randompostfromreddit.model.Child_Data;
import com.example.randompostfromreddit.ui.MainActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class RedditAppWidgetProvider extends AppWidgetProvider {
    SharedPreferences pref;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        pref = MainActivity.getContextOfApplication().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String post = pref.getString("current_post","");
        Gson gson = new Gson();
        Child_Data data = gson.fromJson(post, Child_Data.class);

        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("permalink",data.getPermalink());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reddit_appwidget);
            views.setOnClickPendingIntent(R.id.widget_thumbnail, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            String image_url = data.getThumbnail();
            if (image_url.isEmpty() || image_url.equals("self") || image_url.equals("default") || image_url.equals("spoiler")|| image_url.equals("nsfw")){
                views.setImageViewResource(R.id.widget_thumbnail,R.drawable.reddit_default);
            } else {
                Picasso.get().load(data.getThumbnail()).into(views, R.id.widget_thumbnail, new int[] {appWidgetId});
            }
            views.setTextViewText(R.id.widget_text,data.getTitle());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
