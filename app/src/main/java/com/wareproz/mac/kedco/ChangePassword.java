package com.wareproz.mac.kedco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.wareproz.mac.kedco.SessionManagement.KEY_ID;

public class ChangePassword extends BaseActivity  {

    Button changepass;
    TextView oldpin, newpin, confirmpin;
    ConnectionDetector connectionDetector;
    String old_pin, new_pin, confirm_pin;

    private ProgressDialog pDialog;
    // Session Manager Class
    SessionManagement session;
    String cid, fullname, role, staff_id, email, phone, customers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        connectionDetector = new ConnectionDetector(this);
        oldpin = (TextView) findViewById(R.id.old_pin);
        newpin = (TextView) findViewById(R.id.new_pin);
        confirmpin = (TextView) findViewById(R.id.confirm_pin);
        changepass = (Button) findViewById(R.id.changepass);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        cid = user.get(KEY_ID);
        fullname = user.get(SessionManagement.FULLNAME);
        role = user.get(SessionManagement.ROLE);
        staff_id = user.get(SessionManagement.KEY_STAFFID);
        email = user.get(SessionManagement.EMAIL);
        phone = user.get(SessionManagement.PHONE);
        customers = user.get(SessionManagement.CUSTOMERS);


        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if there is internet access
                if(!connectionDetector.isConnectingToInternet()){

                    Toast.makeText(ChangePassword.this,"Internet Network Not Avaliable",Toast.LENGTH_LONG).show();

                }else {

                    old_pin = oldpin.getText().toString();
                    new_pin = newpin.getText().toString();
                    confirm_pin = confirmpin.getText().toString();

                    if(old_pin.trim().length() > 0 && new_pin.trim().length() > 0 && confirm_pin.trim().length() > 0){

                        if (Objects.equals(new_pin, confirm_pin)){
                            new ChangePass().execute();
                        }else {
                            Toast.makeText(ChangePassword.this,"New PIN Mismatch",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(ChangePassword.this,"All fields are compulsory",Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

    }

    private class ChangePass extends AsyncTask<Void, Void, Void> {

        String json_status,id, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "changepassword.php?oldpass="+ old_pin +"&newpass="+ new_pin +"&confirmpass="+ confirm_pin +"&staff_id="+ staff_id;
            String jsonStr = sh.makeServiceCall(url);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    message = jsonObj.getString("message");

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePassword.this);
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent mIntent = new Intent(ChangePassword.this, HomeActivity.class);
                                startActivity(mIntent);
                                finish();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }else{
                //
                Toast.makeText(ChangePassword.this,message,Toast.LENGTH_LONG).show();
            }
        }

    }

}
