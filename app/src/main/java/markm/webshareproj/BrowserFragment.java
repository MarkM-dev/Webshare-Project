package markm.webshareproj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@SuppressLint("NewApi")
public class BrowserFragment extends Fragment implements WebLinkShare {
	final static String ARG_POSITION = "position";
	final static String ARG_LINK = "link";
	protected static final String HTTP = "http://";
	int mCurrentPosition = -1;
	static String mCurrentLink = null;
	private View view;
	private Intent intent;
	private ImageButton _goButton = null;
	private boolean _isHideBars = false;
	private static WebView _webView;
	private String newLink;
	private FrameLayout mCustomViewContainer;
	private FrameLayout mContentView;
	private String url;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

	private FrameLayout.LayoutParams GRAVITY_BOTTOM_RIGHT = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT);

	private View mCustomView;
	private ValueCallback<Uri> mUploadMessage = null;

	private EditText addressEditText;

	final static String HOME_PAGE = "http://www.google.com";

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			addressEditText = (EditText) view.findViewById(R.id.web_address_edit_text);
			addressEditText.setText(url);
			return true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
			mCurrentLink = savedInstanceState.getString(ARG_LINK);
		}

		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.browser, container, false);

		// //////////////
		intent = getActivity().getIntent();
		newLink = intent.getStringExtra("newLink");

		_webView = (WebView) view.findViewById(R.id.wv);

		WebSettings webSettings = _webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		_webView.setInitialScale(1);
		_webView.getSettings().setBuiltInZoomControls(true);
		_webView.getSettings().setUseWideViewPort(true);
		_webView.getSettings().setDisplayZoomControls(false);

		addressEditText = (EditText) view.findViewById(R.id.web_address_edit_text);
		_goButton = (ImageButton) view.findViewById(R.id.web_go_button);
		_goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addressEditText.getText().toString().startsWith(HTTP)) {
					_webView.loadUrl(addressEditText.getText().toString());
				} else {
					_webView.loadUrl(HTTP + addressEditText.getText().toString());
				}
			}
		});

		addressEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (addressEditText.getText().toString().startsWith(HTTP)) {
					_webView.loadUrl(addressEditText.getText().toString());
				} else {
					_webView.loadUrl(HTTP + addressEditText.getText().toString());
				}
				return true;
			}
		});

		WebViewClient webClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageStarted(WebView view, final String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				addressEditText.post(new Runnable() {
					@Override
					public void run() {
						addressEditText.setText(url);
					}
				});
			}
		};

		_webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				boolean isExternalStorageConnected = false;// getExternalStorageController().isExtStorage();

				if (!isExternalStorageConnected) {
					// makeInfoNotification(MessagesManager.getTitle($insert_usb_to_download,
					// getResources().getString(R.string.insert_usb_to_download)));
				} else {
					// Intent intent = new Intent(WebActivity.this,
					// DownloadChooseActivity.class);
					// intent.putExtra("url", url);
					// startActivityForResult(intent, DOWNLOAD_RESULTCODE);
				}
			}
		});

		_webView.setWebViewClient(webClient);
		// _webView.setWebViewClient(new WebViewClient());
		_webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onShowCustomView(View view,
					final WebChromeClient.CustomViewCallback callback) {
				// if a view already exists then immediately terminate the new
				// one
				if (mCustomView != null) {
					callback.onCustomViewHidden();
					return;
				}

				// Add the custom view to its container.
				final ImageButton fs = new ImageButton(getActivity());
				fs.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						fs.setVisibility(View.GONE);
						onHideCustomView();
					}
				});
				GRAVITY_BOTTOM_RIGHT.rightMargin = 16;
				GRAVITY_BOTTOM_RIGHT.bottomMargin = 16;

				mCustomViewContainer.addView(view, COVER_SCREEN_GRAVITY_CENTER);
				mCustomViewContainer.addView(fs, GRAVITY_BOTTOM_RIGHT);
				mCustomView = view;
				mCustomViewCallback = callback;

				// hide main browser view
				mContentView.setVisibility(View.GONE);

				// Finally show the custom view container.
				mCustomViewContainer.setVisibility(View.VISIBLE);
				mCustomViewContainer.bringToFront();
			}

			@Override
			public void onHideCustomView() {
				if (mCustomView == null)
					return;

				// Hide the custom view.
				mCustomView.setVisibility(View.GONE);
				// Remove the custom view from its container.
				mCustomViewContainer.removeView(mCustomView);
				mCustomView = null;
				// mCustomViewContainer.setVisibility(View.GONE);
				mCustomViewCallback.onCustomViewHidden();

				// Show the content view.
				mContentView.setVisibility(View.VISIBLE);
				mContentView.bringToFront();
			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				// mUploadMessage = uploadMsg;
				// final boolean isExternalStorageConnected =
				// getExternalStorageController().isExtStorage();
				//
				// runOnUiThread(new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {
				// if(!isExternalStorageConnected)
				// {
				// //makeInfoNotification(MessagesManager.getTitle($insert_usb_to_upload,
				// getResources().getString(R.string.insert_usb_to_upload)));
				// mUploadMessage.onReceiveValue(null);
				// return;
				// }
				//
				// // Intent intent = new Intent(WebActivity.this,
				// UploadChooserActivity.class);
				// // startActivityForResult(intent, UPLOAD_RESULTCODE);
				// }
				// });

			}

			// For Android < 3.0
			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

			// For Android > 4.1
			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				openFileChooser(uploadMsg, acceptType);
			}

		});

		final ImageButton homeImageView = (ImageButton) view.findViewById(R.id.web_home_image_view);
		homeImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_webView.loadUrl(HOME_PAGE);
			}
		});

		final ImageButton webBackImageView = (ImageButton) view.findViewById(R.id.web_back_image_view);
		webBackImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_webView.canGoBack()) {
					_webView.goBack();
				}
			}
		});
		
		final ImageButton webForwardImageView = (ImageButton) view.findViewById(R.id.web_forward_image_view);
		webForwardImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_webView.canGoForward()) {
					_webView.goForward();
				}
			}
		});

		if (getActivity().getIntent().getExtras() != null) {
			String _url = getActivity().getIntent().getExtras()
					.getString(LINK_PARAM, null);
			if (_url != null) {
				url = _url;
			}
		}
		_webView.loadUrl(url);
		return view;
	}

	protected String getCurrentLink() {
		return addressEditText.getText().toString();
	}

	@Override
	public void onDestroyView() {
		_webView.setVisibility(View.GONE);
		_webView.post(new Runnable() {
			@Override
			public void run() {
				_webView.clearCache(true);
				_webView.destroy();
			}
		});
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			updateArticleView(args.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			// Set article based on saved instance state defined during
			// onCreateView
			updateArticleView(mCurrentPosition);
		}
	}

	public void updateArticleView(int position) {
		WebView article = (WebView) getActivity().findViewById(R.id.browser);
		// article.setText(Ipsum.Articles[position]);
		mCurrentPosition = position;
	}

	public static void updateWebView(final String link) {
		_webView.post(new Runnable() {
			@Override
			public void run() {
				_webView.loadUrl(link);
			}
		});
		mCurrentLink = link;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, mCurrentPosition);
	}

	public void changlink(String url2) {
		// TODO Auto-generated method stub
		_webView.loadUrl(url2);
	}
}