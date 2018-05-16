package markm.webshareproj;

import android.widget.TextView;

public class ViewHolder {
	private long LinkId;
	private TextView myTextViewLink;
	private String url;
	private String description;
	private String category;
	private String origin;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getLinkId() {
		return LinkId;
	}

	public void setLinkId(long linkId) {
		LinkId = linkId;
	}

	public TextView getMyTextViewLink() {
		return myTextViewLink;
	}

	public void setMyTextViewLink(TextView myTextViewLink) {
		this.myTextViewLink = myTextViewLink;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	
}
