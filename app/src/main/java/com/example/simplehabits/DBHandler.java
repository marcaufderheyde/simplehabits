package com.example.simplehabits;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "habitdb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME_CATEGORIES = "mycategories";
    private static final String TABLE_NAME_HABITS = "myhabits";

    // below variable is for our category id column
    private static final String ID_COL = "id";

    // below variable is for our category name column
    private static final String NAME_COL = "name";

    // below variable is for our category currency column.
    private static final String CURRENCY_COL = "currency";

    // below variable for our category sentiment column.
    private static final String DEFAULT_SENTIMENT_COL = "default_sentiment";

    // below variable for our category sentiment column.
    private static final String HABIT_SENTIMENT_COL = "habit_sentiment";

    // below variable for our habit date column.
    private static final String DATE_COL = "dateTIMESTAMP";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query_categories = "CREATE TABLE " + TABLE_NAME_CATEGORIES + " ("
                + NAME_COL + " TEXT PRIMARY KEY, "
                + CURRENCY_COL + " TEXT,"
                + DEFAULT_SENTIMENT_COL + " TEXT)";

        String query_habits = "CREATE TABLE " + TABLE_NAME_HABITS + " ("
                + DATE_COL + " PRIMARY KEY DEFAULT CURRENT_TIMESTAMP, "
                + CURRENCY_COL + " TEXT, "
                + HABIT_SENTIMENT_COL + " TEXT,"
                + NAME_COL + " TEXT,"
                + "FOREIGN KEY (" + NAME_COL + ")"
                + "REFERENCES " + TABLE_NAME_CATEGORIES
                + ")";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query_habits);
        db.execSQL(query_categories);
    }

    // this method is use to add new category to our sqlite database.
    public void addNewCategory(String catName, String catCurrency, String catSentiment) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, catName);
        values.put(CURRENCY_COL, catCurrency);
        values.put(DEFAULT_SENTIMENT_COL, catSentiment);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME_CATEGORIES, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    /**
     * UPDATES THE CATEGORY
     *
     * UPDATE TABLE > SET LASTNNAME(COL2) = newName = WHERE id = id in Question = AND LASTNAME(COL2) = oldName (was previously) >
     */
    public void updateCategory (String oldName,String newName, String newCurrency, String newSentiment) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_CATEGORIES + " SET " + NAME_COL +
                " = '" + newName + "', " + CURRENCY_COL + " = '" + newCurrency +
                "', " + DEFAULT_SENTIMENT_COL + " = '" + newSentiment +
                "' WHERE " + NAME_COL + " = '" + oldName + "'";

        //LOGS THE NEW NAME
        Log.d("updateCategory: query: ", query);
        Log.d("updateCategory: Setting name to ", newName); // NEW NAME CHANGING IT TO
        db.execSQL(query); // EXECUTE QUERY
    }

    /**
     * DELETE FROM DATABASE
     * >>> DELETE FROM TABLE WHERE name = name
     *
     */
    public void deleteCategory(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_CATEGORIES + " WHERE "
                + NAME_COL + " = '" + name + "'";

        Log.d("deleteName: query: ", query);
        Log.d("deleteName: Deleting ", name + " from database.");
        db.execSQL(query); // EXECUTE QUERY
    }

    public void deleteHabit(String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_HABITS + " WHERE "
                + DATE_COL + " = '" + timestamp + "'";

        Log.d("deleteName: query: ", query);
        Log.d("deleteName: Deleting ", timestamp + " from database.");
        db.execSQL(query); // EXECUTE QUERY
    }


    // this method is use to add new category to our sqlite database.
    public void addNewHabit(String habitName, String habitCurrency, String habitSentiment) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, habitName);
        values.put(CURRENCY_COL, habitCurrency);
        values.put(HABIT_SENTIMENT_COL, habitSentiment);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME_HABITS, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the habits.
    public ArrayList<CatModal> readHabits() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorHabits = db.rawQuery("SELECT * FROM " + TABLE_NAME_HABITS, null);

        // on below line we are creating a new array list.
        ArrayList<CatModal> habitModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorHabits.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                habitModalArrayList.add(new CatModal(cursorHabits.getString(3),
                        cursorHabits.getString(1),
                        cursorHabits.getString(2),
                        cursorHabits.getString(0)));
            } while (cursorHabits.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorHabits.close();
        return habitModalArrayList;
    }

    // We use special SQL queries to find only those Habits from X recent days
    public ArrayList<ArrayList<CatModal>> readXDaysHabits(int X_days) {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        // We filter by the timestamp column to retrieve only relevant habits
        Cursor cursorHabits = db.rawQuery("SELECT * FROM " + TABLE_NAME_HABITS
                                                + " WHERE " + DATE_COL
                                                + " > datetime('now' , '-" + X_days + " days')", null);

        // on below line we are creating a new array list.
        ArrayList<ArrayList<CatModal>> habitModalArrayList = new ArrayList<>();
        ArrayList<CatModal> perDayHabitModalArrayList = new ArrayList<>();
        String newestTimestamp;

        // moving our cursor to first position.
        if (cursorHabits.moveToFirst()) {
            Log.d("Trying to get day: ", cursorHabits.getString(0).substring(8,10));
            newestTimestamp = cursorHabits.getString(0).substring(8,10);
            do {
                if(cursorHabits.getString(0).substring(8,10).equals(newestTimestamp)){
                    Log.d("Found a matching timestamp: ", "TRUE");
                    // on below line we are adding the data from cursor to our array list.
                    perDayHabitModalArrayList.add(new CatModal(cursorHabits.getString(3),
                            cursorHabits.getString(1),
                            cursorHabits.getString(2),
                            cursorHabits.getString(0)));
                }
                else {
                    Log.d("Found a matching timestamp: ", "FALSE");
                    newestTimestamp = cursorHabits.getString(0).substring(8,10);
                    habitModalArrayList.add(perDayHabitModalArrayList);
                    perDayHabitModalArrayList.clear();
                    perDayHabitModalArrayList = new ArrayList<>();
                }

            } while (cursorHabits.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorHabits.close();
        habitModalArrayList.add(perDayHabitModalArrayList);
        return habitModalArrayList;
    }

    // we have created a new method for reading all the courses.
    public ArrayList<CatModal> readCategories() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCategories = db.rawQuery("SELECT * FROM " + TABLE_NAME_CATEGORIES, null);

        // on below line we are creating a new array list.
        ArrayList<CatModal> categoryModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCategories.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                categoryModalArrayList.add(new CatModal(cursorCategories.getString(0),
                        cursorCategories.getString(1),
                        cursorCategories.getString(2)));
            } while (cursorCategories.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCategories.close();
        return categoryModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HABITS);
        onCreate(db);
    }

}

