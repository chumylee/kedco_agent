package com.wareproz.mac.kedco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.wareproz.mac.kedco.SessionManagement.KEY_ID;

public class MeterBypassConf extends BaseActivity {

    private String TAG = Disconnection.class.getSimpleName();
    String selecteditem;

    // Session Manager Class
    SessionManagement session;
    String cid, fullname, role, staff_id, email, phone, customers;
    private ProgressDialog pDialog;
    private ListView lv;
    private boolean meterBypassed = false;

    // URL to get contacts JSON
    private static String url = "meterbypass.php";

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_bypass_conf);

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

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new MeterBypassConf.GetContacts().execute();
    }



    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MeterBypassConf.this);
            pDialog.setMessage("Loading ... ");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "meterbypass.php?role="+ role +"&id="+ staff_id;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("result");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = "Customer Name: " + c.getString("customername");
                        String email = "Account Number: " + c.getString("accountnumber");
                        String address = "Customer Address: " + c.getString("address");
                        String bypass = "Bypass: " + c.getString("bypass");
                        String reqby = "Requested By: " + c.getString("reqby");
                        String csp = "CSP: " + c.getString("csp");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                        Date parsedDate = null;
                        try {
                            parsedDate = formatter.parse(c.getString("date"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parsedDate);

                        String date = "Checked on: " + formatter2.format(parsedDate);
                        //String gender = c.getString("gender");

                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        //String mobile = phone.getString("mobile");
                        //String home = phone.getString("home");
                        //String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", address);
                        contact.put("date", date);
                        contact.put("bypass", bypass);
                        contact.put("reqby", reqby);
                        contact.put("csp", csp);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MeterBypassConf.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile", "bypass", "date", "id", "reqby", "csp"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile, R.id.reason, R.id.new_tariff, R.id.id, R.id.reqby, R.id.csp});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    if (Integer.parseInt(role) != 3){

                        Toast.makeText(MeterBypassConf.this,"You dont have permission to perform this action",Toast.LENGTH_LONG).show();

                    }else {

                        selecteditem = ((TextView)view.findViewById(R.id.id)).getText().toString();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeterBypassConf.this);
                        alertDialogBuilder.setMessage("Is this meter bypassed by the customer?");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        meterBypassed = true;
                                        new MeterBypassConf.confirmer().execute();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                meterBypassed = false;
                                new MeterBypassConf.confirmer().execute();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }

                }
            });
        }

    }

    private class confirmer extends AsyncTask<Void, Void, Void> {

        String json_status,json_msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MeterBypassConf.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "confirm.php?id="+ selecteditem +"&what=meterbypass&bypass="+meterBypassed;
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

                Toast.makeText(MeterBypassConf.this,json_msg,Toast.LENGTH_LONG).show();
                finish();

            }else{
                //
                Toast.makeText(MeterBypassConf.this,"Unable to confirm, try again",Toast.LENGTH_LONG).show();
            }
        }

    }
}
