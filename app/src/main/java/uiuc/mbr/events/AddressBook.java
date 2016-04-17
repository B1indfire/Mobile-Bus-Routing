package uiuc.mbr.events;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**Manages a database of UserLocation objects.
 * Make sure to call initIfNecessary() early in the app's lifecyle.*/
public class AddressBook
{
	private static final String TABLE = "Locations";


	private static File dbFile(Context context)
	{
		File dir = context.getExternalFilesDir(null);
		return new File(dir, "address_book.sqlite");
	}

	private static SQLiteDatabase db(Context context)
	{
		return SQLiteDatabase.openDatabase(dbFile(context).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
	}

	/**Puts all variables in a UserLocation object into a ContentValues object, optionally including the id.*/
	private static ContentValues values(UserLocation loc)
	{
		ContentValues out = new ContentValues();
		out.put("name", loc.name);
		out.put("address", loc.address);
		out.put("latitude", loc.latitude);
		out.put("longitude", loc.longitude);
		return out;
	}


	/**Sets up the database if necessary.*/
	public static void initIfNecessary(Context context)
	{
		File file = dbFile(context);
		if(file.exists())
			return;

		AssetManager assets = context.getAssets();
		try(InputStream in = assets.open("locations-init.sql"))
		{
			String script = new Scanner(in).useDelimiter("\\Z").next();
			String[] parts = script.split(";");
			try(SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.CREATE_IF_NECESSARY))
			{
				for(int i = 0; i < parts.length - 1; i++)
					db.execSQL(parts[i]);
			}
		}
		catch(IOException e)
		{
			file.delete();
			throw new RuntimeException(e);
		}
	}



	/**Inserts a new entry into the database and gives an ID to the provided object.*/
	public static void create(UserLocation location, Context context)
	{
		try(SQLiteDatabase db = db(context))
		{
			db.insert(TABLE, null, values(location));
		}
	}


	/**Finds the location object with the provided name.*
	 * Returns null if there is no such location.*/
	@Nullable
	public static UserLocation getByName(String name, Context context)
	{
		try(SQLiteDatabase db = db(context))
		{
			String query = "SELECT address, latitude, longitude FROM " + TABLE + " WHERE name = ?";
			try(Cursor cursor = db.rawQuery(query, new String[]{name}))
			{
				if(!cursor.moveToNext())
					return null;

				String address = cursor.getString(0);
				double lat = cursor.getDouble(1), lon = cursor.getDouble(2);
				return new UserLocation(name, address, lat, lon);
			}
		}
	}


    /**Returns all addresses in no particular order.*/
    public static List<UserLocation> getAll(Context context)
    {
        try(SQLiteDatabase db = db(context))
        {
			try(Cursor cursor = db.rawQuery("SELECT address, latitude, longitude, name FROM " + TABLE, null))
			{
				List<UserLocation> out = new ArrayList<>();
				while(cursor.moveToNext())
				{
					String address = cursor.getString(0);
					double lat = cursor.getDouble(1), lon = cursor.getDouble(2);
					String name = cursor.getString(3);
					out.add(new UserLocation(name, address, lat, lon));
				}

				return out;
			}
        }
    }


	/**Updates all fields of a location except the name.*/
	public static void update(UserLocation location, Context context)
	{
		try(SQLiteDatabase db = db(context))
		{
			ContentValues values = values(location);
			values.remove("name");
			db.update(TABLE, values, "name = ?", new String[]{location.name});
		}
	}


	/**Removes a location from the database.*/
	public static void delete(String name, Context context)
	{
		try(SQLiteDatabase db = db(context))
		{
			int deleted = db.delete(TABLE, "name = ?", new String[]{name});
			if(deleted != 1)
				throw new RuntimeException("Deleted an unexpected number of rows: " + deleted);
		}
	}
}
