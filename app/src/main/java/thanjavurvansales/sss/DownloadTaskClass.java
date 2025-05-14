package thanjavurvansales.sss;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class DownloadTaskClass {

    private static DownloadTaskClass instance = new DownloadTaskClass();
    public String downloadFileName = "";

    public static DownloadTaskClass getInstance() {
        return instance;
    }


    public long downloadSalesInvoice(Object dataList, Context context){

        ArrayList<Object> downloadData = (ArrayList<Object>) dataList;
        long downloadID = 0;
        if(downloadData == null || downloadData.size() <= 0){
            return downloadID;
        }

        String billdate = (String) downloadData.get(Constants.KEY_INDEX_0);
        String transactionno = (String) downloadData.get(Constants.KEY_INDEX_1);
        String bookingno = (String) downloadData.get(Constants.KEY_INDEX_2);
        String financialyearcode = (String) downloadData.get(Constants.KEY_INDEX_3);
        String companycode = (String) downloadData.get(Constants.KEY_INDEX_4);
        String salesandvanname = (String) downloadData.get(Constants.KEY_INDEX_6);
        //String schedulecode = (String) downloadData.get(5);


        //downloadFileName = params[1];
        String downloadURL = "";//(String) downloadData.get(Constants.KEY_INDEX_6);
        String downloadFilename = (String) downloadData.get(Constants.KEY_INDEX_7);
        try {
            String vanname = PreferenceMangr.prefer_getString("getvanname",context);
            vanname =vanname.replace(" ","");
            RestAPI api = new RestAPI();


            JSONObject jsonObj = api.DownloadInvoicePdf(billdate, transactionno, bookingno, financialyearcode, companycode,
                    PreferenceMangr.prefer_getString("getvancode",context),salesandvanname, Constants.KEY_API_DOWNLOAD_EINVOICE);

            if(jsonObj!=null) {
                DataBaseAdapter objdatabaseadapter = null;
                try {
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    String jsonRes=jsonObj.getString("einvoiceresponse").replace("'", "");
                    objdatabaseadapter.updateSalesEinvoice(transactionno, financialyearcode, jsonObj.getInt("status"),
                            jsonObj.getString("path"), jsonObj.getString("Irn"),
                            jsonObj.getString("AckNo"), jsonObj.getString("AckDt"), jsonObj.getString("einvoice_status"),
                            jsonRes,jsonObj.getString("einvoiceqrcodeurl"), companycode);
                   if(jsonObj.getString("status").equals("1")){
                       if(jsonObj.has("downloadURL")) {
                           downloadURL = jsonObj.getString("downloadURL");
                       }
                   }else{
                       Log.w("message",jsonObj.getString("message").replace("'", ""));
                   }

                }catch (Exception e){
                    Log.w("e===>",e.getLocalizedMessage());
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

            }


//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                downloadID = downloadTask_Above_Android9(downloadURL,downloadFilename,context);
            } else {
                downloadID = downloadTask_Below_Android9(downloadURL,downloadFilename,context);
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block

            downloadURL="";
            Log.d("AsyncDownloadPDF", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();

        }


        return downloadID;
    }

    public long downloadPdf(Object dataList, Context context){
        ArrayList<Object> downloadData = (ArrayList<Object>) dataList;
        long downloadID = 0;

        if(downloadData == null || downloadData.size() <= 0){
            return downloadID;
        }
        String downloadURL = (String) downloadData.get(0);
        String downloadFilename = (String) downloadData.get(1);
        downloadFileName = (String) downloadData.get(1);

        String result = "";
        JSONObject jsonObj = null;
        try {
            String vanname = PreferenceMangr.prefer_getString("getvanname",context);
            vanname =vanname.replace(" ","");
            RestAPI api = new RestAPI();

            jsonObj = api.DownloadPdf(PreferenceMangr.prefer_getString("getvancode",context),PreferenceMangr.prefer_getString("getschedulecode",context),
                    PreferenceMangr.prefer_getString("getfinanceyrcode",context),vanname,"downloadpdf.php");

            if(jsonObj!=null) {
                if(jsonObj.has("success")) {
                    result = jsonObj.getString("success");
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                downloadID = downloadTask_Above_Android9(downloadURL,downloadFilename,context);
            } else {
                downloadID = downloadTask_Below_Android9(downloadURL,downloadFilename,context);
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block

            result="";
            Log.d("AsyncDownloadPDF", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();

        }
        return downloadID;

    }

    private long downloadTask_Below_Android9(String downloadURL, String downloadFileName, Context context) throws Exception {
        long downloadID = 0;
        if (!downloadURL.startsWith("http")) {
            return downloadID;
        }

        try {
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

            String extDirPath = "";
            File destDir = null;
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                extDirPath = Environment.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
                destDir = new File( Environment.getExternalStorageDirectory(),"Download");
            } else {*/
            extDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            destDir = new File( extDirPath );
            //}

            if (!destDir.exists())
                destDir.mkdirs();

            File downloadFile = new File(destDir.getAbsolutePath() + File.separator + downloadFileName);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(downloadFile));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (downloadManager != null) {
                downloadID = downloadManager.enqueue(request);
            }

            /*MediaScannerConnection.scanFile(MenuActivity.this, new String[]{downloadFile.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });*/
            //downloadLoading.dismiss();

            //context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Exception e) {
            return 0;
        }
        return downloadID;
    }

    private long downloadTask_Above_Android9(String downloadURL, String downloadFileName, Context context) throws Exception {
        long downloadId=0;
        if (!downloadURL.startsWith("http")) {
            return downloadId;
        }
        //String name = "temp.mcaddon";
        String extDirPath = "";
        File destDir = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            File result = new File(file.getAbsolutePath() + File.separator + downloadFileName);


            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(result));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (downloadManager != null) {
                downloadId = downloadManager.enqueue(request);
            }

        } catch (Exception e) {
            //mToast(this, e.toString());
            return 0;
        }
        return downloadId;
    }
}
