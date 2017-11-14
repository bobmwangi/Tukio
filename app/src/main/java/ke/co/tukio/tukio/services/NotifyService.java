package ke.co.tukio.tukio.services;

import android.R;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import ke.co.tukio.tukio.ReminderEventsActivity;

/**
 * This service is started when an Alarm has been raised
 * 
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 * 
 * @author paul.blundell
 */
public class NotifyService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	// Unique id to identify the notification.
	private static final int NOTIFICATION = 1234;
	// Name of an intent extra we can use to identify if this service was started to create a notification	
	public static final String INTENT_NOTIFY = "ke.co.tukio.tukio.INTENT_NOTIFY";
	// The system notification manager
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		// If this service was started by out AlarmTask intent then we want to show our notification
		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();
		
		// We don't care if this service is stopped as we have already delivered our notification
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification2() {
		// This is the 'title' of the notification
		CharSequence title = "Alarm!!";
		// This is the icon to use on the notification
		int icon = R.drawable.ic_dialog_alert;
		// This is the scrolling text of the notification
		CharSequence text = "Your notification time is upon us.";		
		// What time to show on the notification
		long time = System.currentTimeMillis();






//		PendingIntent pendingIntent = PendingIntent.getActivity(NotifyService.this, 1, intent, 0);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ReminderEventsActivity.class), 0);

		Notification.Builder builder = new Notification.Builder(NotifyService.this);

		builder.setAutoCancel(false);
		builder.setTicker("this is ticker text");
		builder.setContentTitle("WhatsApp Notification");
		builder.setContentText("You have a new message");
		builder.setSmallIcon(R.drawable.ic_dialog_alert);
		builder.setContentIntent(pendingIntent);
		builder.setOngoing(true);


//		builder.setSubText("This is subtext...");   //API level 16
		builder.setNumber(100);
//		builder.build();
		builder.getNotification();








//		Notification notification = new Notification(icon, text, time);
//		Notification.Builder notification = new Notification.Builder(NotifyService.this)
//				.setContentText(text)
//				.setSmallIcon(icon)
//				.setWhen(time)

//				.build();
//				.getNotification();

		// The PendingIntent to launch our activity if the user selects this notification
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ReminderEventsActivity.class), 0);

		// Set the info for the views that show in the notification panel.
//		builder.setLatestEventInfo(this, title, text, contentIntent);

		// Clear the notification when it is pressed
//		builder.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// Send the notification to the system.
//		mNM.notify(NOTIFICATION, notification);
		
		// Stop the service when we are finished
		stopSelf();
	}



    private void showNotification() {
        // This is the 'title' of the notification
        CharSequence title = "Alarm!!";
        // This is the icon to use on the notification
        int icon = R.drawable.ic_dialog_alert;
        // This is the scrolling text of the notification
        CharSequence text = "Your notification time is upon us.";
        // What time to show on the notification
        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ReminderEventsActivity.class), 0);

        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notification);

        // Stop the service when we are finished
        stopSelf();
    }
}