package com.example.mike.mp3player.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.TypedValue;

import com.example.mike.mp3player.R;
import com.example.mike.mp3player.client.activities.MediaPlayerActivity;
import com.example.mike.mp3player.commons.AndroidUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;

import static com.example.mike.mp3player.commons.Constants.MEDIA_SESSION;

public class MyNotificationManager {

    private final MediaPlaybackService service;
    private final NotificationManager notificationManager;
    private final MediaSessionCompat mediaSessionCompat;

    public static final int NOTIFICATION_ID = 512;
    private static final String TAG = "MY_NOTIFICATION_MANAGER";
    private static final String CHANNEL_ID = "com.example.mike.mp3player.service";
    private static final int REQUEST_CODE = 501;

    public MyNotificationManager(MediaPlaybackService service) {
        this.service = service;
        this.mediaSessionCompat = service.getMediaSession();
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll();
    }

    public void onDestroy() {
        if (AndroidUtils.isAndroidOreoOrHigher()) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
        }
        //Log.d(TAG, "onDestroy: ");
    }

    public synchronized NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public synchronized Notification getNotification(MediaMetadataCompat metadata,
                                                      @NonNull PlaybackStateCompat state) {
        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        MediaDescriptionCompat description = metadata.getDescription();

        if (AndroidUtils.isAndroidOreoOrHigher()) {
            return buildOreoNotification(mediaSessionCompat.getSessionToken(), isPlaying, description).build();
        }
        return buildNotification(mediaSessionCompat.getSessionToken(), isPlaying, description).build();
    }

    // Does nothing on versions of Android earlier than Oreo.
    @RequiresApi(Build.VERSION_CODES.O)
    private Notification.Builder buildOreoNotification(MediaSessionCompat.Token token,
                                                        boolean isPlaying,
                                                        MediaDescriptionCompat description) {
        createChannel();
        Context context = service.getApplicationContext();
        Notification.Action playPauseAction = null;

        if (isPlaying) {
            playPauseAction = makeOreoAction(context, android.R.drawable.ic_media_pause, service.getString(R.string.PAUSE), PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playPauseAction = makeOreoAction(context, android.R.drawable.ic_media_play, service.getString(R.string.PLAY), PlaybackStateCompat.ACTION_PLAY);
        }
        Notification.Action skipToNextAction = makeOreoAction(context, android.R.drawable.ic_media_next, service.getString(R.string.SKIP_TO_NEXT), PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        Notification.Action skipToPreviousAction = makeOreoAction(context, android.R.drawable.ic_media_previous, service.getString(R.string.SKIP_TO_PREVIOUS), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        Notification.Builder builder = new Notification.Builder(service.getApplicationContext(), CHANNEL_ID);

        builder
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentTitle(description.getTitle())
            .setContentText(description.getSubtitle())
            .setLargeIcon(getLargeIcon())
            .setTicker("to do")
            .setAutoCancel(!isPlaying)
            .setColorized(true)
            .setOngoing(isPlaying)
            .setColor(getPrimaryColor(context))
            .setSmallIcon(getSmallIcon(isPlaying))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service, PlaybackStateCompat.ACTION_STOP))
            .setContentIntent(createContentIntent(token))
            .addAction(skipToPreviousAction)
            .addAction(playPauseAction)
            .addAction(skipToNextAction);

        final Notification.MediaStyle style = new Notification.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2);

        if (token != null){
            style.setMediaSession((MediaSession.Token) token.getToken());
            builder.setStyle(style);
        }
         return builder;
    }

    private NotificationCompat.Builder buildNotification(MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat description) {
        Context context = service.getApplicationContext();
        // Create the (mandatory) notification channel when running on Android Oreo.
        NotificationCompat.Action playPauseAction = null;
        if (isPlaying) {
            playPauseAction = makeNoneOreoAction(android.R.drawable.ic_media_pause, service.getString(R.string.PAUSE), PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playPauseAction = makeNoneOreoAction(android.R.drawable.ic_media_play, service.getString(R.string.PLAY), PlaybackStateCompat.ACTION_PLAY);
        }

        NotificationCompat.Action skipToNextAction = makeNoneOreoAction(android.R.drawable.ic_media_next, service.getString(R.string.SKIP_TO_NEXT), PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        NotificationCompat.Action skipToPreviousAction = makeNoneOreoAction(android.R.drawable.ic_media_previous, service.getString(R.string.SKIP_TO_PREVIOUS), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        MediaStyle mediaStyle = new MediaStyle();
        NotificationCompat.Style style = mediaStyle.setMediaSession(token)
            .setShowCancelButton(true)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service,
                    PlaybackStateCompat.ACTION_STOP))
            .setShowActionsInCompactView(0,1,2);
        builder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(description.getTitle())
            .setContentText(description.getSubtitle())
            .setLargeIcon(getLargeIconBitmap())
            .setTicker("to do")
            .setAutoCancel(!isPlaying)
            .setOngoing(isPlaying)
            .setColor(getPrimaryColor(context))
            .setSmallIcon(getSmallIcon(isPlaying))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_STOP))
            .setContentIntent(createContentIntent(token))
            .addAction(skipToPreviousAction)
            .addAction(playPauseAction)
            .addAction(skipToNextAction)
            .setStyle(style);
        return builder;
    }

    // Does nothing on versions of Android earlier than Oreo.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "MediaSession";
            // The user-visible description of the channel.
            String description = "MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            /* Sets the notification light color for notifications posted to this
               channel, if the device supports this feature. WIll possibly implement in the future
               mChannel.setLightColor(Color.RED);
            */
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
            //Log.d(TAG, "createChannel: New channel created");
        } else {
            //Log.d(TAG, "createChannel: Existing channel reused");
        }
    }

    private PendingIntent createContentIntent(MediaSessionCompat.Token token) {
        Intent openUI = new Intent(service, MediaPlayerActivity.class);
        openUI.putExtra(MEDIA_SESSION, token);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                service, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private NotificationCompat.Action makeNoneOreoAction(int iconId, String title, long action) {
        PendingIntent intent = MediaButtonReceiver.buildMediaButtonPendingIntent(service, action);
        return new NotificationCompat.Action(iconId, title, intent);
    }

    private Notification.Action makeOreoAction(Context context, int iconId, String title, long action) {
        Icon icon = Icon.createWithResource(context, iconId);
        PendingIntent intent = MediaButtonReceiver.buildMediaButtonPendingIntent(service, action);
        return new Notification.Action.Builder( icon, title, intent).build();
    }

    private Bitmap getLargeIconBitmap() {
        return BitmapFactory.decodeResource(service.getApplicationContext().getResources(), R.drawable.ic_music_note);

    }

    private Icon getLargeIcon() {
       return Icon.createWithResource(service.getApplicationContext(), R.drawable.ic_music_note);
    }

    private int getSmallIcon(boolean isPlaying) {
        if (isPlaying) {
            return android.R.drawable.ic_media_play;
        }
        return android.R.drawable.ic_media_pause;
    }

    /**
     *
     * @param context
     * @return
     */
    private @ColorInt int getPrimaryColor(Context context) {
        Resources.Theme theme = context.getTheme();
        int[] attrs = {R.attr.themeColorPrimary};
        TypedArray typedArray = theme.obtainStyledAttributes(R.style.AppTheme_Orange, attrs);
        @ColorInt int toReturn = ContextCompat.getColor(context, typedArray.getResourceId(0, 0));
        typedArray.recycle();
        return toReturn;
    }
}
