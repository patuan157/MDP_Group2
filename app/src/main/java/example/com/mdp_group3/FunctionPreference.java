package example.com.mdp_group3;


import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FunctionPreference {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // SharedPreference file name
    private static final String PREF_NAME = "mdpFileFunctionPref";

    // User name (make variable public to access from outside)
    public static final String KEY_F1 = "f1";

    // Email address (make variable public to access from outside)
    public static final String KEY_F2 = "f2";

    // Constructor
    public FunctionPreference(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create functions details
     * */
    public void createFunctions(String f1, String f2){

        // Storing name in pref
        editor.putString(KEY_F1, f1);

        // Storing email in pref
        editor.putString(KEY_F2, f2);

        // commit changes
        editor.commit();
    }

    /**
     * Get functions details
     * */
    public HashMap<String, String> getFunctionsDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_F1, pref.getString(KEY_F1, null));

        // user email id
        user.put(KEY_F2, pref.getString(KEY_F2, null));

        // return user
        return user;
    }

    /**
     * Clear functions details
     * */
    public void clearFunctions(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

}