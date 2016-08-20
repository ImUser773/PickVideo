package com.iamdeveloper.pickimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by IamDeveloper on 8/20/2016.
 */
public class RealPath {

    @SuppressLint("NewApi")
    public static String getRealPatchFromURI_API19(Context context, Uri uri){
        String filePatch = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        Log.i("wholeID",wholeID);
        String id = wholeID.split(":")[1];
        Log.i("id",id);
        String[] column = {MediaStore.Images.Media.DATA};
        Log.i("column",column[0]);
        String sel = MediaStore.Images.Media._ID + "=?";
        Log.i("sel",sel);
        Cursor cursor = context.getContentResolver().query
                (MediaStore.Images.Media.EXTERNAL_CONTENT_URI,column,sel,new String[]{id},null);
        assert cursor != null;
        int columnIndex = cursor.getColumnIndex(column[0]);
        if(cursor.moveToFirst()){
            filePatch = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePatch;

    }
}
