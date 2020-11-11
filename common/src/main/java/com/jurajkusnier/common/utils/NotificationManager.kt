package com.jurajkusnier.common.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.jurajkusnier.common.R
import com.jurajkusnier.common.Sound

class MyNotificationManager(private val context: Context) {

    private val channelId = createChannelId()

    fun showNotification(mediaSession: MediaSessionCompat, sound: Sound, isPlaying: Boolean) {
        val builder = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(sound.title)
            setContentText(sound.subtitle)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, sound.icon))
            setNotificationSilent()
            setContentIntent(mediaSession.controller.sessionActivity)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.ic_notification)
            color = ContextCompat.getColor(context, R.color.design_default_color_background)
            addAction(actionPrevious)
            addAction(if (isPlaying) actionPause else actionPlay)
            addAction(actionNext)
            setStyle(getMediaStyle(mediaSession))
        }
        (context as Service).startForeground(NOW_PLAYING_NOTIFICATION_ID, builder.build())
    }

    private fun getMediaStyle(mediaSession: MediaSessionCompat) =
        androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setCancelButtonIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

    private val actionPlay = NotificationCompat.Action(
        R.drawable.ic_baseline_play_arrow_24,
        context.getString(R.string.play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PLAY
        )
    )

    private val actionPause = NotificationCompat.Action(
        R.drawable.ic_baseline_pause_24,
        context.getString(R.string.pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )

    private val actionNext = NotificationCompat.Action(
        R.drawable.ic_baseline_skip_next_24,
        context.getString(R.string.next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )

    private val actionPrevious = NotificationCompat.Action(
        R.drawable.ic_baseline_skip_previous_24,
        context.getString(R.string.previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    private fun createChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, context.getString(R.string.media_player))
        } else {
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_LOW
        )
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    companion object {
        private const val NOW_PLAYING_NOTIFICATION_ID = 0x1
        private const val CHANNEL_ID = "media_player_channel_id"
    }
}