package trios.linesales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.multidex.BuildConfig;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static boolean isNullOrEmpty(String description) {
        if(TextUtils.isEmpty(description) || Constants.NULL_VALUE.equalsIgnoreCase(description) ||
                description.trim().length() == 0)
            return true;
        return false;
    }

    public static boolean isServerAvailable(Context context) {
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
        if(!result){
            Toast.makeText(context,"Server not reachable",Toast.LENGTH_SHORT).show();
            return false;
        }

        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(!result){
            Toast.makeText(context,"Please check internet connection",Toast.LENGTH_SHORT).show();
            return false;
        }

        result = isServerAvailable(context);

        return  result;
    }

    public static boolean copyFileUsingChannel(File source, File dest){
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            // Utilities.writeToLogFile(Constants.LOG_ERROR_LEVEL, "Exception while copying file: " + e.getMessage());
        }
        return false;
    }

    @SuppressLint("LongLogTag")
    /*public static  void SharePDFToAll_Above_Android9(Activity activity, Context context, String downloadFileName){
        File sourceFile =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Uri.decode(downloadFileName) );
        //File destinationFile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + File.separator + Uri.decode(downloadFileName));

        //boolean fileCopied =  Utilities.copyFileUsingChannel(sourceFile.getAbsoluteFile(), destinationFile.getAbsoluteFile());

        // if(fileCopied){
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID , new File(sourceFile.getPath()));
        String decodedURIString = Uri.decode(uri.toString());
        Uri decodedURI = Uri.parse(decodedURIString);

        Log.d("URI String : ", uri.toString());
        Log.d("URI String : decodedURIString ", decodedURIString);
        Log.d("URI String : decodedURI ", decodedURI.toString());

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        //intent.setType("files/pdf");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, decodedURI );//Uri.fromFile(new File(file.getPath() + File.separator + Uri.decode(downloadFileName));
        intentShareFile.setDataAndType(decodedURI ,"files/pdf");
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        activity.startActivity(Intent.createChooser(intentShareFile,"Share File"));
    }*/

    public static  void SharePDFToAll_Above_Android9(Activity activity, Context context, String downloadFileName){

        if (downloadFileName.contains("file://"))
            downloadFileName = downloadFileName.replace("file://","");
        File sourceFile =  new File(Uri.decode(downloadFileName));
        Uri uri = FileProvider.getUriForFile(context,  BuildConfig.APPLICATION_ID , new File(sourceFile.getPath()));

        String decodedURIString = Uri.decode(uri.toString());
        Uri decodedURI = Uri.parse(decodedURIString);

        Log.d("URI String : decodedURI ", decodedURI.toString());

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(sourceFile).toString());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.putExtra(Intent.EXTRA_STREAM, decodedURI );
        intentShareFile.setDataAndType(decodedURI ,mimeType);

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        activity.startActivity(Intent.createChooser(intentShareFile,"Share to"));
    }

    /*public static void SharePDFToAll_Below_Android9(Activity activity, String downloadFileName, String decodedPath){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        String extDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File fileInDir = new File( extDirPath +"/"+ downloadFileName);

        if(fileInDir.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(decodedPath));
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            activity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }*/

    public static void SharePDFToAll_Below_Android9(Activity activity, String decodedPath){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileInDir = new File(decodedPath);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(decodedPath)).toString());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if(fileInDir.exists()) {
            intentShareFile.setType(mimeType);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(decodedPath)));
            activity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    public static boolean openWhatsapp(Activity act, String targetPhoneNumber, String template) {
        //https://wa.me/15551234567?text=I'm%20interested%20in%20your%20car%20for%20sale
        String number = getPhoneNumberWithOnlyNumbers(targetPhoneNumber, false);
        if (Utilities.isNullOrEmpty(number)) {
            //Utilities.writeToLogFile(Constants.LOG_ERROR_LEVEL, "Unable to get valid number from " + targetPhoneNumber);
            return false;
        }
        //Utilities.writeToLogFile(Constants.LOG_INFO_LEVEL, "WHATSAPP URL : " + whatsappURL + number);
        redirectLink(act, Constants.whatsAppURL + number+"?text="+template);
        return true;
    }

    public static String getPhoneNumberWithOnlyNumbers(String phone, boolean getOnlyTrailingDigits) {
        if (Utilities.isNullOrEmpty(phone))
            return null;

        StringBuilder sb = new StringBuilder();
        for (char c: phone.toCharArray()) {
            if ((c >= '0' && c <= '9'))
                sb.append(c);
        }

        if (getOnlyTrailingDigits)
            return sb.substring(Math.max(sb.length() - 10, 0));
        else
            return sb.toString();

    }

    public static void redirectLink(Activity activity, String link){
        try {
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            httpIntent.setData(Uri.parse(link));
            activity.startActivity(httpIntent);
        }catch (Exception e){
            //Utilities.displayToastMessage(activity, Utilities.getString(R.string.could_not_redirect_url));
            Toast.makeText(activity.getApplicationContext(), "Could not redirect this URL.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sharePDFToWhatsapp_Below_Android9(Activity activity, String mobileno, Uri filePathURI){
        Intent sendIntent = new Intent("android.intent.action.SEND");
        //File f=new File(filePath);
        //Uri uri = Uri.fromFile(f);
        sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.ContactPicker"));
        sendIntent.setType("application/pdf");
        sendIntent.putExtra(Intent.EXTRA_STREAM, filePathURI);
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+mobileno)+"@s.whatsapp.net");
        activity.startActivity(sendIntent);
    }

    @SuppressLint("LongLogTag")
    public static void sharePDFToWhatsapp_Above_Android9(Activity activity, String mobileno, Uri filePathURI, Context context, String downloadFileName){

        File sourceFile =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Uri.decode(downloadFileName) );

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID , new File(sourceFile.getPath()));
        String decodedURIString = Uri.decode(uri.toString());
        Uri decodedURI = Uri.parse(decodedURIString);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        //intent.setType("files/pdf");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, decodedURI );//Uri.fromFile(new File(file.getPath() + File.separator + Uri.decode(downloadFileName));
        intentShareFile.setDataAndType(decodedURI ,"files/pdf");
        intentShareFile.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+mobileno)+"@s.whatsapp.net");
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.setPackage("com.whatsapp");

        activity.startActivity(intentShareFile);
    }

    public static String getPDFLocalFilePath(Context context,String billdate,String transactionno,String bookingno,
                                             String financialyearcode,String companycode,String billno){
        DataBaseAdapter objdbadapter1 = null;
        String filePath="";
        try{
            objdbadapter1 = new DataBaseAdapter(context);
            objdbadapter1.open();

            filePath = objdbadapter1.GetBillInVoicePDFLocalURL(billdate,
                    transactionno,
                    bookingno,
                    financialyearcode,
                    companycode,
                    billno);

        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), context.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdbadapter1 != null)
                objdbadapter1.close();
        }
        return filePath;
    }
    public static String GetBillInVoicecancelstatus(Context context,String billdate,String transactionno,String bookingno,
                                             String financialyearcode,String companycode,String billno){
        DataBaseAdapter objdbadapter1 = null;
        String filePath="";
        try{
            objdbadapter1 = new DataBaseAdapter(context);
            objdbadapter1.open();

            filePath = objdbadapter1.GetBillInVoicecancelstatus(billdate,
                    transactionno,
                    bookingno,
                    financialyearcode,
                    companycode,
                    billno);

        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), context.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdbadapter1 != null)
                objdbadapter1.close();
        }
        return filePath;
    }
    public static String getEinvoicePath(Context context,String billdate,String transactionno,String bookingno,
                                         String financialyearcode,String companycode,String billno){
        DataBaseAdapter objdbadapter1 = null;
        String einvoiceurl="";
        try{
            objdbadapter1 = new DataBaseAdapter(context);
            objdbadapter1.open();

            einvoiceurl=objdbadapter1.GetBillInVoiceURL(billdate,
                    transactionno,
                    bookingno,
                    financialyearcode,
                    companycode,
                    billno);


        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), context.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdbadapter1 != null)
                objdbadapter1.close();
        }
        return einvoiceurl;
    }
    public static File getExternalStoragePath(Context context){
        File sd = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        return sd;
    }
    public static void CheckDeliveryNoteDialog(Context context){
        String message="Please check your van stock or contact office.";
        AlertDialog.Builder dn_builder = new AlertDialog.Builder(context);
        // dn_builder.setTitle("Alert !");
        TextView title = new TextView(context);
        // You Can Customise your Title here
        title.setText("Alert !");
        title.setBackgroundColor(Color.RED);
        title.setPadding(10, 10, 20, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        dn_builder.setCustomTitle(title);
        dn_builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });
        AlertDialog alert = dn_builder.create();
        alert.show();
    }
    public static String getDeliveryNotePendingCount(String deviceid, String getschedulecode,Context context) {
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        String result="";
        Cursor Cur=null;
        try{
            if(!getschedulecode.equals("") && !getschedulecode.equals("null") && !getschedulecode.equals(null)) {
                jsonObj = api.DeliveryNote(deviceid, "check_deliverynotePending.php", getschedulecode);
                if (isSuccessful(jsonObj)) {
                    JSONArray json_category = jsonObj.getJSONArray("Value");
                    JSONObject obj = (JSONObject) json_category.get(0);
                    result = obj.getString("count");
                }
            }
        }catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    context.getClass().getSimpleName()+" - Check delivery note pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {

        }


            return result;

    }
    public static boolean isSuccessful(JSONObject object) {
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

    @SuppressLint("LongLogTag")
    public static void downloadUPIImage(Context context, ArrayList<Object> downloadUPIData, DropBoxAsyncResponseListener dropboxlistener) {
        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
        try {
            Log.d("DownloadUPI", "Statr IN Background");
            objdatabaseadapter.open();
            boolean networkstate = isServerAvailable();
            Log.d("DownloadUPI", "Network Check");
            if (networkstate == true) {

                Cursor mCur2 = objdatabaseadapter.GetCompanyVenderDetails();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    downloadUPIData = new ArrayList<>();
                    downloadUPIData.add(Constants.KEY_INDEX_0,mCur2.getString(0));
                    downloadUPIData.add(Constants.KEY_INDEX_1,mCur2.getString(1));
                    downloadUPIData.add(Constants.KEY_INDEX_2,mCur2.getString(2));
                    Log.d("DownloadUPI", "downloadUPIData.add");
                    AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context,dropboxlistener,downloadUPIData,Constants.DOWNLOAD_VENDER_IMAGE);
                    asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    Log.d("DownloadUPI", "AsyncTaskClass COmpleted");
                    mCur2.moveToNext();
                }

            }else{
                Toast toast = Toast.makeText(context, "Please check internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }catch (Exception e){
            Log.e("AsyncSyncAllDetails Download UPI Image : " , e.getLocalizedMessage());
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            InsertError(context, e.toString(),"SYNC ALL Post Download UPI Image");
        }
        finally{
            if(objdatabaseadapter!=null){
                objdatabaseadapter.close();
            }
        }
    }

    public static void insertPDF_FilePath_InLocalDB(Context context, ArrayList<Object> downloadUPIData,String downloadedFilePath){
        DataBaseAdapter dataBaseAdapter = null;
        Log.e("UPDATELOCATPATH", String.valueOf(downloadUPIData.size()));
        if(downloadUPIData == null || downloadUPIData.size() < 0)
            return;

        try{

            String companycode = (String) downloadUPIData.get(Constants.KEY_INDEX_0);
            String venderid = (String) downloadUPIData.get(Constants.KEY_INDEX_1);

            dataBaseAdapter = new DataBaseAdapter(context);
            dataBaseAdapter.open();
            dataBaseAdapter.insertImageFile_LocalPath(companycode, venderid, downloadedFilePath);
            dataBaseAdapter.close();
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                    context.getClass().getSimpleName() + " - insert ImageFile In LocalDB ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    @SuppressLint("LongLogTag")
    public static void InsertError(Context context, String geterrror, String methodName){
        try{
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            if(mDbErrHelper == null){
                mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
            }
            if(mDbErrHelper != null){
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), context.getClass().getSimpleName() + methodName, String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }catch (Exception e){
            Log.e("Exception in Insert Error ",e.getLocalizedMessage());
        }
    }
    public static boolean isServerAvailable() {
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

    public static void deleteOrderToSalesConverstionDetails(Context context) {
        PreferenceMangr preferenceMangr = new PreferenceMangr(context);
        preferenceMangr.pref_deleteKey(Constants.KEY_ORDER_TO_SALES_TRANS_NO);
        preferenceMangr.pref_deleteKey(Constants.KEY_ORDER_TO_SALES_FINANCIALYEAR);
        preferenceMangr.pref_deleteKey(Constants.KEY_ORDER_TO_SALES_COMPANYCODE);
    }

    public static boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            //return Environment.isExternalStorageManager();
            return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) { // Android 13
            return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    public static void askStoragePermission(Activity activity, Context context) {
        String[] permissionList = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            /*try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                context.startActivity(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivity(intent);
            }*/
            permissionList = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED};
            ActivityCompat.requestPermissions(activity, permissionList, Constants.STORAGE_PERMISSION_ANDROID_14);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            permissionList = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO};
            ActivityCompat.requestPermissions(activity, permissionList, Constants.STORAGE_PERMISSION_ANDROID_13);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11
            permissionList = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(activity, permissionList, Constants.STORAGE_PERMISSION_ANDROID_11);
        } else {
            permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, permissionList, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public static boolean isPrinterSelectedInApp(Context context) {
        PreferenceMangr preferenceMangr = new PreferenceMangr(context);
        return !Utilities.isNullOrEmpty(preferenceMangr.pref_getString("SelectedPrinterAddress"));
    }
}
