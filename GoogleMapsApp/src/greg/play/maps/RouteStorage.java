package greg.play.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class RouteStorage extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "routeStorageDb";
	private static final String ROUTE_ID = "routeId";
	private static final String ROUTE_DATA = "routeData";
    private static final String DICTIONARY_TABLE_NAME = "routeHistory";
    private static final String DICTIONARY_TABLE_CREATE =
                "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                ROUTE_ID + " TEXT, " +
                ROUTE_DATA + " TEXT);";
    
    RouteStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
   
	public RouteStorage(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DICTIONARY_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public ArrayList<String> getRouteIds()
	{
		ArrayList<String> routeIds = new ArrayList<String>();
		SQLiteDatabase db = super.getReadableDatabase();
		Cursor cursor = db.query(DICTIONARY_TABLE_NAME, new String[] {ROUTE_ID, ROUTE_DATA}, null, null, null, null, null);
		boolean dataExists = cursor.moveToFirst();
		while (dataExists)
		{
			String routeId = cursor.getString(cursor.getColumnIndex(ROUTE_ID));
			routeIds.add(routeId);
			
			dataExists = cursor.moveToNext();
		}
				
		return routeIds;
	}
	
	public String getRouteData(String routeId)
	{
		String routeData = "";
		String whereClause = ROUTE_ID + "='" + routeId + "'";
		
		SQLiteDatabase db = super.getReadableDatabase();
		
		Cursor cursor = db.query(DICTIONARY_TABLE_NAME, new String[] {ROUTE_ID, ROUTE_DATA}, whereClause, null, null, null, null);
		
		boolean dataExists = cursor.moveToFirst();
		if (dataExists)
		{
			routeData = cursor.getString(cursor.getColumnIndex(ROUTE_DATA));
		}		
		
		return routeData;
	}
	
	public void addRouteData(String routeId, String routeData)
	{	
		SQLiteDatabase db = super.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ROUTE_ID, routeId);
		values.put(ROUTE_DATA, routeData);
		
		db.insert(DICTIONARY_TABLE_NAME, null, values);
	}

}
