package teamsylvanmatthew.memecenter.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import teamsylvanmatthew.memecenter.Models.Filter;
import teamsylvanmatthew.memecenter.Models.Rule;

public class MemeCenterDataSource {
    private static final String TAG = "MemeCenterDataSource";
    private SQLiteDatabase database;
    private MemeCenterDatabaseHelper databaseHelper;

    public MemeCenterDataSource(Context context) {
        databaseHelper = new MemeCenterDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }


    public ArrayList<String> getRules(long filterId) {
        ArrayList<String> rules = new ArrayList<String>();

        final String query = "SELECT " + Rule.REGEX_COLUMN + " FROM " + Rule.TABLE_NAME + " WHERE " + Rule.FILTER_ID_FK_COLUMN + " = " + filterId + ";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while (!cursor.isAfterLast()) {
            rules.add(cursor.getString(0));
            cursor.moveToNext();
        }
        Log.i(TAG, "query: " + query);

        return rules;
    }

    public long getFilterId(String name) {
        final String query = "SELECT " + Filter.FILTER_ID_COLUMN + " FROM " + Filter.TABLE_NAME + " WHERE " + Filter.NAME_COLUMN + " = \"" + name + "\";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            return (long) cursor.getInt(0);
        }
        return -1;
    }

    public ArrayList<Filter> getAllFilters() {
        ArrayList<Filter> filters = new ArrayList<Filter>();

        final String query = "SELECT " + Filter.FILTER_ID_COLUMN + ", " + Filter.NAME_COLUMN + " FROM " + Filter.TABLE_NAME + ";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while (!cursor.isAfterLast()) {
            filters.add(new Filter(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        Log.i(TAG, "query: " + query);

        return filters;
    }

    public long addFilter(Filter filter) {
        Log.i(TAG, "addFilter: (" + filter.getId() + ", " + filter.getName() + ")");

        if (filter != null) {
            ContentValues currentValues = new ContentValues();
            currentValues.put(Filter.NAME_COLUMN, filter.getName());
            return database.insert(Filter.TABLE_NAME, null, currentValues);
        }

        return -1;
    }

    public void updateFilter(String oldName, String newName) {
        Log.i(TAG, "updateFilter: (" + oldName + ")");
        if (oldName != null && newName != null && !oldName.equals(newName)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Filter.NAME_COLUMN, newName);
            database.update(Filter.TABLE_NAME, contentValues, Filter.NAME_COLUMN + "=" + oldName, null);
        }
    }

    public boolean deleteFilter(Filter filter) {
        final String DELETE_FILTER = "DELETE FROM " + Filter.TABLE_NAME + " WHERE " + Filter.FILTER_ID_COLUMN + " = " + filter.getId() + ";";
        final String DELETE_RULES = "DELETE FROM " + Rule.TABLE_NAME + " WHERE " + Rule.FILTER_ID_FK_COLUMN + " = " + filter.getId() + ";";

        database.execSQL(DELETE_FILTER);
        database.execSQL(DELETE_RULES);

        Log.i(TAG, "DELETE: " + DELETE_FILTER);
        Log.i(TAG, "DELETE: " + DELETE_RULES);


        return true;
    }

    public long addRule(Rule rule) {
        Log.i(TAG, "addRule: (" + rule.getRuleId() + ", " + rule.getFilter_fk_id() + ", " + rule.getRegex() + ")");

        if (rule != null) {
            ContentValues currentValues = new ContentValues();
            currentValues.put(Rule.FILTER_ID_FK_COLUMN, rule.getFilter_fk_id());
            currentValues.put(Rule.REGEX_COLUMN, rule.getRegex());
            return database.insert(Rule.TABLE_NAME, null, currentValues);
        }

        return -1;
    }

    public boolean deleteRules(long filterId) {
        Log.i(TAG, "deleteRules: (" + filterId + ")");

        return database.delete(Rule.TABLE_NAME, Rule.FILTER_ID_FK_COLUMN + "=" + filterId, null) > 0;
    }
}
