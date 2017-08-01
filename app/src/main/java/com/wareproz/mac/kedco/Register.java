package com.wareproz.mac.kedco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    TextView txtlogin;
    Button register_button;
    EditText txtstaffid, txtpassword, txtpassword2, txtemail, txtphonenumber;
    String staffid, password, password2, email, phonenumber;
    ConnectionDetector connectionDetector;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        connectionDetector = new ConnectionDetector(this);
        txtlogin = (TextView) findViewById(R.id.tologin);
        register_button = (Button) findViewById(R.id.changepass);
        txtstaffid = (EditText) findViewById(R.id.staffid);
        txtpassword = (EditText) findViewById(R.id.new_pin);
        txtpassword2 = (EditText) findViewById(R.id.password2);
        txtemail = (EditText) findViewById(R.id.email);
        txtphonenumber = (EditText) findViewById(R.id.phone);

        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Register.this, Login.class);
                startActivity(mIntent);
                finish();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!connectionDetector.isConnectingToInternet()){

                    Toast.makeText(Register.this,"Internet Network Not Avaliable",Toast.LENGTH_LONG).show();

                }else {

                    staffid = txtstaffid.getText().toString();
                    password = txtpassword.getText().toString();
                    password2 = txtpassword2.getText().toString();
                    email = txtemail.getText().toString();
                    phonenumber = txtphonenumber.getText().toString();

                    if(staffid.trim().length() > 0 && password.trim().length() > 0 && password2.trim().length() > 0
                            && email.trim().length() > 0 && phonenumber.trim().length() > 0){

                        if (password.equals(password2)){
                            new Register.RegisterUser().execute();
                        }else{
                            Toast.makeText(Register.this,"Password mismatch, try again",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(Register.this,"All fields are compulsory",Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

    }

    private class RegisterUser extends AsyncTask<Void, Void, Void> {

        String json_status,json_msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "register.php?staffid="+ staffid +"&password="+ password +"&email="+ email +"&phonenumber="+ phonenumber;

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    json_msg = jsonObj.getString("msg");
                    //JSONArray contacts = jsonObj.getJSONArray("contacts");

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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register.this);
                alertDialogBuilder.setMessage(json_msg);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent mIntent = new Intent(Register.this, Login.class);
                                startActivity(mIntent);
                                finish();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }else{
                //
                Toast.makeText(Register.this,json_msg,Toast.LENGTH_LONG).show();
            }
        }

    }
}
