package trios.linesales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

class ReceiptListBaseAdapter  extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ReceiptDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");


    public ReceiptListBaseAdapter( Context context,ArrayList<ReceiptDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public ReceiptDetails getItem(int position) {
        return (ReceiptDetails) myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if(getCount() > 0){
            return getCount();
        }else{
            return super.getViewTypeCount();
        }
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
            convertView = inflater.inflate(R.layout.receipt_list, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.receiptlistSno = (TextView) convertView.findViewById(R.id.receiptlistSno);
                mHolder.receiptlistno = (TextView) convertView.findViewById(R.id.receiptlistno);
                mHolder.receiptlistdate = (TextView) convertView.findViewById(R.id.receiptlistdate);
                mHolder.receiptlistcustomername = (TextView) convertView.findViewById(R.id.receiptlistcustomername);
                mHolder.receiptlistreceiptmode = (TextView) convertView.findViewById(R.id.receiptlistreceiptmode);
                mHolder.receiptlisttotal = (TextView) convertView.findViewById(R.id.receiptlisttotal);
                mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                mHolder.receiptcard_view = (CardView)convertView.findViewById(R.id.receiptcard_view);
                mHolder.receiptlistshortname = (TextView) convertView.findViewById(R.id.receiptlistshortname);
                mHolder.receiptlistarea = (TextView)convertView.findViewById(R.id.receiptlistarea);


                convertView.setTag(mHolder);
                convertView.setTag(R.id.receiptlistSno, mHolder.receiptlistSno);
                convertView.setTag(R.id.receiptlistno, mHolder.receiptlistno);
                convertView.setTag(R.id.receiptlistdate, mHolder.receiptlistdate);
                convertView.setTag(R.id.receiptlistcustomername, mHolder.receiptlistcustomername);
                convertView.setTag(R.id.receiptlistreceiptmode, mHolder.receiptlistreceiptmode);
                convertView.setTag(R.id.receiptlisttotal, mHolder.receiptlisttotal);
                convertView.setTag(R.id.listLL, mHolder.listLL);
                convertView.setTag(R.id.receiptlistshortname, mHolder.receiptlistshortname);
                convertView.setTag(R.id.receiptcard_view, mHolder.receiptcard_view);
                convertView.setTag(R.id.receiptlistarea,mHolder.receiptlistarea);

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


            ReceiptDetails currentListData = getItem(position);

            mHolder.receiptlistSno.setText(currentListData.getSno());
            mHolder.receiptlistno.setText(currentListData.getVoucherno());
            mHolder.receiptlistdate.setText(currentListData.getReceiptdate());
            mHolder.receiptlistcustomername.setText(currentListData.getCustomernametamil());
            mHolder.receiptlistreceiptmode.setText(currentListData.getReceiptmode() );
            mHolder.receiptlisttotal.setText(dft.format(Double.parseDouble(currentListData.getAmount())));
            mHolder.receiptlistshortname.setText(currentListData.getShortname());
            mHolder.receiptlistarea.setText(currentListData.getAreaname() +" - "+currentListData.getCityname());

            mHolder.receiptlistSno.setTag(position);
            mHolder.receiptlistno.setTag(position);
            mHolder.receiptlistdate.setTag(position);
            mHolder.receiptlistreceiptmode.setTag(position);
            mHolder.receiptlistcustomername.setTag(position);
            mHolder.receiptlisttotal.setTag(position);
            mHolder.receiptlistshortname.setTag(position);
            mHolder.receiptlistarea.setTag(position);

            mHolder.receiptcard_view.setTag(position);

          /*  if (position % 2 == 1) {
                mHolder.receiptcard_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                mHolder.receiptcard_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
            }*/
            if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")){
                mHolder.receiptcard_view.setCardBackgroundColor(Color.parseColor("#E6E5E5"));
                mHolder.receiptlistSno.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistno.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistdate.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistreceiptmode.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistcustomername.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlisttotal.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistshortname.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.receiptlistarea.setTextColor(context.getResources().getColor(R.color.lightred));
            }

        } catch (Exception e) {
            Log.i("Route value", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        calculateTotalAmount();


        return convertView;
    }
    private class ViewHolder {
        TextView receiptlistSno,receiptlistdate, receiptlistreceiptmode,
                receiptlistcustomername, receiptlisttotal,receiptlistno,receiptlistshortname,receiptlistarea;
        LinearLayout listLL;
        CardView receiptcard_view;

    }
    public void calculateTotalAmount() {
        Double addamt = 0.0;
        Double cashamt=0.0;
        Double chequeamt=0.0;
        Double upiamt=0.0;
        try {

            for (int i = 0; i < myList.size(); i++) {
                if(!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6")) {
                    Double amt = Double.parseDouble(myList.get(i).getAmount());
                    addamt = addamt + amt;

                    if (myList.get(i).getReceiptmode().equals("Cash")) {
                        Double camt = Double.parseDouble(myList.get(i).getAmount());
                        cashamt = cashamt + camt;
                    }
                    if (myList.get(i).getReceiptmode().equals("Cheque")) {
                        Double chamt = Double.parseDouble(myList.get(i).getAmount());
                        chequeamt = chequeamt + chamt;
                    }
                    if (myList.get(i).getReceiptmode().equals("UPI")) {
                        Double chamt = Double.parseDouble(myList.get(i).getAmount());
                        upiamt = upiamt + chamt;
                    }

                }
            }
            DecimalFormat df = new DecimalFormat("#0.00");
            ReceiptActivity.receiptsubtotalamt.setText("\u20B9 "+df.format(addamt));
            ReceiptActivity.cashtotalamt.setText("\u20B9 "+df.format(cashamt));
            ReceiptActivity.chequetotalamt.setText("\u20B9 "+df.format(chequeamt));
            ReceiptActivity.upitotalamt.setText("\u20B9 "+df.format(upiamt));
        } catch (Exception ex) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = ex.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
}

