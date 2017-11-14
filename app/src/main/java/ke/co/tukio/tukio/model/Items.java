package ke.co.tukio.tukio.model;

public class Items {
	private String title, thumbnailUrl, description, time, views, id;


	public Items() {
	}

	public Items(String title, String thumbnailUrl, String description, String time, String views, String id)
	{
		this.title = title;
		this.thumbnailUrl = thumbnailUrl;
		this.description = description;
		this.time = time;
		this.views = views;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public void setViews(String views) {
		this.views = views;
	}
	public String getViews() {
		return views;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

}
