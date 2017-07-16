package com.wareproz.mac.kedco;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

public class SessionManagement {

    // Shared Preferences
    SharedPreferences pref, pref2;

    // Editor for Shared preferences
    Editor editor, editor2;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "KedcoPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    //
    public static final String KEY_STAFFID = "staff_id";
    public static final String KEY_ID = "id";
    public static final String FULLNAME = "fullname";
    public static final String ROLE = "role";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String CUSTOMERS = "customers";
    public static final String MDCUSTOMERS = "mdcustomers";
    public static final String GRID = "grid";


    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        pref2 = _context.getSharedPreferences("KedcoPref2", PRIVATE_MODE);
        editor2 = pref2.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String fullname,String role
            , String staff_id,String email,String phone,String customers,String mdcustomers,String grid){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing email in pref
        editor.putString(KEY_STAFFID, staff_id);
        editor.putString(KEY_ID, id);
        editor.putString(FULLNAME, fullname);
        editor.putString(ROLE, role);
        editor.putString(EMAIL, email);
        editor.putString(PHONE, phone);
        editor.putString(CUSTOMERS, customers);
        editor.putString(MDCUSTOMERS, mdcustomers);
        editor.putString(GRID, grid);
        // commit changes
        editor.commit();

    }

    /**
     * Store Token
     * */
    public void storeToken (String token){
        // Storing token in pref2
        editor2.putString("token", token);
        // commit changes
        editor2.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_STAFFID, pref.getString(KEY_STAFFID, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(FULLNAME, pref.getString(FULLNAME, null));
        user.put(ROLE, pref.getString(ROLE, null));
        user.put(EMAIL, pref.getString(EMAIL, null));
        user.put(PHONE, pref.getString(PHONE, null));
        user.put(CUSTOMERS, pref.getString(CUSTOMERS, null));
        user.put(MDCUSTOMERS, pref.getString(MDCUSTOMERS, null));
        user.put(GRID, pref.getString(GRID, null));

        // return user
        return user;
    }

    public String getTokenDetails(){

        String token = pref2.getString("token", null);
        // return user
        return token;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        //clear user data too ... to clear push note token
        clearApplicationData();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void clearApplicationData() {
        File cache = _context.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    // Get token
    public boolean hasToken(){

        String token = pref2.getString("token", null);
        if (token != null) {
            return true;
        }else{
            return false;
        }
    }

}

