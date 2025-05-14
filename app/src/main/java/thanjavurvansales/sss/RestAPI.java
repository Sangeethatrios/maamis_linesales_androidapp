package thanjavurvansales.sss;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RestAPI {
//test
  //public  static  String urlString = "http://"+LoginActivity.ipaddress+"/linesales/webservice/";
//      public  static  String urlString = "http://"+LoginActivity.ipaddress+"/";
  public  static  String urlString = LoginActivity.ipaddress+"/";
   // public  static  String urlString = "http://"+LoginActivity.ipaddress+"/";
    //public  static  String urlString = "http://192.168.1.46:8080/";
    //122.165.65.120:8090
    public PreferenceMangr preferenceMangr=null;

    public String GetJSONResponse(String paraURL, String paraData) {
        BufferedReader reader = null;
        StringBuffer response = null;
        try {
            URL url = new URL(paraURL);
            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            if (!paraData.equals("")) {
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(paraData);
                wr.flush();
            }
            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            response = new StringBuffer();
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                response.append(line);
            }
        }
        catch (Exception ex) {
            Log.i("LoginException", ex.toString());

        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }
        return response.toString();
    }

    //Get All master from mysql
    public JSONObject GetAllDetails(String paraimeino,String paraphpfile,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");
        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getsalesschedulecode"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
//            Log.i("LoginException",myResponse.getString("success"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject GetOTPapi(String paramobileno,String paraprocess,String paraotp,String paraphpfile,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paramobileno", "UTF-8") + "="
                + URLEncoder.encode(paramobileno, "UTF-8");
        data += "&" +URLEncoder.encode("paraprocess", "UTF-8") + "="
                + URLEncoder.encode(paraprocess, "UTF-8");
        data += "&" +URLEncoder.encode("paraotp", "UTF-8") + "="
                + URLEncoder.encode(paraotp, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
//            Log.i("LoginException",myResponse.getString("success"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get Schedule details from mysql
    public JSONObject GetscheduleDetails(String paraimeino,String parascheduledate,String paraphpfile,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");
        data +="&" +  URLEncoder.encode("parascheduledate", "UTF-8")
                + "=" + URLEncoder.encode(parascheduledate, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject GetOrderStatus(String paraimeino,String paravancode,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(paravancode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject VanStockVerification(String paraimeino,String paraphpfile,String paraschedulecode) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(paraschedulecode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject CheckVanStock(String paraimeino,String paraphpfile,String paravancode) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(paravancode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject DeliveryNote(String paraimeino,String paraphpfile,String paraschedulecode) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(paraschedulecode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }
    public String udfnSyncDetails(String imeino,String syncprocess,String vancode,String schedulecode) throws Exception {
        String url = urlString + "syncdetails.php";
        String myResponse = null;

        // Create data variable for sent values to server

        String data = URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(imeino, "UTF-8");

        data +="&" +  URLEncoder.encode("parasyncprocess", "UTF-8")
                + "=" + URLEncoder.encode(syncprocess, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(vancode, "UTF-8");

        data +="&" +  URLEncoder.encode("pararefcode", "UTF-8")
                + "=" + URLEncoder.encode(schedulecode, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new String(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (Exception ex) {
            Log.i("udfnSyncDetails", ex.toString());
        }

        return myResponse;
    }


    /********************SYNCH FROM SQLLITE*******************/
    //Schedule Details

    public JSONObject ScheduleDetails(String paraschedulearray, Context ctx) throws Exception {
        String url;

        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertschedule.php";

        JSONObject myResponse = null;

        String data = "&" + "paraschedulearray" + "="
                + paraschedulearray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Customer Details
    public JSONObject CustomerDetails(String paracustomerarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertcustomerdetails.php";

        JSONObject myResponse = null;

        String data = "&" + URLEncoder.encode("paracustomerarray", "UTF-8")
                + "=" + URLEncoder.encode(paracustomerarray, "UTF-8");
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("Customer", ex.toString());
        }

        return myResponse;
    }

    //Order Details
    public JSONObject OrderDetails(String paraorderdetails,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertorderdetails.php";

        JSONObject myResponse = null;

        String data = "&" + "paraorderdetails" + "="
                + paraorderdetails;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }
    //Insert Expenses Details
    public JSONObject ExpenseDetails(String paraexpensedetails,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertexpenses.php";

        JSONObject myResponse = null;

        String data = "&" + "paraexpensedetails" + "="
                + paraexpensedetails;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }
    //Insert Receipt Details
    public JSONObject ReceiptDetails(String parareceiptdetails,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertreceipt.php";

        JSONObject myResponse = null;

        String data = "&" + "parareceiptdetails" + "="
                + parareceiptdetails;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ReceiptDetails", ex.toString());
        }

        return myResponse;
    }

    //Delete Expenses Details
    public JSONObject DeleteExpenseDetails(String paraexpensedetails,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "deleteexpenses.php";

        JSONObject myResponse = null;

        String data = "&" + "paraexpensedetails" + "="
                + paraexpensedetails;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }
    //Cancel Receipt Details
    public JSONObject CancelREciptDetails(String parareceiptdetails,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "cancelreceipt.php";

        JSONObject myResponse = null;

        String data = "&" + "receiptarray" + "="
                + parareceiptdetails;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales Details
    public JSONObject SalesDetails(String parasalearray,String parasalesitemarray,String parastockarray,String pararetailerarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertsalesandsalesitems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "salesitem"
                + "=" + parasalesitemarray;
        data +="&" + "stockjson"
                + "=" + parastockarray;
        data +="&" + URLEncoder.encode("retailerjson", "UTF-8")
                + "=" + URLEncoder.encode(pararetailerarray, "UTF-8");
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //All Sales Details
    public JSONObject allSalesDetails(String parasalearray,String parasalesitemarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "all_syncsalesanditems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "salesitem"
                + "=" + parasalesitemarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send dataGG
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }


    //Sales Details
    public JSONObject SalesOrderDetails(String parasalearray,String parasalesitemarray,String pararetailerarray,Context ctx) throws Exception {
        String url="";
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertsalesorderandorderitems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesorderarray" + "="
                + parasalearray;
        data +="&" + "salesorderitem"
                + "=" + parasalesitemarray;
        data +="&" + URLEncoder.encode("retailerjson", "UTF-8")
                + "=" + URLEncoder.encode(pararetailerarray, "UTF-8");
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }


    //Cash report Details
    public JSONObject CashReportDetails(String paracasharray,String paradenominationarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertcashreport.php";

        JSONObject myResponse = null;

        String data = "&" + "paracasharray" + "="
                + paracasharray;
        data +="&" + "paradenominationarray"
                + "=" + paradenominationarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Cash report Details
    public JSONObject CashCloseDetails(String paracasharray,String paraschedulearray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertclosecash.php";

        JSONObject myResponse = null;

        String data = "&" + "paracasharray" + "="
                + paracasharray;
        data +="&" + "paraschedulearray"
                + "=" + paraschedulearray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Cash report Details
    public JSONObject SaveOnlineStatus(String createddate,String synctime,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertonlinestatus.php";
        JSONObject myResponse = null;
        String data = "&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paracreateddate", "UTF-8")
                + "=" + URLEncoder.encode(createddate, "UTF-8");
        data +="&" +  URLEncoder.encode("parasynctime", "UTF-8")
                + "=" + URLEncoder.encode(synctime, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }
        return myResponse;
    }


    //Cash report Details
    public JSONObject SalesCloseDetails(String paracasharray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertsalesclose.php";

        JSONObject myResponse = null;

        String data = "&" + "paracasharray" + "="
                + paracasharray;

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("SalescloseDetails", ex.toString());
        }

        return myResponse;
    }

    //Nil stock details
    public JSONObject NilStockDetails(String paranilstockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertnilstocktransaction.php";

        JSONObject myResponse = null;

        String data = "&" + "paranilstockarray" + "="
                + paranilstockarray;

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("SalescloseDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales Return Details
    public JSONObject SalesReturnDetails(String parasalearray,String parasalesitemarray,String parastockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "insertsalesreturnandsalesreturnitem.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "salesitem"
                + "=" + parasalesitemarray;
        data +="&" + "stockjson"
                + "=" + parastockarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales Details
    public JSONObject SalesReceiptDetails(String parasalearray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "updatesalesreceipt.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }
    //Sales cancel Details
    public JSONObject SalesCancelDetails(String parasalearray, String parasaleitemarray, String parastockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "cancelsalesandsalesitems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "salesitem"
                + "=" + parasaleitemarray;
        data +="&" + "stockjson"
                + "=" + parastockarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales cancel Details
    public JSONObject allSalesCancelDetails(String parasalearray, String parastockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "all_synccancelsales.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "stockjson"
                + "=" + parastockarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales cancel Details
    public JSONObject allSalesStockConversionDetails( String parastockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "all_syncstockconversion.php";

        JSONObject myResponse = null;

        String data = "&" + "stockjson" + "="
                + parastockarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales cancel Details
    public JSONObject SalesOrderCancelDetails(String parasalearray, String parasaleitemarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "cancelsalesorderandorderitems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesorderarray" + "="
                + parasalearray;
        data +="&" + "salesorderitem"
                + "=" + parasaleitemarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Sales Return cancel Details
    public JSONObject SalesReturnCancelDetails(String parasalearray,String parastockarray,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + "cancelsalesreturnandsalesreturnitems.php";

        JSONObject myResponse = null;

        String data = "&" + "salesarray" + "="
                + parasalearray;
        data +="&" + "stockjson"
                + "=" + parastockarray;
        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getvancode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("deviceid"), "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("ScheduleDetails", ex.toString());
        }

        return myResponse;
    }

    //Get Customer based on routecode from mysql
    public JSONObject GetCustomerDetails(String paraimeino,String routecode,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("pararoutecode", "UTF-8")
                + "=" + URLEncoder.encode(routecode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get Customer based on routecode from mysql
    public JSONObject GetCashNotPaidDetails(String paraimeino,String vancode,String routecode,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(vancode, "UTF-8");

        data +="&" +  URLEncoder.encode("pararoutecode", "UTF-8")
                + "=" + URLEncoder.encode(routecode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }
    //Get Max code for financial year
    public JSONObject GetMaxCode(String paraimeino,String financialyearcode,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("parafinancialyearcode", "UTF-8")
                + "=" + URLEncoder.encode(financialyearcode, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    //Get All master from mysql
    public JSONObject CheckIMEI(String paraimeino,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    public JSONObject DownloadPdf(String vancode,String schedulecode,String financialyearcode,String vanname,String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paravancode", "UTF-8") + "="
                + URLEncoder.encode(vancode, "UTF-8");

        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(schedulecode, "UTF-8");

        data +="&" +  URLEncoder.encode("parafinancialyearcode", "UTF-8")
                + "=" + URLEncoder.encode(financialyearcode, "UTF-8");

        data +="&" +  URLEncoder.encode("paravanname", "UTF-8")
                + "=" + URLEncoder.encode(vanname, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("PDFException", ex.toString());
        }

        return myResponse;
    }

    public JSONObject CheckPreviousdayschedule(String paraimeino,String paraphpfile,String paraschedulecode,String paravancode) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");

        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(paraschedulecode, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(paravancode, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }

    public JSONObject DownloadInvoicePdf(String billdate, String transactionno, String bookingno, String financialyearcode,
                                         String companycode,String vancode,String salesandvanname, String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("parabilldate", "UTF-8") + "="
                + URLEncoder.encode(billdate, "UTF-8");

        data +="&" +  URLEncoder.encode("paratransactionno", "UTF-8")
                + "=" + URLEncoder.encode(transactionno, "UTF-8");

        data +="&" +  URLEncoder.encode("parabookingno", "UTF-8")
                + "=" + URLEncoder.encode(bookingno, "UTF-8");

        data +="&" +  URLEncoder.encode("parafinancialyearcode", "UTF-8")
                + "=" + URLEncoder.encode(financialyearcode, "UTF-8");

        data +="&" +  URLEncoder.encode("paracompanycode", "UTF-8")
                + "=" + URLEncoder.encode(companycode, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(vancode, "UTF-8");

        data +="&" +  URLEncoder.encode("paraloggedinuserid", "UTF-8")
                + "=" + URLEncoder.encode(salesandvanname, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("PDFException", ex.toString());
        }

        return myResponse;
    }
    public JSONObject CancelInvoicePdf(String billdate, String transactionno, String bookingno, String financialyearcode,
                                       String companycode,String vancode, String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("parabilldate", "UTF-8") + "="
                + URLEncoder.encode(billdate, "UTF-8");

        data +="&" +  URLEncoder.encode("paratransactionno", "UTF-8")
                + "=" + URLEncoder.encode(transactionno, "UTF-8");

        data +="&" +  URLEncoder.encode("parabookingno", "UTF-8")
                + "=" + URLEncoder.encode(bookingno, "UTF-8");

        data +="&" +  URLEncoder.encode("parafinancialyearcode", "UTF-8")
                + "=" + URLEncoder.encode(financialyearcode, "UTF-8");

        data +="&" +  URLEncoder.encode("paracompanycode", "UTF-8")
                + "=" + URLEncoder.encode(companycode, "UTF-8");

        data +="&" +  URLEncoder.encode("paravancode", "UTF-8")
                + "=" + URLEncoder.encode(vancode, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("PDFException", ex.toString());
        }

        return myResponse;
    }
    //Get All master from mysql
    public JSONObject GetAdditionalStockDetails(String paraimeino,String companycode,String itemsubgroupcode,String itemtype,String paraphpfile,Context ctx) throws Exception {
        String url;
        preferenceMangr = new PreferenceMangr(ctx);
        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("paraimeino", "UTF-8") + "="
                + URLEncoder.encode(paraimeino, "UTF-8");
        data +="&" +  URLEncoder.encode("paraschedulecode", "UTF-8")
                + "=" + URLEncoder.encode(preferenceMangr.pref_getString("getsalesschedulecode"), "UTF-8");
        data +="&" +  URLEncoder.encode("paracompanycode", "UTF-8")
                + "=" + URLEncoder.encode(companycode, "UTF-8");
        data +="&" +  URLEncoder.encode("paraitemsubgroupcode", "UTF-8")
                + "=" + URLEncoder.encode(itemsubgroupcode, "UTF-8");
        data +="&" +  URLEncoder.encode("paraitemtype", "UTF-8")
                + "=" + URLEncoder.encode(itemtype, "UTF-8");
        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("LoginException", ex.toString());
        }

        return myResponse;
    }
    public JSONObject getsalescancelandeinvoice(String billdate, String transactionno, String bookingno, String financialyearcode,
                                         String companycode,String paraimeino, String paraphpfile) throws Exception {
        String url;

        url = urlString + paraphpfile;

        JSONObject myResponse = null;

        String data = "&" +URLEncoder.encode("parabilldate", "UTF-8") + "="
                + URLEncoder.encode(billdate, "UTF-8");

        data +="&" +  URLEncoder.encode("paratransactionno", "UTF-8")
                + "=" + URLEncoder.encode(transactionno, "UTF-8");

        data +="&" +  URLEncoder.encode("parabookingno", "UTF-8")
                + "=" + URLEncoder.encode(bookingno, "UTF-8");

        data +="&" +  URLEncoder.encode("parafinancialyearcode", "UTF-8")
                + "=" + URLEncoder.encode(financialyearcode, "UTF-8");

        data +="&" +  URLEncoder.encode("paracompanycode", "UTF-8")
                + "=" + URLEncoder.encode(companycode, "UTF-8");

        data +="&" +  URLEncoder.encode("paraimeino", "UTF-8")
                + "=" + URLEncoder.encode(paraimeino, "UTF-8");

        // Send data
        try {
            //Read JSON response and print
            myResponse = new JSONObject(GetJSONResponse(url, data));
            //Log.i("LoginException",myResponse.getString("UserID"));
        } catch (JSONException ex) {
            Log.i("EinvoiceException", ex.toString());
        }

        return myResponse;
    }
}
