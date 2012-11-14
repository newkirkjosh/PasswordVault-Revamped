package org.research.thevault;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PVDatamanager implements Constants{
	
	private static PVDatamanager instance = null;
	
	protected PVDatamanager() {
	}
	
	public static PVDatamanager getInstance(){
		if(instance == null)
			instance = new PVDatamanager();
		return instance;
	}
	
	public synchronized ArrayList<HashMap<String, String>> getConversation(Context ctx, String other){
		ArrayList<HashMap<String, String>> convo = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = new PasswordVaultTables(ctx).getReadableDatabase();
		Cursor cursor = db.rawQuery("Select * from " + MESSAGE_TABLE_NAME + " where " + OTHER_MEMBER + "='" + other + "' order by " + _ID + " ASC", null);
		while(cursor.moveToNext()){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(OTHER_MEMBER, cursor.getString(cursor.getColumnIndex(OTHER_MEMBER)));
			map.put(RECIPIENT, cursor.getString(cursor.getColumnIndex(RECIPIENT)));
			map.put(MESSAGE, cursor.getString(cursor.getColumnIndex(MESSAGE)));
			map.put(SENDER, cursor.getString(cursor.getColumnIndex(SENDER)));
			convo.add(map);
		}
		db.close();
		return convo;
	}
	
	public synchronized String[] getConvos(Context ctx){
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase db = new PasswordVaultTables(ctx).getReadableDatabase();
		Cursor cursor = db.rawQuery("Select distinct " + OTHER_MEMBER + " from " + MESSAGE_TABLE_NAME, null);
		while(cursor.moveToNext()){
			if(cursor != null){
				list.add(cursor.getString(cursor.getColumnIndex(OTHER_MEMBER)));
			}
		}
		cursor.close();
		db.close();
		return list.toArray(new String[0]);
	}
	
	public synchronized HashMap<String, String> getLastMessage(Context ctx, String name){
		HashMap<String, String> map = new HashMap<String, String>();
		SQLiteDatabase db = new PasswordVaultTables(ctx).getReadableDatabase();
		Cursor cursor = db.rawQuery("Select * from " + MESSAGE_TABLE_NAME + " where " + OTHER_MEMBER + "='" + name + "' order by " + _ID + " DESC", null);
		if(cursor.moveToFirst()){
   	        map.put("name", cursor.getString(cursor.getColumnIndex(OTHER_MEMBER)));
   	        map.put("message", cursor.getString(cursor.getColumnIndex(MESSAGE)));
   	        map.put("time", cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
		}
		db.close();
		return map;
	}
	
	public synchronized void deleteConvo(Context ctx, String name){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		db.delete(MESSAGE_TABLE_NAME, OTHER_MEMBER + "=?", new String[] {name});
		db.close();
	}
	
	public synchronized void insertMessage(Context ctx, ContentValues values){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		db.insert(MESSAGE_TABLE_NAME, null, values);
		db.close();
	}
	
	public synchronized Cursor getPasswords(Context ctx, String password){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getReadableDatabase();
		Cursor cursor = db.rawQuery( "SELECT "+ PWORD + " FROM " + PASS_TABLE_NAME + " WHERE " + PWORD + "='" + password + "'", null);
		db.close();
		return cursor;
	}
	
	public synchronized void insertSite(Context ctx, ContentValues values){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		db.insert(SITE_TABLE_NAME, null, values);
		db.close();
	}
	
	public synchronized ArrayList<HashMap<String, String>> getSites(Context ctx){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		Cursor cursor = db.rawQuery( "SELECT * FROM " + SITE_TABLE_NAME, null);
		while(cursor.moveToNext()){
			if(cursor != null){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(_ID, cursor.getInt(cursor.getColumnIndex(_ID)) + "");
				map.put(URL, cursor.getString(cursor.getColumnIndex(URL)));
	   	        map.put(UNAME, cursor.getString(cursor.getColumnIndex(UNAME)));
	   	        map.put(PWORD, cursor.getString(cursor.getColumnIndex(PWORD)));
	   	        list.add(map);
			}
		}
		db.close();
		return list;
	}
	
	public synchronized void deleteSite(Context ctx, int id){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		db.delete(SITE_TABLE_NAME, _ID + "=" + id, null);
		db.close();
	}
	
	public synchronized void insertPassword(Context ctx, ContentValues values){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		db.insertOrThrow( PASS_TABLE_NAME, null, values );
		db.close();
	}
	
	public synchronized boolean hasPassword(Context ctx){
		SQLiteDatabase db = new PasswordVaultTables(ctx).getWritableDatabase();
		Cursor passCur = db.rawQuery( "SELECT * FROM " + PASS_TABLE_NAME, null);
		boolean hasPass = passCur.moveToFirst();
		passCur.close();
		db.close();
		return hasPass;
	}
}
