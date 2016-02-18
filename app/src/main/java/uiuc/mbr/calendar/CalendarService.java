package uiuc.mbr.calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Richard Shen on 2/18/2016.
 * API for accessing the Android calendar and events.
 */
public class CalendarService {
    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
    };

    // The indices for the projection array above.
    private static final int CALENDAR_ID_INDEX = 0;
    private static final int CALENDAR_DISPLAY_NAME_INDEX = 1;

    //Instances are occurrences of recurring events
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
    };

    private static final int EVENT_CALENDAR_ID_INDEX = 0;
    private static final int EVENT_PARENT_ID_INDEX = 1;
    private static final int EVENT_TITLE_INDEX = 2;
    private static final int EVENT_DESCRIPTION_INDEX = 3;
    private static final int EVENT_LOCATION_INDEX = 4;
    private static final int EVENT_BEGIN_INDEX = 5;
    private static final int EVENT_END_INDEX = 6;

    public static final long MILLISECONDS_IN_DAY = 86400000;

    Context context;

    public CalendarService(Context context) {
        this.context = context;
    }

    /**
     * Returns the first listed Google account on the device.
     */
    private Account getGoogleAccount() {
        AccountManager manager = AccountManager.get(context);
        Account[] list = manager.getAccountsByType("com.google");

        if (list.length > 0) {
            Log.d("AccountName", list[0].name);
            return list[0];
        }
        return null;
    }

    /**
     * Gets all calendars associated the current Google account on the device.
     * @return An ArrayList of Calendars, null if no account was found.
     */
    public ArrayList<Calendar> getCalendars() {
        // Run query
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND (" + CalendarContract.Calendars.VISIBLE + " = ?) AND (" +
                CalendarContract.Calendars.SYNC_EVENTS + " = ?))";
        Account account = getGoogleAccount();
        if(account == null) {
            return null;
        } else {
            if(account.name == null) {
                return null;
            }
        }
        String[] selectionArgs = new String[]{getGoogleAccount().name, "com.google", "1", "1"};
        ArrayList<Calendar> cals = new ArrayList<>();
        // Submit the query and get a Cursor object back.
        try {
            cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

            while (cur.moveToNext()) {
                long calID = 0;
                String displayName = null;

                // Get the field values
                calID = cur.getLong(CALENDAR_ID_INDEX);
                displayName = cur.getString(CALENDAR_DISPLAY_NAME_INDEX);

                cals.add(new Calendar(calID, displayName));
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return cals;
    }

    /**
     * @return All events in the next 24 hours.
     */
    public ArrayList<Event> getEventsNext24Hours() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        long now = c.getTimeInMillis();
        return getEvents(now, now+MILLISECONDS_IN_DAY);
    }

    /**
     * Finds all events that start between startTime and endTime.
     * @param startTime Start time in UTC millis.
     * @param endTime End time in UTC millis.
     * @return An ArrayList of all Events starting between startTime and endTime.
     */
    public ArrayList<Event> getEvents(long startTime, long endTime) {
        ContentResolver cr = context.getContentResolver();

        String selection = "(( " + CalendarContract.Instances.ALL_DAY + " = ?))";
        String[] selectionArgs = new String[]{"0"};
        Uri uri = CalendarContract.Instances.CONTENT_URI;
        Cursor cur = cr.query(Uri.parse(uri.toString()+"/"+startTime+"/"+endTime),
                EVENT_PROJECTION,
                selection,
                selectionArgs,
                null);

        ArrayList<Event> eventList = new ArrayList<>();
        while (cur.moveToNext()) {
            long calID = 0;
            long parentEventId = 0;
            String title = null;
            String description = null;
            String location = null;
            long begin = 0;
            long end = 0;

            // Get the field values
            calID = cur.getLong(EVENT_CALENDAR_ID_INDEX);
            parentEventId = cur.getLong(EVENT_PARENT_ID_INDEX);
            title = cur.getString(EVENT_TITLE_INDEX);
            description = cur.getString(EVENT_DESCRIPTION_INDEX);
            location = cur.getString(EVENT_LOCATION_INDEX);
            begin = cur.getLong(EVENT_BEGIN_INDEX);
            end = cur.getLong(EVENT_END_INDEX);

            eventList.add(new Event(calID, parentEventId, title, description, location, new Date(begin), new Date(end)));
        }

        cur.close();

        return eventList;
    }
}
