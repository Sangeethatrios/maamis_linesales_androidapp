package trios.linesales;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdditionalStockReceiptActivity extends AppCompatActivity {
    public static ListView listadditionalstock;
    ArrayList<AdditionalStockDetails> additionalstocklist = new ArrayList<AdditionalStockDetails>();
    public DecimalFormat df = new DecimalFormat("0.00");
    public Context context;
    ImageView goback,additionalstocklistlogout;
    TextView txtvanstockdate,txtvanstock,accept,cancelbtn;
    private int year, month, day;
    private Calendar calendar;
    AdditionalStockBaseAdapter adapter=null;

    boolean deviceFound,networkstate;
    public static String stockdate;
    LinearLayout listbutton;





    Dialog acceptpopup,pindialog;
    Button btnSubmitpin;
    Pinview pinview;
    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_stock_receipt);

        context = this;

        listadditionalstock = (ListView) findViewById(R.id.listadditionalstock);
        additionalstocklistlogout = (ImageView)findViewById(R.id.additionalstocklistlogout);
        goback = (ImageView)findViewById(R.id.goback);
        txtvanstockdate = (TextView)findViewById(R.id.txtvanstockdate);
        txtvanstock = (TextView)findViewById(R.id.txtvanname);
        accept = (TextView)findViewById(R.id.acceptbtn);
        cancelbtn= (TextView)findViewById(R.id.cancelbtn);


        listbutton= (LinearLayout) findViewById(R.id.listbutton);

        listbutton.setVisibility(View.GONE);

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        txtvanstock.setText(preferenceMangr.pref_getString("getvanname"));

        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();
            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
        }



        //Set  Date
        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        /*month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //Call Date Functionality
        showmainDate(year, month+1, day);*/
        stockdate = preferenceMangr.pref_getString("getformatdate");
        txtvanstockdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));


        //Logout process
        additionalstocklistlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity.logoutprocess = "True";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setIcon(R.mipmap.ic_van);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        GetVanStock();
        //
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(additionalstocklist.size() > 0 ) {
                    popup_accept();
//                    acceptpopup = new Dialog(context);
//                    acceptpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    acceptpopup.setContentView(R.layout.additonalstockpopup);
//
//                    TextView txtYesAction = (TextView) acceptpopup.findViewById(R.id.txtYesAction);
//                    TextView txtNoAction = (TextView) acceptpopup.findViewById(R.id.txtNoAction);
//                    txtYesAction.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            popup_accept();
//
//                        }
//                    });
//
//                    txtNoAction.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            acceptpopup.dismiss();
//                        }
//                    });
//                    acceptpopup.setCanceledOnTouchOutside(false);
//                    acceptpopup.setCancelable(false);
//                    acceptpopup.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }



            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Goback process
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });







    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR



        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean isSuccessful(JSONObject object) {
        boolean success = false;
        if (object != null) {
            try {
                String success1 = object.getString("success");
                if (success1.equals("1")) {
                    success = (object.getJSONArray("Value").length()) > 0;
                }
            } catch (JSONException e) {
                Log.d("JSON Error", e.getMessage());
            }
        }
        return success;
    }
    //Checking internet connection
    public boolean isNetworkAvailable() {
        /*ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();*/
        int code;
        Boolean result=false;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL siteURL = new URL(RestAPI.urlString);
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.connect();
            code = connection.getResponseCode();
            if (code == 200) {
                result=true;
            }
            connection.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSync", e.getMessage());
            result=false;

        }
        return result;
    }



    public  void GetVanStock(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
//        accept.setEnabled(false);
        accept.setEnabled(false);
        accept.setBackgroundColor(getResources().getColor(R.color.gray));
        accept.setTextColor(getResources().getColor(R.color.black));


        try{
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                additionalstocklist.clear();
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();

//                        GetAdditionalStockDetails
                jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncadditionalstockreceipt.php", context);
                if (isSuccessful(jsonObj)) {
                    if (jsonObj != null) {
                        JSONArray json_category = null;
                        try {
                            json_category = jsonObj.optJSONArray("Value");
                            if (json_category != null && json_category.length() > 0) {
                                for (int i = 0; i < json_category.length(); i++) {
                                    JSONObject obj = (JSONObject) json_category.get(i);
                                    additionalstocklist.add(new AdditionalStockDetails(obj.getString("itemnametamil"),
                                            obj.getString("qty"), String.valueOf(i + 1), obj.getString("unitname"), obj.getString("colourcode")));
                                }
                                adapter = new AdditionalStockBaseAdapter(context, additionalstocklist);
                                listadditionalstock.setAdapter(adapter);
                                accept.setEnabled(true);

                                accept.setBackgroundColor(getResources().getColor(R.color.green));
                                accept.setTextColor(getResources().getColor(R.color.white));

                                listbutton.setVisibility(View.VISIBLE);
                            } else {

                                listbutton.setVisibility(View.GONE);
                                adapter = new AdditionalStockBaseAdapter(context, additionalstocklist);
                                listadditionalstock.setAdapter(adapter);
                                Toast toast = Toast.makeText(getApplicationContext(), "No Stock Available", Toast.LENGTH_LONG);
                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        } catch (Exception e) {
                            Log.d("JSON Error", e.getMessage());
                        }
                    }

                } else {
                    adapter = new AdditionalStockBaseAdapter(context, additionalstocklist);
                    listadditionalstock.setAdapter(adapter);

                    listbutton.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(getApplicationContext(), "No Stock Available", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            }
            else {
                adapter = new AdditionalStockBaseAdapter(context, additionalstocklist);
                listadditionalstock.setAdapter(adapter);

                listbutton.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//                return;
            }


        }  catch (Exception e){
            Log.i("Stocklist", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }
    //Set date
    private void showmainDate(int year, int monthOfYear, int dayOfMonth) {
        String vardate = "";
        String getmonth="";
        String getdate="";

        if (dayOfMonth < 10) {
            vardate = "0" + dayOfMonth;
            getdate = "0" + dayOfMonth;
        } else {
            vardate = String.valueOf(dayOfMonth);
            getdate = String.valueOf(dayOfMonth);
        }
        if (monthOfYear < 10) {
            vardate = vardate + "-" + "0" + monthOfYear;
            getmonth = "0" + monthOfYear;;
        } else {
            vardate = vardate +"-" + monthOfYear;
            getmonth = String.valueOf(monthOfYear);;
        }
        vardate = vardate + "-" + year;
        stockdate=vardate;
        txtvanstockdate.setText(vardate);

    }



    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }
    public void popup_accept(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        // builder.setIcon(R.mipmap.ic_vanluncher);
        builder.setMessage("Do you want to Accept this stock?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        pindialog = new Dialog(context);
                        pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        pindialog.setContentView(R.layout.validatepinumber);
                        btnSubmitpin = (Button)pindialog.findViewById(R.id.btnSubmitpin);
                        pinview = (Pinview)pindialog.findViewById(R.id.pinview);
                        ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
                        closepopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pindialog.dismiss();
                            }
                        });
                        btnSubmitpin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataBaseAdapter objdatabaseadapter = null;
                                try {

                                    String getEncryptedPIN = null;
                                    try {
                                        getEncryptedPIN = sha256(pinview.getValue());
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {
                                        RestAPI api = new RestAPI();
                                        JSONObject jsonObj = null;
                                        try {
                                            objdatabaseadapter = new DataBaseAdapter(context);
                                            objdatabaseadapter.open();
                                            networkstate = isNetworkAvailable();
                                            if (networkstate == true) {

                                              //  closeKeyboard();
                                                jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "insertadditionalstockreceipt.php", context);

                                                if (jsonObj != null) {
                                                    boolean success = false;
                                                    JSONArray json_category = null;
                                                    try {
                                                        String success1 = jsonObj.getString("success");
                                                        if (success1.equals("1")) {
                                                            pindialog.dismiss();
                                                            closeKeyboard();
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Stock accepted successfully.Please wait until the admin approve this stock", Toast.LENGTH_LONG);
                                                            //toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            GetVanStock();
                                                            finish();
//                                                            GetVanStock();
//                                                            acceptpopup.dismiss();
                                                        }
                                                    } catch (JSONException e) {
                                                        Log.d("JSON Error", e.getMessage());
                                                    }

                                                }
                                            }else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
                                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                return;
                                            }
                                        }
                                        catch (Exception e){
//                                            Toast toast = Toast.makeText(getApplicationContext(), "Please Check Network!", Toast.LENGTH_LONG);
//                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                            toast.show();
//                                            acceptpopup.dismiss();
                                            pindialog.dismiss();
                                            DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                            mDbErrHelper2.open();
                                            mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper2.close();

                                        }

                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin",Toast.LENGTH_LONG);
                                        //toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    }
                                } catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if(objdatabaseadapter!=null)
                                        objdatabaseadapter.close();
                                }
                            }
                        });
                        pindialog.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //Sha Encrypt
    static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    //Close Keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
