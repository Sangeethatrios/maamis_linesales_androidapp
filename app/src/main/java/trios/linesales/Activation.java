package trios.linesales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Activation extends AppCompatActivity  {
   Context context;

    Intent varIntent;
    boolean networkstate;
    static String varCompanyname=null;
    static String varusername=null;
    static String varuserid=null;
    static String deviceid = null;
    static String ProductID = "18";
    static String VersionNo = "1.1";
//    static String varOTPURL="http://122.165.65.120/SSSActivationServer/ActivationService.svc/generateOTP";
//    static String varActivationURL="http://122.165.65.120/SSSActivationServer/ActivationService.svc/ProductActivation";
    static String varOTPURL="http://cloud.shivasoftwares.com/activation/AndroidActivationService.svc/generateOTP";
    static String varActivationURL="http://cloud.shivasoftwares.com/activation/AndroidActivationService.svc/ProductActivation";
    String productserverResponse=null;
    String otpserverResponse=null;
    EditText txtCompanyName,txtCity,txtusername,txtMobileNumber,txtEmailAddress,txtOTP,txtPassword,txtconpassword;
    Button btnGetOTP,btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        context=this;
        DataBaseAdapter mDbHelper = new DataBaseAdapter(context);
        mDbHelper.createDatabase();
        txtCompanyName = (EditText) findViewById(R.id.txtCompanyName);
        txtCity = (EditText) findViewById(R.id.txtCity);
        txtusername = (EditText) findViewById(R.id.txtusername);
        txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
        txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
        txtOTP = (EditText) findViewById(R.id.txtOTP);
        btnGetOTP = (Button) findViewById(R.id.btnGetOTP);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        context = this;
        btnRegister.setEnabled(false);


        try{
            TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            if (tm.getDeviceId().equals(null)) {
                deviceid = "000000";
            } else {
                deviceid = tm.getDeviceId();
            }
            udfncheckdevicedetails();
        }
        catch (Exception e)
        {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }


    }

    private void udfncheckdevicedetails(){
        try{
            if(!deviceid.isEmpty()){
                DataBaseAdapter mDbAdapter = new DataBaseAdapter(context);
                mDbAdapter.open();
                Cursor mCur = mDbAdapter.udfngetdevicedetails(deviceid);
                mDbAdapter.close();
                if(mCur.getCount()>0){
                    varusername=mCur.getString(0);
                    varCompanyname=mCur.getString(1);
                    varuserid=deviceid;
                    Intent intent = new Intent(Activation.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
        catch (Exception e)
        {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }
    private  String getRegistrationKey(){
        String androidId=Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)deviceid.hashCode() << 32));

        return deviceUuid.toString();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getOTP(View v)
    {
        try{
            if(TextUtils.isEmpty(txtCompanyName.getText()))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter company name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if(TextUtils.isEmpty(txtCity.getText()))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter city", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if(TextUtils.isEmpty(txtusername.getText()))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter user name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if(TextUtils.isEmpty(txtMobileNumber.getText()))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter mobile number", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if(!txtMobileNumber.getText().toString().equals("") && !txtMobileNumber.getText().toString().equals("null")
                    && !txtMobileNumber.getText().toString().equals(null) ) {
                if (txtMobileNumber.getText().toString().length() < 10) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
            if(!TextUtils.isEmpty(txtEmailAddress.getText()))
            {
                if(!txtEmailAddress.getText().toString().toLowerCase().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid Email ID", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
            else
            {
                txtEmailAddress.setText("-");
            }
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                String varRegistrationKey=getRegistrationKey();


                //udfncallActivationService();
                new GenerateOTP().execute(varRegistrationKey,VersionNo,ProductID);

            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Please turn on your internet", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        catch (Exception e)
        {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    class GenerateOTP extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb=null;
            BufferedReader reader=null;
            String varURL="";
            try {
                varURL=varOTPURL+"/"+ txtCompanyName.getText().toString() +"" +
                        "/"+ txtusername.getText().toString() +"/"+ txtCity.getText().toString() +"/"+txtMobileNumber.getText().toString()+"/"+ txtEmailAddress.getText().toString()+
                        "/"+ params[0] +"/"+ params[1] +"/"+params[2];
                /*varURL=varOTPURL+"?CompanyName="+ txtCompanyName.getText().toString() +"" +
                        "&UserName="+ txtusername.getText().toString() +"&City="+ txtCity.getText().toString() +"&MobileNumber="+txtMobileNumber.getText().toString()+"&EmailID="+ txtEmailAddress.getText().toString()+
                        "&RegistrationKey="+ params[0] +"&VersionNo="+ params[1] +"&ProductID="+params[2];*/
                varURL=varURL.replace(" ","%20");
                URL url = new URL(varURL);
                Log.i("otpurl",varURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(7000);
                connection.setRequestMethod("GET");
                connection.connect();
                int statusCode = connection.getResponseCode();
                //Log.e("statusCode", "" + statusCode);
                if (statusCode == 200) {
                    sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                }

                connection.disconnect();
                if (sb!=null)
                    otpserverResponse=sb.toString();
                else
                    otpserverResponse=null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return otpserverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //All your UI operation can be performed here
            System.out.println(s);
            Log.i("otpseverresponse", "" + s);
          // otpserverResponse="Success";
            if(otpserverResponse!=null){
                if(otpserverResponse.contains("Success")){
                    String varRegistrationKey=getRegistrationKey();

                    DataBaseAdapter objactivationAdaptor = new DataBaseAdapter(context);
                    objactivationAdaptor.open();
                    objactivationAdaptor.insertActivationDetails(txtCompanyName.getText().toString(), txtusername.getText().toString(),
                            txtCity.getText().toString(),txtMobileNumber.getText().toString(),txtEmailAddress.getText().toString(),
                           varRegistrationKey,"Maker",deviceid," ");
                    objactivationAdaptor.close();

                    Toast toast = Toast.makeText(getApplicationContext(),"OTP sent successfully. Please contact service provider", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    btnRegister.setEnabled(true);

                }
                else{

                    Toast toast = Toast.makeText(getApplicationContext(),"OTP not sent. Please try again", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"OTP not sent. Please try again", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public void ProductActivation(View v)
    {
        try{
            if(TextUtils.isEmpty(txtOTP.getText()))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter OTP", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                String varRegistrationKey=getRegistrationKey();


                //udfncallActivationService();
                new ProductActivation().execute(varRegistrationKey,VersionNo);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Please turn on your internet", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        catch (Exception e)
        {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }

    class ProductActivation extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb=null;
            BufferedReader reader=null;
            String varURL;
            try {
                varURL=varActivationURL+"/"+ txtCompanyName.getText().toString() +"" +
                        "/"+ txtusername.getText().toString() +"/"+ txtCity.getText().toString() +"/"+txtMobileNumber.getText().toString()+"/"+ txtEmailAddress.getText().toString()+
                        "/"+ params[0] +"/"+ params[1] +"/"+txtOTP.getText().toString();
                varURL=varURL.replace(" ","%20");
                URL url = new URL(varURL);
                Log.i("activationurl",varURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(7000);
                connection.setRequestMethod("GET");
                connection.connect();
                int statusCode = connection.getResponseCode();
                //Log.e("statusCode", "" + statusCode);
                if (statusCode == 200) {
                    sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                }

                connection.disconnect();
                if (sb!=null)
                    productserverResponse=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return productserverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //All your UI operation can be performed here
            System.out.println(s);
            Log.e("productseverresponse", "" + s);
           // productserverResponse="Success";
            if(productserverResponse!=null){
                if(productserverResponse.contains("Success")){
                    String varRegistrationKey=getRegistrationKey();
                    DataBaseAdapter objactivationAdaptor = new DataBaseAdapter(context);
                    objactivationAdaptor.open();
                    objactivationAdaptor.updateActivationDetails(varRegistrationKey,deviceid,txtOTP.getText().toString());
                    objactivationAdaptor.close();
                    Toast toast = Toast.makeText(getApplicationContext(),"Product is activated successfully", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    udfncheckdevicedetails();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Product is not activated", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Product is not activated", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }


    public void goBack(View v)
    {
        try{
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    Activation.this);
            alert.setTitle("Confirm");
            alert.setMessage("Are you sure want to exit?");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        }
        catch (Exception e)
        {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }

    @Override
    public void onBackPressed()
    {
        goBack(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
                return super.onOptionsItemSelected(item);
    }

}
