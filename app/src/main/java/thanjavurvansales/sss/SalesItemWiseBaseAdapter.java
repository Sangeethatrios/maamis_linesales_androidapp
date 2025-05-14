package thanjavurvansales.sss;




import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SalesItemWiseBaseAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    public static double total=0.00;
    ArrayList<SalesItemWiseReportDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");
    LinearLayout listLL;
    Dialog lisdetailsdialog;
    ListView setListViewDeails;
    public static TextView tt1;
    public static String itemname;
    public SalesItemWiseBaseAdapter( Context context,ArrayList<SalesItemWiseReportDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public SalesItemWiseReportDetails getItem(int position) {
        return (SalesItemWiseReportDetails) myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.salesitemwisereport, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
                mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                mHolder.listqty = (TextView) convertView. findViewById(R.id.listqty);
                mHolder.listuom = (TextView) convertView. findViewById(R.id.listuom);
                mHolder.listamount = (TextView) convertView. findViewById(R.id.listamount);
                mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                mHolder.LLOutstandingReportList = (LinearLayout) convertView. findViewById(R.id.LLOutstandingReportList);
            } catch (Exception e) {
                Log.i("Route", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
            }
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        try {
            SalesItemWiseReportDetails currentListData = getItem(position);
            mHolder.listsno.setText(currentListData.getSno());
            mHolder.listitemname.setText(currentListData.getItemname());
            double val = Double.parseDouble(currentListData.getQty());
            int getqty = (int) val;
            mHolder.listqty.setText(String.valueOf(getqty));
            mHolder.listuom.setText(currentListData.getUom());
            mHolder.listamount.setText(currentListData.getAmount());

            mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
            if (position % 2 == 1) {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
            }

        } catch (Exception e) {
            Log.i("Route value", e.toString());
//            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//            mDbErrHelper.open();
//            String geterrror = e.toString();
//            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//            mDbErrHelper.close();
        }

        calculateTotalAmount();
        return convertView;
    }
    private class ViewHolder {
        TextView listsno, listitemname,listqty,listamount,listuom;
        LinearLayout LLOutstandingReportList;
        CardView card_view;
    }

    public void calculateTotalAmount() {
        Double addamt = 0.0;
        Double addqty = 0.0;
        try {

            for (int i = 0; i < myList.size(); i++) {
                Double qty = Double.parseDouble(myList.get(i).getAmount());
                addamt = addamt + qty;
            }
            DecimalFormat df = new DecimalFormat("#.00");
            SalesItemWiseReportActivity.itemlisttotalamount.setText("Total  \u20B9 "+df.format(addamt));

           /* for (int j = 0; j < myList.size(); j++) {
                Double qty1 = Double.parseDouble(myList.get(j).getQty());
                addqty = addqty + qty1;
            }
            SalesItemWiseReportActivity.itemlisttotalqty.setText(df.format(addqty));*/
        } catch (Exception ex) {
//            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//            mDbErrHelper.open();
//            String geterrror = ex.toString();
//            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//            mDbErrHelper.close();
        }
    }
}
