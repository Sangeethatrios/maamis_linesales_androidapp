package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UPIPaymentAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] venderid,vendername;

    private TextView txtupivendername;

    private String getpaymentvenderID;

    private  Dialog upipaymentvenderdialog;

    Activity activity;

    UPIPaymentAdapter(Context c, String[] venderid, String[] vendername, TextView txtupivendername,
                      String getpaymentvenderID, Dialog upipaymentvenderdialog, Activity activity) {
        context = c;
        layoutInflater = LayoutInflater.from(context);
        this.venderid = venderid;
        this.vendername = vendername;
        this.txtupivendername = txtupivendername;
        this.getpaymentvenderID = getpaymentvenderID;
        this.upipaymentvenderdialog = upipaymentvenderdialog;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return venderid.length;
    }

    @Override
    public Object getItem(int position) {
        return venderid[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.paymentvenderpopuplist, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.listvendername = (TextView) convertView.findViewById(R.id.listvendername);
            } catch (Exception e) {
                Log.i("Route", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        try {
            mHolder.listvendername.setText(String.valueOf(vendername[position]));
        } catch (Exception e) {
            Log.i("Route value", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtupivendername.setText(String.valueOf(vendername[position]));
                getpaymentvenderID = venderid[position];
                if (activity instanceof SalesListActivity) {
                    SalesListActivity.getpaymentvenderID = venderid[position];
                }
                if (activity instanceof ExpensesActivity) {
                    ExpensesActivity.getpaymentvenderID = venderid[position];
                }
                if (activity instanceof MyScheduleActivity) {
                    MyScheduleActivity.getpaymentvenderID = venderid[position];
                }
                if (activity instanceof SalesOrderListActivity) {
                    SalesOrderListActivity.getpaymentvenderID = venderid[position];
                }
                if (activity instanceof SalesReturnListActivity) {
                    SalesReturnListActivity.getpaymentvenderID = venderid[position];
                }

                upipaymentvenderdialog.dismiss();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView listvendername;

    }

}
