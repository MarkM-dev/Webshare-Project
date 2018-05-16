package markm.webshareproj;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReciever extends BroadcastReceiver implements WebLinkShare {
	private Context context;
	private String from;
	private String smsbody;
	private String contactName;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Now we will extract the SMS message from the bundle
		this.context = context;
		Log.d("BubbleSMSReciever","***********************************************************");
		Log.d("BubbleSMSReciever", "Entered onReceive");
		Log.d("BubbleSMSReciever","***********************************************************");

		Bundle extras = intent.getExtras();
		SmsMessage[] msgs = null;
		if (extras == null) {
			Log.e("SMSReciever", "custom message: Bad SMS data");
		} else {
			smsbody = "";
			from = null;

			Object[] pdus = (Object[]) extras.get("pdus");

			msgs = new SmsMessage[pdus.length];

			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				from = msgs[i].getOriginatingAddress();
				String strMess = msgs[i].getMessageBody().toString();
				smsbody += strMess;
			}

			if (smsbody.startsWith(CODE)) {

				String link = smsbody.substring(CODE.length());
				getContactName(context);
				generateNotification(from, link);
				Intent _intent = new Intent(SMS_RECEIVED_ACTION);

				_intent.putExtra(LINK_PARAM, link);
				_intent.putExtra(CONTACT_PARAM, contactName);

				context.sendBroadcast(_intent);
				// / Overide SMS message in the SMS database- taking if the
				// initilized @@
			}
		}
	}

	@SuppressLint("NewApi")
	private void generateNotification(String strFrom, String link) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
									.setSmallIcon(R.drawable.icon)
									.setContentTitle("WebLinksShare")
									.setContentText(contactName + context.getString(R.string.notification_shared_a_link_with_you));

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// stackBuilder.addParentStack(DialerActivity.class);

		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(LINK_PARAM, link);

		stackBuilder.addNextIntent(intent);

		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_ONE_SHOT
						| android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

		mBuilder.setContentIntent(resultPendingIntent);
		Notification notification = mBuilder.build();
		// Clear the notification after been launched
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	public String getContactName(Context context) {

		Uri u = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(from));
		String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME };

		Cursor c = context.getContentResolver().query(u, projection, null, null, null);

		try {
			if (!c.moveToFirst())
				return contactName = from;

			int index = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			return contactName = c.getString(index);

		} finally {
			if (c != null)
				c.close();
		}
	}

}
