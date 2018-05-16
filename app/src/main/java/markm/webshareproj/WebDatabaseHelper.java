package markm.webshareproj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WebDatabaseHelper extends SQLiteOpenHelper {

	// db name.
	private static final String DATABASE_NAME = "webs.db";
	// table name.
	public static final String TABLE_WEB = "webs_table";

	// columns.
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_ORIGIN = "origin";

	private static final int DATABASE_VERSION = 1;

	// Create SQL query that creates the table
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_WEB
			+ "(" + COLUMN_ID + " integer primary key autoincrement," + " "
			+ COLUMN_TITLE + " text not null," + " " 
			+ COLUMN_DESCRIPTION + " text not null," + " " 
			+ COLUMN_LINK + " text not null," + " "
			+ COLUMN_CATEGORY + " text not null," + " " 
			+ COLUMN_ORIGIN + " text not null);";

	public WebDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
