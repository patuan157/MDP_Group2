package example.com.mdp_group2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class PreferenceActivity extends Activity {

    FunctionPreference functionPref;
    EditText editText_f1;
    EditText editText_f2;
    String function_pref_string_f1;
    String function_pref_string_f2;
    String string_f1;
    String string_f2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        functionPref = new FunctionPreference(getApplicationContext());

        editText_f1 = (EditText) findViewById(R.id.edittext_f1);
        editText_f2 = (EditText) findViewById(R.id.edittext_f2);

        function_pref_string_f1 = functionPref.getFunctionsDetails().get("f1");
        function_pref_string_f2 = functionPref.getFunctionsDetails().get("f2");

        if (function_pref_string_f1 != null && function_pref_string_f2 != null) {
            editText_f1.setText(function_pref_string_f1);
            editText_f2.setText(function_pref_string_f2);
        }

        editText_f1.setSelection(editText_f1.getText().length());
        editText_f2.setSelection(editText_f2.getText().length());

        editText_f2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    save();
                }
                return false;
            }
        });
    }

    public void onSaveBtnClicked(View view) {
        save();
        //Toast.makeText(this, "Function preferences saved successfully.", Toast.LENGTH_LONG).show();
    }

    public void onClearBtnClicked(View view) {
        clear();
        //Toast.makeText(this, "Function preferences cleared successfully.", Toast.LENGTH_LONG).show();
    }

    public void save() {
        string_f1 = editText_f1.getText().toString();
        string_f2 = editText_f2.getText().toString();

        if (string_f1 != null && string_f2 != null) {
            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
            confirmDialog.setTitle("Error!");

            confirmDialog
                    .setMessage("Are you sure you want to save the function preferences?")
                    .setCancelable(true)
                    .setPositiveButton("Save",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            functionPref.createFunctions(string_f1, string_f2);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = confirmDialog.create();

            // show it
            alertDialog.show();
        } else {
            Toast.makeText(this, "Please enter values for F1 and F2", Toast.LENGTH_LONG).show();
        }
    }

    public void clear() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("Error!");

        confirmDialog
                .setMessage("Are you sure you want to clear the function preferences?")
                .setCancelable(true)
                .setPositiveButton("Clear",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        functionPref.clearFunctions();
                        editText_f1.clearComposingText();
                        editText_f2.clearComposingText();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = confirmDialog.create();

        // show it
        alertDialog.show();
    }

}
