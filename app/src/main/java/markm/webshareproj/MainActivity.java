package markm.webshareproj;

import markm.webshareproj.MyListFragment.ListFragmentListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ListFragmentListener, WebLinkShare {
	private String url;
	private FragmentManager fragmentManager;

	private BroadcastReceiver mySMSReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String from = intent.getExtras().getString(CONTACT_PARAM);
			final String link = intent.getExtras().getString(LINK_PARAM);

			AlertDialog.Builder buider = new AlertDialog.Builder(MainActivity.this);
			buider.setTitle("New share has been received from " + from + " !").setMessage(link);
			buider.setNegativeButton(R.string.sms_received_dialog_no_thanks, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String ns = Context.NOTIFICATION_SERVICE;
					NotificationManager nf = (NotificationManager) getSystemService(ns);
					nf.cancel(NOTIFICATION_ID);
					dialog.dismiss();
				}
			});
			buider.setNeutralButton(R.string.sms_received_dialog_later, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			buider.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String ns = Context.NOTIFICATION_SERVICE;
					NotificationManager nf = (NotificationManager) getSystemService(ns);
					nf.cancel(NOTIFICATION_ID);
					setBrowserText(link);
					dialog.dismiss();
				}
			});
			buider.create().show();
		}
	};

	protected static final String HTTP = "http://";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// works on older API's (3.0 & above)
		fragmentManager = getSupportFragmentManager();
		// if portrait mode.
		if (findViewById(R.id.phoneLayout) != null) {
			MyListFragment list = new MyListFragment();
			fragmentManager.beginTransaction()
					.add(R.id.phoneLayout, list, "list").commit();
		} else {
			MyListFragment list = new MyListFragment();
			BrowserFragment browser = new BrowserFragment();
			fragmentManager.beginTransaction()
					.add(R.id.listFrame, list, "list")
					.add(R.id.browserFrame, browser, "browser").commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mySMSReceiver, new IntentFilter(SMS_RECEIVED_ACTION));
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mySMSReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		
		// hide the delete button if the phone is in portrait mode.
		if (findViewById(R.id.phoneLayout) != null) {
			menu.findItem(R.id.action_delete).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_add:
			if (fragmentManager.findFragmentByTag("browser") != null && fragmentManager.findFragmentByTag("browser").isVisible()) {
				EditText browserEditText = (EditText) findViewById(R.id.web_address_edit_text);
				url = browserEditText.getText().toString();
			}
			
			AddLinkDialog iMyDialogabot = new AddLinkDialog(MainActivity.this);
			iMyDialogabot.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
			iMyDialogabot.show();
			break;
		case R.id.action_delete:
			notifiyLinkDeleted();
			break;
		case R.id.action_share:
			if (fragmentManager.findFragmentByTag("browser") != null && fragmentManager.findFragmentByTag("browser").isVisible()) {
				EditText browserEditText = (EditText) findViewById(R.id.web_address_edit_text);
				url = browserEditText.getText().toString();

				Uri smsUri = Uri.parse("tel:123456");
				Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
				intent.putExtra("sms_body", CODE + url);
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
			} else {
				Uri smsUri = Uri.parse("tel:123456");
				Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
				intent.putExtra("sms_body", CODE + R.string.sms_send_body_replace_with_url);
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
			}
		}
		return true;
	}

	public void notifiyLinkDeleted() {
		MyListFragment listFragment = (MyListFragment) fragmentManager.findFragmentByTag("list");
		listFragment.onLinkDeleted();
	}

	@Override
	public void setBrowserText(String url) {
		if (findViewById(R.id.phoneLayout) != null) {
			BrowserFragment browser = new BrowserFragment();
			fragmentManager.beginTransaction()
			.replace(R.id.phoneLayout, browser, "browser")
			.addToBackStack(null)
			.commit();
			// מוודא שהשיכבה עם הדפדפן נמצאת לפני שמכניסים לתוכה URL חדש
			fragmentManager.executePendingTransactions();
		}
		WebView _webView = (WebView) findViewById(R.id.wv);
		EditText addressEditText = (EditText) findViewById(R.id.web_address_edit_text);
		addressEditText.setText(url);

		if (addressEditText.getText().toString().startsWith(HTTP)) {
			_webView.loadUrl(addressEditText.getText().toString());
		} else {
			_webView.loadUrl(HTTP + addressEditText.getText().toString());
		}
	}

	
	private class AddLinkDialog extends Dialog {
		EditText url_editText, origin_editText, category_editText, description_editText, title_editText;

		public AddLinkDialog(Context context) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.add_dialog);
			url_editText = (EditText) findViewById(R.id.addDialog_url);
			origin_editText = (EditText) findViewById(R.id.addDialog_origin);
			category_editText = (EditText) findViewById(R.id.addDialog_category);
			description_editText = (EditText) findViewById(R.id.addDialog_description);
			title_editText = (EditText) findViewById(R.id.addDialog_title);
			url_editText.setText(url);
			Button ok_button = (Button) findViewById(R.id.addDialog_ok_button);
			Button cancel_button = (Button) findViewById(R.id.addDialog_cancel_button);
			cancel_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			ok_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = title_editText.getText().toString();
					String category = category_editText.getText().toString();
					String origin = origin_editText.getText().toString();
					String description = description_editText.getText().toString();
					String url = url_editText.getText().toString();
					WebDatabaseHandler db = new WebDatabaseHandler(MainActivity.this);
				
					if (title != null && !title.equals("")) {
						db.addLink(title, description, url, category, origin);

						MyListFragment listFragment = (MyListFragment) fragmentManager.findFragmentByTag("list");
						listFragment.onLinkAdded();
						dismiss();
					} else {
						Toast.makeText(getContext(), R.string.add_dialog_must_enter_title, Toast.LENGTH_SHORT).show();
						title_editText.requestFocus();
					}
				}
			});

		}
	}

}
