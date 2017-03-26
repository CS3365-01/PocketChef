package ttu.edu.pocketchef.content;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ttu.edu.pocketchef.R;

public class DB {
    private static File localDBFile;
    private static SQLiteDatabase sqlDB;

    public static boolean create(Resources res, File cacheDir) {
        File local = new File(cacheDir, "local.db");
        localDBFile = local;

        if (!getDBFile().exists()) {
            try {
                InputStream in = res.openRawResource(R.raw.local);
                FileOutputStream out = new FileOutputStream(local);
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                } finally {
                    in.close();
                    out.close();
                }

                return local.exists();
            } catch (Throwable t) {
                t.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static SQLiteDatabase getDB() {
        if (getDBFile() == null)
            return null;

        if (sqlDB == null) {
            sqlDB = SQLiteDatabase.openDatabase(localDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        }

        return sqlDB;
    }

    public static File getDBFile() {
        return localDBFile;
    }

    public static void dumpRecipes() {
        if (getDB() == null)
            return;

        String selectQuery = "SELECT * FROM Recipe";
        Cursor c = getDB().rawQuery(selectQuery, null);
        StringBuilder sb = new StringBuilder();
        while (c.moveToNext()) {
            for (String cname : c.getColumnNames()) {
                sb.append(cname + ": " + c.getString(c.getColumnIndex(cname)) + " ");
            }
            sb.append("\n");
        }
        Log.i("ReciepeTable", sb.toString());
        c.close();
    }
}
