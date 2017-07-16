package com.wareproz.mac.kedco;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.wareproz.mac.kedco.SessionManagement.KEY_ID;

public class MeterInstallationConfirmation extends BaseActivity implements SeekBar.OnSeekBarChangeListener  {

    private ProgressDialog pDialog;
    String customerAccNo,customerName,customerId,billDate;
    TextView custName,custAccNo,billPeriod;

    // Session Manager Class
    SessionManagement session;
    String cid, fullname, role, staff_id, email, phone, customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_installation_confirmation);

        Bundle bundle = getIntent().getExtras();
        customerId = bundle.getString("customerId");
        customerName = bundle.getString("customerName");
        customerAccNo = bundle.getString("customerAccNo");
        billDate = bundle.getString("billDate");

        custName = (TextView) findViewById(R.id.custName);
        custAccNo = (TextView) findViewById(R.id.custAccNo);
        billPeriod = (TextView) findViewById(R.id.billPeriod);

        custName.setText(customerName);
        custAccNo.setText(customerAccNo);
        billPeriod.setText(billDate);

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

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
        seekbar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(i == 100)
        {
            new MeterInstallationConfirmation.confirmer().execute();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (seekBar.getProgress() < 100) {
            seekBar.setProgress(0);
        }
    }

    private class confirmer extends AsyncTask<Void, Void, Void> {

        String json_status,msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MeterInstallationConfirmation.this);
            pDialog.setMessage("Submitting meter installation details ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "meterInstallationSubmit.php?customer_id="+ customerId +"&bill_period="+ billDate +"&salesrep="+ cid;
            String jsonStr = sh.makeServiceCall(url);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    msg = jsonObj.getString("msg");

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

                Toast.makeText(MeterInstallationConfirmation.this,msg,Toast.LENGTH_LONG).show();

                Intent mIntent = new Intent(MeterInstallationConfirmation.this, HomeActivity.class);
                startActivity(mIntent);

            }else{
                //
                Toast.makeText(MeterInstallationConfirmation.this,msg,Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
}
