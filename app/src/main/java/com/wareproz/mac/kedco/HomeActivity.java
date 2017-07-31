package com.wareproz.mac.kedco;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.wareproz.mac.kedco.SessionManagement.KEY_ID;

public class HomeActivity extends BaseActivity  {

    // Session Manager Class
    SessionManagement session;
    private ProgressDialog pDialog;
    String id, fullname, role, staff_id, email, phone, customers, mdcustomers, grid , unlocker;


    //right menu slide
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Context cont = this;
    RelativeLayout rightMenu;
    //!right menu slide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(KEY_ID);
        fullname = user.get(SessionManagement.FULLNAME);
        role = user.get(SessionManagement.ROLE);
        staff_id = user.get(SessionManagement.KEY_STAFFID);
        email = user.get(SessionManagement.EMAIL);
        phone = user.get(SessionManagement.PHONE);
        customers = user.get(SessionManagement.CUSTOMERS);
        mdcustomers = user.get(SessionManagement.MDCUSTOMERS);
        grid = user.get(SessionManagement.GRID);


        TextView namefield = (TextView) findViewById(R.id.usersname);
        namefield.setText(fullname);

        RelativeLayout bill_distribution = (RelativeLayout) findViewById(R.id.bill_distribution);
        RelativeLayout meter_reading = (RelativeLayout) findViewById(R.id.meter_reading);
        RelativeLayout meter_installation = (RelativeLayout) findViewById(R.id.meter_installation);
        RelativeLayout disconnection_request = (RelativeLayout) findViewById(R.id.disconnection_request);
        RelativeLayout disconnection = (RelativeLayout) findViewById(R.id.disconnection);
        RelativeLayout reconnection_request = (RelativeLayout) findViewById(R.id.reconnection_request);
        RelativeLayout reconnection = (RelativeLayout) findViewById(R.id.reconnection);
        RelativeLayout tarriff_adjustment_request = (RelativeLayout) findViewById(R.id.tarriff_adjustment_request);
        RelativeLayout tarriff_adjustment = (RelativeLayout) findViewById(R.id.tarriff_adjustment);
        RelativeLayout meter_bypass_check = (RelativeLayout) findViewById(R.id.meter_bypass_check);
        RelativeLayout meter_bypass_confirmation = (RelativeLayout) findViewById(R.id.meter_bypass_confirmation);
        RelativeLayout fault_reporting = (RelativeLayout) findViewById(R.id.fault_reporting);
        RelativeLayout fault_handling = (RelativeLayout) findViewById(R.id.fault_handling);
        RelativeLayout grid_meter_reading = (RelativeLayout) findViewById(R.id.grid_meter_reading);

        fault_reporting.setAlpha(1);


        if (Integer.parseInt(role) == 1){
            bill_distribution.setAlpha(1);
            meter_bypass_check.setAlpha(1);
        }

        if (Integer.parseInt(role) == 2){
            bill_distribution.setAlpha(1);
            meter_bypass_check.setAlpha(1);

            disconnection_request.setAlpha(1);
            reconnection_request.setAlpha(1);
            tarriff_adjustment_request.setAlpha(1);

            disconnection.setAlpha(1);
            reconnection.setAlpha(1);
            tarriff_adjustment.setAlpha(1);
        }

        if (Integer.parseInt(role) == 3){
            disconnection.setAlpha(1);
            reconnection.setAlpha(1);
            meter_bypass_confirmation.setAlpha(1);
            fault_handling.setAlpha(1);
        }

        if (Integer.parseInt(role) == 4){
            tarriff_adjustment.setAlpha(1);
        }

        if (Integer.parseInt(role) == 7){
            meter_installation.setAlpha(1);
            meter_reading.setAlpha(1);
            grid_meter_reading.setAlpha(1);
        }



        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_home);
        rightMenu = (RelativeLayout) findViewById(R.id.rightMenu);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    public void bill_distribution(View v) {

        if (Integer.parseInt(role) == 1 || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, BillDistribution.class);
            startActivity(changer);
        }
    }

    public void meter_reading(View v) {

        if (Integer.parseInt(role) == 7){
            Intent changer = new Intent(this, MeterReading.class);
            startActivity(changer);
        }
    }

    public void meter_installation(View v) {

        if (Integer.parseInt(role) == 7){
            Intent changer = new Intent(this, MeterInstallation.class);
            startActivity(changer);
        }
    }

    public void disconnection_request(View v) {

        if (Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, DisconnectionRequest.class);
            startActivity(changer);
        }
    }

    public void disconnection(View v) {

        if (Integer.parseInt(role) == 3 || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, Disconnection.class);
            startActivity(changer);
        }
    }

    public void reconnection_request(View v) {

        if (Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, ReconnectionRequest.class);
            startActivity(changer);
        }
    }

    public void reconnection(View v) {

        if (Integer.parseInt(role) == 3 || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, Reconnection.class);
            startActivity(changer);
        }

    }

    public void tarriff_adjustment_request(View v) {

        if (Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, TariffAdjustmentRequest.class);
            startActivity(changer);
        }

    }

    public void tarriff_adjustment(View v) {

        if (Integer.parseInt(role) == 4 || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, TariffAdjustment.class);
            startActivity(changer);
        }

    }

    public void meter_bypass_check(View v) {

        if (Integer.parseInt(role) == 1  || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, MeterBypassCheck.class);
            startActivity(changer);
        }

    }

    public void meter_bypass_confirmation(View v) {

        if (Integer.parseInt(role) == 3){
            Intent changer = new Intent(this, MeterBypassConf.class);
            startActivity(changer);
        }

    }

    public void fault_reporting(View v) {

        //if (Integer.parseInt(role) == 1  || Integer.parseInt(role) == 2){
            Intent changer = new Intent(this, FaultReporting.class);
            startActivity(changer);
        //}

    }

    public void fault_handling(View v) {

        if (Integer.parseInt(role) == 3){
            Intent changer = new Intent(this, FaultHandling.class);
            startActivity(changer);
        }

    }

    public void grid_meter_reading(View v) {

        if (Integer.parseInt(role) == 7){
            Intent changer = new Intent(this, GridMeterReading.class);
            startActivity(changer);
        }

    }

    public void logoutx(View v) {
        //new LogoutUser().execute();
        new LogoutUser().execute();
    }

    public void sdmenu(View v) {
        if (mDrawerLayout.isDrawerOpen(rightMenu)){

            mDrawerLayout.closeDrawer(rightMenu);

        }else if (!mDrawerLayout.isDrawerOpen(rightMenu)){

            mDrawerLayout.openDrawer(rightMenu);

        }
    }

    private class LogoutUser extends AsyncTask<Void, Void, Void> {

        String json_status, json_message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Logging Out...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "logout.php?username="+ staff_id;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_status = jsonObj.getString("status");
                    json_message = jsonObj.getString("msg");

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
            if(json_status.equals("1")){
                session.logoutUser();
            }
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("TAG", json_message);
        }

    }

}
