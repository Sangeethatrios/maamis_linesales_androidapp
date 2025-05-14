package trios.linesales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.fragment.app.Fragment;

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

        switch(requestType){
            case Constants.DOWNLOAD_SALES_BILL_INVOICE:
                return DownloadTaskClass.getInstance().downloadSalesInvoice(mDataNeedToPerform,mContext);
            case Constants.DOWNLOAD_PDF:
                return DownloadTaskClass.getInstance().downloadPdf(mDataNeedToPerform,mContext);
            case Constants.DOWNLOAD_VENDER_IMAGE:
                return DownloadTaskClass.getInstance().downloadVendorImage(mDataNeedToPerform,mContext);
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

}
