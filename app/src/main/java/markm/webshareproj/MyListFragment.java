package markm.webshareproj;

import static markm.webshareproj.WebDatabaseHelper.COLUMN_CATEGORY;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_DESCRIPTION;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_ID;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_LINK;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_ORIGIN;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_TITLE;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MyListFragment extends ListFragment implements LinkAddedListener, LinkDeletedListener, WebLinkShare {

	public static final String INDICATOR_NAME = "hgf";

	private WebDatabaseHandler handler = null;
	private long selectedLinkId = -1;

	private MyCursorAdapter myAdapter;

	private Cursor cursor;

	private ListFragmentListener listFragmentListener;

	public interface ListFragmentListener {
		void setBrowserText(String text);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listFragmentListener = (ListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("you must implement ListFragmentListener in your Activity");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handler = new WebDatabaseHandler(getActivity());
		cursor = handler.getAllLink();
		myAdapter = new MyCursorAdapter(getActivity(), cursor);
		setListAdapter(myAdapter);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					ViewHolder vHolder = (ViewHolder)view.getTag();
					String title = vHolder.getMyTextViewLink().getText().toString();
					String description = vHolder.getDescription();
					String url = vHolder.getUrl();
					String category = vHolder.getCategory();
					String origin = vHolder.getOrigin();
					long linkid = vHolder.getLinkId();
					OptDialog iMyDialogabot = new OptDialog(getActivity(), title, description, url, category, origin, linkid);
					iMyDialogabot.show();
				return true;
			}
		});
	}
	
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);

		ViewHolder holder = (ViewHolder) view.getTag();
		selectedLinkId = holder.getLinkId();
		String strurl = holder.getUrl();
		listFragmentListener.setBrowserText(strurl);

		myAdapter.notifyDataSetInvalidated();
	}
	
	@Override
	public void onLinkAdded() {
		cursor = handler.getAllLink();
		myAdapter.swapCursor(cursor);
		Toast.makeText(getActivity(), R.string.toast_link_added, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLinkDeleted() {
		if (selectedLinkId != -1) {
			handler.removeLink(selectedLinkId);
			selectedLinkId = -1;
			cursor = handler.getAllLink();
			myAdapter.swapCursor(cursor);
		} else {
			Toast.makeText(getActivity(), R.string.toast_no_item_is_selected, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);

	}

	private class MyCursorAdapter extends CursorAdapter {

		private LayoutInflater myInflator = null;

		public MyCursorAdapter(Context context, Cursor c) {
			super(context, c, true);
			myInflator = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View myView, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) myView.getTag();

			// get the int of the wanted column.
			int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
			int columnIndexLink = cursor.getColumnIndex(COLUMN_LINK);
			int columnIndexTitle = cursor.getColumnIndex(COLUMN_TITLE);
			int columnIndexDescription = cursor.getColumnIndex(COLUMN_DESCRIPTION);
			int columnIndexCategory = cursor.getColumnIndex(COLUMN_CATEGORY);
			int columnIndexOrigin = cursor.getColumnIndex(COLUMN_ORIGIN);

			// get the string from the selected column.
			long webId = cursor.getInt(columnIndexId);
			String link = cursor.getString(columnIndexLink);
			String title = cursor.getString(columnIndexTitle);
			String description = cursor.getString(columnIndexDescription);
			String category = cursor.getString(columnIndexCategory);
			String origin = cursor.getString(columnIndexOrigin);
			
			holder.setLinkId(webId);
			holder.setUrl(link);
			holder.getMyTextViewLink().setText(title);
			holder.setDescription(description);
			holder.setCategory(category);
			holder.setOrigin(origin);
			
			
			if (selectedLinkId == webId) {
				myView.setBackgroundColor(Color.DKGRAY);
			} else {
				myView.setBackgroundColor(Color.BLACK);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			View myView = myInflator.inflate(R.layout.my_list_item, null);

			ViewHolder holder = new ViewHolder();
			TextView myTextView = (TextView) myView.findViewById(R.id.myTextView);
			holder.setMyTextViewLink(myTextView);
			myView.setTag(holder);

			return myView;
		}

	}
	
	private class OptDialog extends Dialog {
		TextView title_textView, description_textView, url_textView, category_textView, origin_textView;
		
		public OptDialog(Context context, String title, String description, String url, String category, String origin, long linkid) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.info_dialog);
			
			final String _url = url;
			final long _linkid = linkid;
			title_textView = (TextView) findViewById(R.id.infoDialog_title_textView);
			description_textView = (TextView) findViewById(R.id.infoDialog_description_textView);
			url_textView = (TextView) findViewById(R.id.infoDialog_url_textView);
			category_textView = (TextView) findViewById(R.id.infoDialog_category_textView);
			origin_textView = (TextView) findViewById(R.id.infoDialog_origin_textView);
			
			title_textView.setText(title);
			description_textView.setText(description);
			url_textView.setText(url);
			category_textView.setText(category);
			origin_textView.setText(origin);
			
			Button ok_button = (Button) findViewById(R.id.infoDialog_ok_button);
			Button delete_button = (Button) findViewById(R.id.infoDialog_delete_button);
			Button share_button = (Button) findViewById(R.id.infoDialog_share_button);
			delete_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebDatabaseHandler db = new WebDatabaseHandler(getActivity());
					db.removeLink(_linkid);
					cursor = handler.getAllLink();
					myAdapter.swapCursor(cursor);
					Toast.makeText(getActivity(), R.string.toast_deleted, Toast.LENGTH_LONG).show();
					dismiss();
				}
			});
			ok_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();				
				}
			});
			share_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = _url;
					
					Uri smsUri = Uri.parse("tel:123456");
					Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
					intent.putExtra("sms_body", CODE + url);
					intent.setType("vnd.android-dir/mms-sms");
					startActivity(intent);
				}
			});

		}
	}
}
