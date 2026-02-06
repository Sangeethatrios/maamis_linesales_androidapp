package trios.linesales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AsyncTaskClass extends AsyncTask<Void,Integer,Object> {

    private int requestType, progressValue;
    private Context mContext;
    private Object mDataNeedToPerform;
    private Object idToPerform;
    private DropBoxAsyncResponseListener asyncResponseListener;
    public boolean cancelThread = false;
    ProgressDialog loader = null;

    public AsyncTaskClass(Context context,DropBoxAsyncResponseListener listener,Object mDataNeedToPerform, int reqType){
        this.mContext = context;
        this.requestType = reqType;
        this.asyncResponseListener = listener;
        this.mDataNeedToPerform = mDataNeedToPerform;
    }
    @Override
    protected Object doInBackground(Void... objects) {
        DataBaseAdapter objdatabaseadapter = null;
        switch(requestType){
            case Constants.DOWNLOAD_SALES_BILL_INVOICE:
                return DownloadTaskClass.getInstance().downloadSalesInvoice(mDataNeedToPerform,mContext);
            case Constants.DOWNLOAD_PDF:
                return DownloadTaskClass.getInstance().downloadPdf(mDataNeedToPerform,mContext);
//            case Constants.DOWNLOAD_VENDER_IMAGE:
//                return DownloadTaskClass.getInstance().downloadVendorImage(mDataNeedToPerform,mContext);

            case Constants.DOWNLOAD_VENDER_IMAGE:
                try {
                    objdatabaseadapter = new DataBaseAdapter(mContext);
                    objdatabaseadapter.open();
                    Cursor mCur2 = objdatabaseadapter.GetCompanyVenderDetails();
                    for (int i = 0; i < mCur2.getCount(); i++) {

                        String downloadCompanyID = mCur2.getString(0);
                        String downloadVenderID = mCur2.getString(1);
                        String imageURL = mCur2.getString(2);


                        String downloadFilename = imageURL.split("/")[imageURL.split("/").length - 1];

                        File appDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
                        if (!appDir.exists()) appDir.mkdirs();

                        File targetFile = new File(appDir, downloadFilename);

                        boolean result = downloadImageToAppStorage(mContext, imageURL, targetFile);
                        if (result) {
                            objdatabaseadapter.insertImageFile_LocalPath(downloadCompanyID, downloadVenderID, targetFile.getAbsolutePath());
                        }
                        mCur2.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e("", "Exception in DOWNLOAD_VENDER_IMAGE : " + e.getLocalizedMessage());
                }
                break;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loader = ProgressDialog.show(mContext, "Processing", "Please wait...", true);
        loader.setCancelable(false);
        loader.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(Object result) {
        loader.dismiss();
        super.onPostExecute(result);
        if (asyncResponseListener != null && result != null){
            if((asyncResponseListener instanceof Activity) && !((Activity) asyncResponseListener).isFinishing()){
                asyncResponseListener.onAsyncTaskResponseReceived(result, requestType);
            }else if((asyncResponseListener instanceof Fragment) && !((Fragment) asyncResponseListener).isDetached()){
                asyncResponseListener.onAsyncTaskResponseReceived(result, requestType);
            }else if((asyncResponseListener instanceof android.app.Fragment) && !((android.app.Fragment) asyncResponseListener).isDetached()){
                asyncResponseListener.onAsyncTaskResponseReceived(result, requestType);
            }else{
                asyncResponseListener.onAsyncTaskResponseReceived(result, requestType);
            }
        }
    }

    public static boolean downloadImageToAppStorage(Context context, String imageUrl, File targetFile) {
        try {
            // Create the directory in app-specific storage


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(imageUrl).build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) return false;

            ResponseBody body = response.body();
            if (body == null) return false;


            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            // reduce the image size
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 350, 350, true);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
