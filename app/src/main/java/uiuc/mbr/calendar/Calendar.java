package uiuc.mbr.calendar;

/**
 * Created by Richard Shen on 2/18/2016.
 */
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

