package itworx.com.e_inspector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by karim on 11/8/2015.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    public DatabaseManager(Context context) {
        super(context, "casesManager", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "CREATE TABLE cases (agentId VARCHAR, caseNumber VARCHAR,description VARCHAR, endTime DATETIME DEFAULT CURRENT_DATE, incidentAudioURI VARCHAR, incidentImageURI VARCHAR, latitude VARCHAR, longitude VARCHAR, resolutionComment VARCHAR, resolutionImageURI VARCHAR, status VARCHAR, title VARCHAR, startTime VARCHAR DEFAULT CURRENT_DATE,submittedBy varchar ,escalation int)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addCase(Case c) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("agentId", c.agentId);
        contentValues.put("title", c.title);
        contentValues.put("description", c.description);
        contentValues.put("incidentImageURI", c.incidentImageURI);
        contentValues.put("incidentAudioURI", c.incidentAudioURI);
        contentValues.put("status", c.status);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert("cases", null, contentValues);
        Log.d("", "row#." + id);
    }

    public ArrayList<Case>   getAllCases()
    {
        ArrayList<Case> lst = new ArrayList<Case>();
        Case c = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor=  db.query("cases", null, null, null, null, null, null, null);
        while (cursor.moveToNext())
        {
           c  = new Case();
            c.title = cursor.getString(cursor.getColumnIndex("title"));
            c.description = cursor.getString(cursor.getColumnIndex("description"));
            c.incidentImageURI = cursor.getString(cursor.getColumnIndex("incidentImageURI"));
            c.incidentAudioURI = cursor.getString(cursor.getColumnIndex("incidentAudioURI"));
            c.status = cursor.getString(cursor.getColumnIndex("status"));
            lst.add(c);
        }
        return lst;
    }
}
