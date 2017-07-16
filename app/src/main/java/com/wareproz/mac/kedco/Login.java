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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Button login_button;
    TextView txtusername, txtpassword, register;
    ConnectionDetector connectionDetector;
    String username, password, Token;

    private ProgressDialog pDialog;

    // Session Manager Class
    SessionManagement session;

    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionDetector = new ConnectionDetector(this);
        login_button = (Button) findViewById(R.id.login);
        txtusername = (TextView) findViewById(R.id.usernamex);
        txtpassword = (TextView) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.textView3);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

//      check if user is logged on and just send him straight to home

        if(session.isLoggedIn()){
            //open home page
            Intent mIntent = new Intent(Login.this, HomeActivity.class);
            startActivity(mIntent);
            finish();
        }


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if there is internet access
                if(!connectionDetector.isConnectingToInternet()){

                    Toast.makeText(Login.this,"Internet Network Not Avaliable",Toast.LENGTH_LONG).show();

                }else {

                    username = txtusername.getText().toString();
                    password = txtpassword.getText().toString();

                    if(username.trim().length() > 0 && password.trim().length() > 0){

                        new LoginUser().execute();

                    }else{
                        Toast.makeText(Login.this,"Staff ID And Password Must Be Entered",Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

        //subscribe to push notification
        FirebaseMessaging.getInstance().subscribeToTopic("Kedco");
        FirebaseInstanceId.getInstance().getToken();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Login.this, Register.class);
                startActivity(mIntent);
                finish();
            }
        });

    }

    private class LoginUser extends AsyncTask<Void, Void, Void> {

        String json_status,id, fullname, role, staff_id, email, phone, msg, customers, mdcustomers, grid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            Token="";
            if(!session.hasToken()) {
                Token = g.getToken();
            }else{
                Token = session.getTokenDetails();
            }
            String url = "login.php?username="+ username +"&password="+ password +"&token="+ Token;
            String jsonStr = sh.makeServiceCall(url);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    id = jsonObj.getString("id");
                    fullname = jsonObj.getString("fullname");
                    role = jsonObj.getString("role");
                    staff_id = jsonObj.getString("staff_id");
                    email = jsonObj.getString("email");
                    phone = jsonObj.getString("phone");
                    msg = jsonObj.getString("msg");
                    customers = jsonObj.getString("customers");
                    mdcustomers = jsonObj.getString("mdcustomers");
                    grid = jsonObj.getString("grid");

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
                // Creating user login session and store some stuff
                session.createLoginSession(id,fullname,role,staff_id,email,phone,customers,mdcustomers,grid);

                //store token in preference
                if(!session.hasToken()) {
                    session.storeToken(Token);
                }

                //open home page
                Intent mIntent = new Intent(Login.this, HomeActivity.class);
                startActivity(mIntent);
                finish();

            }else{
                //
                Toast.makeText(Login.this,msg,Toast.LENGTH_LONG).show();
            }
        }

    }
}
