package markm.webshareproj;

import static markm.webshareproj.WebDatabaseHelper.COLUMN_CATEGORY;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_DESCRIPTION;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_ID;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_LINK;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_ORIGIN;
import static markm.webshareproj.WebDatabaseHelper.COLUMN_TITLE;
import static markm.webshareproj.WebDatabaseHelper.TABLE_WEB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * A static import will inject the entire public\protect methods and parameters of the import subject
 */

public class WebDatabaseHandler {

	private Context c;

	private WebDatabaseHelper helper = null;

	public WebDatabaseHandler(Context context) {
		helper = new WebDatabaseHelper(context);
		c = context;
	}
	
	// פונקציה שניקח את כל הנתונים בדאטאבייס ונחזיר cursor
	public Cursor getAllLink() {
		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor c = db.query(TABLE_WEB,
				new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION,
						COLUMN_LINK, COLUMN_CATEGORY, COLUMN_ORIGIN }, null,
				null, null, null, null);

		return c;
	}
	// הפונקציה הזאת מוסיפה  לינק לבסיס הנתונים.
	public void addLink(String title, String description, String link, String category, String origin) {

		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_DESCRIPTION, description);
		cv.put(COLUMN_LINK, link);
		cv.put(COLUMN_CATEGORY, category);
		cv.put(COLUMN_ORIGIN, origin);
		
		try {
			db.insertOrThrow(TABLE_WEB, null, cv);	
		} catch (Exception e) {
			Log.e("database problem","addLink to db: problem inserting table values.");
		}finally{
			if(db.isOpen())db.close();
		}
}
	// deleting entries using id.
	public void removeLink(long id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(TABLE_WEB, "_id=?", new String[] { String.valueOf(id) });
	}

}
