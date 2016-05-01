package uiuc.mbr.calendar;

/**Data class that describes an Android calendar.*/
public class Calendar {
	private long id;
	private String name;

	public Calendar(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "Calendar{ID: "+id+", NAME: "+name+"}";
	}

}

