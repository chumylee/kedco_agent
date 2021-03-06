package com.wareproz.mac.kedco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wareproz.mac.kedco.SessionManagement.KEY_ID;

public class MeterBypassCheck extends BaseActivity implements AdapterView.OnItemSelectedListener{

    AutoCompleteTextView accountno;
    TextView custName,inputtext;
    Spinner inputtext2;
    Button submitButton,getDetails;
    ConnectionDetector connectionDetector;
    String[] accountnos;
    String customerAccNo,customerName,customerId,json_status,billDate,billDate2;

    private ProgressDialog pDialog;

    // Session Manager Class
    SessionManagement session;
    String cid, fullname, role, staff_id, email, phone, customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_bypass_check);

        connectionDetector = new ConnectionDetector(this);
        accountno = (AutoCompleteTextView)findViewById(R.id.accountno);
        getDetails = (Button) findViewById(R.id.getdetails);
        submitButton = (Button) findViewById(R.id.submit);
        custName = (TextView) findViewById(R.id.custname);
        inputtext = (TextView) findViewById(R.id.inputtext);
        inputtext2 = (Spinner) findViewById(R.id.bypass);

        customerId="";


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

        accountnos = customers.split(",");

        ArrayAdapter adapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,accountnos);

        accountno.setAdapter(adapter);
        accountno.setThreshold(1);

        getDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customerAccNo = accountno.getText().toString();
                if(customerAccNo.trim().length() > 0 ){

                    new MeterBypassCheck.GetCustomerDetails().execute();

                }else{
                    Toast.makeText(MeterBypassCheck.this,"Enter Customer Account number first",Toast.LENGTH_LONG).show();

                }

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(customerId.length() > 0 ){

                    billDate = inputtext.getText().toString();

                    Intent changer = new Intent(MeterBypassCheck.this, MeterBypassConfirmation.class);
                    changer.putExtra("customerId", customerId);
                    changer.putExtra("customerName", customerName);
                    changer.putExtra("customerAccNo", customerAccNo);
                    changer.putExtra("billDate", billDate);
                    changer.putExtra("billDate2", billDate2);
                    startActivity(changer);

                }else{
                    Toast.makeText(MeterBypassCheck.this,"No customer selected",Toast.LENGTH_LONG).show();

                }

            }
        });

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.bypass);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Noticed");
        categories.add("Suspected");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        billDate2 = parent.getItemAtPosition(position).toString();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private class GetCustomerDetails extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MeterBypassCheck.this);
            pDialog.setMessage("Getting Customer Details ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "getCustomerDetails.php?accountno="+ customerAccNo +"&id="+ staff_id;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    customerId = jsonObj.getString("id");
                    customerName = jsonObj.getString("customername");

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
                // Creating user login session and store some stuff
                custName.setText(customerName);
            }else{
                //
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeterBypassCheck.this);
                alertDialogBuilder.setMessage("The entered account number does not exist OR you dont have access the manage that customer");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

    }

}
