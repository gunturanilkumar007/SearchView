package github.nisrulz.searchview;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    SearchView searchView;

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private ImageView imageView;
    private int height=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");
        imageView=(ImageView)findViewById(R.id.image_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //System.out.println(newText);
                return false;
            }
        });
        expand(imageView,10000,height);
        ((Button) findViewById(R.id.notify)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height+=100;
                ((Button) findViewById(R.id.notify)).setText(""+height);
                expand(imageView,5000,height);

            }
        });

    }
    public static void expand(final View v, int duration, int targetHeight) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }
    public void createNotification(String title, String message, Bitmap bitmap) {

      /*  mContext=MainActivity.this;
        *//**Creates an explicit intent for an Activity in your app**//*
        Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 *//* Request code *//*, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        *//*NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(message);
        style.setBigContentTitle(title);
        style.setSummaryText("hellooooo");*//*

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setSummaryText(title);
        style.setBigContentTitle("Big Content title - " + title);
        String lines[]=message.split("/.");
        for (String line : lines) {
            style.addLine(line);
        }

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setStyle(style)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 *//* Request Code *//*, mBuilder.build());*/





        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.view_expanded_notification);
        //expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        expandedView.setTextViewText(R.id.notification_message, message);
        expandedView.setTextViewText(R.id.title, title);
        expandedView.setImageViewBitmap(R.id.image_view,bitmap);
        expandedView.setViewVisibility(R.id.image_view,1);
        // adding action to left button
        Intent leftIntent = new Intent(this, MainActivity.class);
        leftIntent.setAction("left");
        //expandedView.setOnClickPendingIntent(R.id.left_button, PendingIntent.getService(this, 0, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // adding action to right button
        Intent rightIntent = new Intent(this, MainActivity.class);
        rightIntent.setAction("right");
        //expandedView.setOnClickPendingIntent(R.id.right_button, PendingIntent.getService(this, 1, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, title);
        Intent intent=new Intent(this, MainActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.logo_color)
                .setContentTitle(title)
                .setContentText(message)
                // notification will be dismissed when tapped
                .setAutoCancel(true)
                // tapping notification will open MainActivity
                .setContentIntent(PendingIntent.getActivity(this, 0,intent, 0))
                // setting the custom collapsed and expanded views
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        // retrieves android.app.NotificationManager
        NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(0, builder.build());
    }
    public class MyAsync extends AsyncTask<Void, Void, Bitmap> {
        public String imageUrl;

        public MyAsync(String url) {
            super();
            this.imageUrl = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
