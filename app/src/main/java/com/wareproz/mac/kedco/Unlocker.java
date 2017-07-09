package com.wareproz.mac.kedco;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Unlocker extends AppCompatActivity {

    Button login_button;
    TextView txtpassword;
    ConnectionDetector connectionDetector;
    String username, password, staff_id;

    private ProgressDialog pDialog;

    // Session Manager Class
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlocker);

        connectionDetector = new ConnectionDetector(this);
        login_button = (Button) findViewById(R.id.login);
        txtpassword = (TextView) findViewById(R.id.password);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        staff_id = user.get(SessionManagement.KEY_STAFFID);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if there is internet access
                if(!connectionDetector.isConnectingToInternet()){

                    Toast.makeText(Unlocker.this,"Internet Network Not Avaliable",Toast.LENGTH_LONG).show();

                }else {

                    username = staff_id;
                    password = txtpassword.getText().toString();

                    if(password.trim().length() > 0){

                        new Unlocker.LoginUser().execute();

                    }else{
                        Toast.makeText(Unlocker.this,"Enter correct pin to unlock",Toast.LENGTH_LONG).show();

                    }

                }

            }
        });
    }

    private class LoginUser extends AsyncTask<Void, Void, Void> {

        String json_status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Unlocker.this);
            pDialog.setMessage("Unlocking ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = "unlock.php?username="+ username +"&password="+ password;
            String jsonStr = sh.makeServiceCall(url);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");

                    //JSONArray customerz = jsonObj.getJSONArray("customers");

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            //do something with what is returned
            if (json_status.equals("1")){
                //open home page
                Intent mIntent = new Intent(Unlocker.this, HomeActivity.class);
                startActivity(mIntent);
                finish();

            }else{
                //
                Toast.makeText(Unlocker.this,"Unable to Unlock, pls try again or contact admin",Toast.LENGTH_LONG).show();
            }
        }

    }
}
