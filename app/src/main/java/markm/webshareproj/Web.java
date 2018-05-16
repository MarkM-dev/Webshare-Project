package markm.webshareproj;

// this class contains all the database information.
public class Web {

	private long id;
	private String title;
	private String description;
	private String link;
	private String category;
	private String origin;

	public Web (long id, String title, String description, String link, String category, String origin) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.link = link;
		this.category = category;
		this.origin = origin;
	}

	public Web() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

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

}
