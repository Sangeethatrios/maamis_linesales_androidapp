package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

class SalesListBaseAdapterList extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<SalesListDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");
    public SalesListBaseAdapterList(Context context,ArrayList<SalesListDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public SalesListDetails getItem(int position) {
        return (SalesListDetails) myList.get(position);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sales_list_details, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.saleslistSno = (TextView) convertView.findViewById(R.id.saleslistSno);
                mHolder.saleslistbillno = (TextView) convertView.findViewById(R.id.saleslistbillno);
                mHolder.saleslistretailer = (TextView) convertView.findViewById(R.id.saleslistretailer);
                mHolder.saleslisttotal = (TextView) convertView.findViewById(R.id.saleslisttotal);
                mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                mHolder.salescard_view= (CardView)convertView.findViewById(R.id.salescard_view);
                mHolder.saleslistcity = (TextView)convertView.findViewById(R.id.saleslistcity);
                mHolder.saleslistarea = (TextView)convertView.findViewById(R.id.saleslistarea);
                mHolder.saleslistgstin = (TextView)convertView.findViewById(R.id.saleslistgstin);
                mHolder.saleslistamounttype = (TextView)convertView.findViewById(R.id.saleslistamounttype);
                mHolder.saleslistbookingno = (TextView)convertView.findViewById(R.id.saleslistbookingno);
                mHolder.saleslistpaymenttype = (TextView)convertView.findViewById(R.id.saleslistpaymenttype);
                mHolder.saleslistcompanyname = (TextView)convertView.findViewById(R.id.saleslistcompanyname);
                mHolder.saleslistbillcopyrequired = (TextView)convertView.findViewById(R.id.saleslistbillcopyrequired) ;
                mHolder.saleslisteinvoice = (TextView)convertView.findViewById(R.id.saleslisteinvoice) ;


                convertView.setTag(mHolder);
                convertView.setTag(R.id.saleslistSno, mHolder.saleslistSno);
                convertView.setTag(R.id.saleslistbillno, mHolder.saleslistbillno);
                convertView.setTag(R.id.saleslistretailer, mHolder.saleslistretailer);
                convertView.setTag(R.id.saleslisttotal, mHolder.saleslisttotal);
                convertView.setTag(R.id.listLL, mHolder.listLL);
                convertView.setTag(R.id.salescard_view,mHolder.salescard_view);
                convertView.setTag(R.id.saleslistcity,mHolder.saleslistcity);
                convertView.setTag(R.id.saleslistarea,mHolder.saleslistarea);
                convertView.setTag(R.id.saleslistgstin,mHolder.saleslistgstin);
                convertView.setTag(R.id.saleslistcompanyname,mHolder.saleslistcompanyname);
                convertView.setTag(R.id.saleslistbookingno,mHolder.saleslistbookingno);
                convertView.setTag(R.id.saleslistpaymenttype,mHolder.saleslistpaymenttype);
                convertView.setTag(R.id.saleslistbillcopyrequired,mHolder.saleslistbillcopyrequired);
                convertView.setTag(R.id.saleslisteinvoice,mHolder.saleslisteinvoice);

            } catch (Exception e) {
                Log.i("Sales List Details", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
            convertView.setTag(mHolder);
        }
        try {
            mHolder.saleslistSno.setTag(position);
            mHolder.saleslistbillno.setTag(position);
            mHolder.saleslistretailer.setTag(position);
            mHolder.saleslisttotal.setTag(position);
            mHolder.saleslistcity.setTag(position);
            mHolder.saleslistarea.setTag(position);
            mHolder.saleslistgstin.setTag(position);
            mHolder.saleslistcompanyname.setTag(position);
            mHolder.saleslistbookingno.setTag(position);
            mHolder.saleslistpaymenttype.setTag(position);
            mHolder.saleslistbillcopyrequired.setTag(position);
            mHolder.saleslisteinvoice.setTag(position);



            final SalesListDetails currentListData = getItem(position);

            mHolder.saleslistSno.setText(currentListData.getSno());
            mHolder.saleslistbillno.setText(currentListData.getBillcode());

            mHolder.saleslistretailer.setText(currentListData.getRetailernametamil());
            mHolder.saleslisttotal.setText(dft.format(Double.parseDouble(currentListData.getGrandtotal())));
            mHolder.saleslistcity.setText(currentListData.getRetailercity());
            mHolder.saleslistarea.setText(currentListData.getArea());
            mHolder.saleslistbookingno.setText("BK.No. "+currentListData.getBookingno());
            mHolder.saleslistcompanyname.setText(currentListData.getCompanyshortname());

            if(currentListData.getBillcopystatus().equals("yes")){
                mHolder.saleslistbillcopyrequired.setVisibility(View.VISIBLE);
            }else{
                mHolder.saleslistbillcopyrequired.setVisibility(View.GONE);
            }


            //Flag 3 means cancelled bill
            if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")){
                mHolder.salescard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
                mHolder.saleslistSno.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistbillno.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistretailer.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslisttotal.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistcity.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistarea.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistcompanyname.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistbookingno.setTextColor(context.getResources().getColor(R.color.lightred));
                mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.lightred));

            }

            if(!currentListData.getGstinumber().equals("") && !currentListData.getGstinumber().equals("null") &&
                    !currentListData.getGstinumber().equals(null) && !currentListData.getGstinumber().equals("0")){
                mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.green));
                mHolder.saleslistgstin.setText("GST");
                if(currentListData.getPaymenttype().equals("2")) {
                    if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")) {
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.red));
                    }else{
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.white));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.green));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }else{
                    if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")){
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.lightred));
                    }else {
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.white));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.white));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }
            }else{
                if(currentListData.getPaymenttype().equals("2")){
                   /* mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                    mHolder.saleslistgstin.setText("No GST");*/
                    if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")) {
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.red));
                    }else{
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.white));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.red));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.white));
                    }


                }else{
                    if(currentListData.getFlag().equals("3") || currentListData.getFlag().equals("6")) {
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistgstin.setText("");
                        mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.red));
                    }else{
                        mHolder.saleslistgstin.setBackgroundColor(context.getResources().getColor(R.color.white));
                        mHolder.saleslistgstin.setText("");
                        // mHolder.saleslistpaymenttype.setBackgroundColor(context.getResources().getColor(R.color.white));
                        // mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }

            }
            if(currentListData.getPaymenttype().equals("1")) {
                if ((currentListData.getCashpaidstatus().equals("no"))) {
                    mHolder.saleslistamounttype.setVisibility(View.VISIBLE);
                }
            }
            if(currentListData.getPaymenttype().equals("1")){
                mHolder.saleslistpaymenttype.setText("Cash");
            }else{
                mHolder.saleslistpaymenttype.setText("Credit");
            }
            String filePath = Utilities.getPDFLocalFilePath(context,myList.get(position).getVoucherdate(),
                    myList.get(position).getTransactionno(),
                    myList.get(position).getBookingno(),
                    myList.get(position).getFinancialyearcode(),
                    myList.get(position).getCompanycode(),
                    myList.get(position).getBillcode());
            String einvoicestatus = Utilities.GetBillInVoicecancelstatus(context,myList.get(position).getVoucherdate(),
                    myList.get(position).getTransactionno(),
                    myList.get(position).getBookingno(),
                    myList.get(position).getFinancialyearcode(),
                    myList.get(position).getCompanycode(),
                    myList.get(position).getBillcode());
//            String einvoicepath = Utilities.getEinvoicePath(context,myList.get(position).getVoucherdate(),
//                    myList.get(position).getTransactionno(),
//                    myList.get(position).getBookingno(),
//                    myList.get(position).getFinancialyearcode(),
//                    myList.get(position).getCompanycode(),
//                    myList.get(position).getBillcode());
            //|| !Utilities.isNullOrEmpty(einvoicepath)
            if((!Utilities.isNullOrEmpty(filePath) ) && !myList.get(position).getFlag().equals("3")
                    && !myList.get(position).getFlag().equals("6")){
                mHolder.saleslisteinvoice.setVisibility(View.VISIBLE);
                mHolder.saleslisteinvoice.setBackgroundColor(context.getResources().getColor(R.color.purple));

            }else if(einvoicestatus.equals("2") && (myList.get(position).getFlag().equals("3")
                    || myList.get(position).getFlag().equals("6"))){
                mHolder.saleslisteinvoice.setVisibility(View.VISIBLE);
                mHolder.saleslisteinvoice.setBackgroundColor(context.getResources().getColor(R.color.purple));

            }else{
                mHolder.saleslisteinvoice.setVisibility(View.GONE);
                mHolder.saleslisteinvoice.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

        } catch (Exception e) {
            Log.i("SalesList Exception", e.toString());
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
        TextView saleslistSno, saleslistbillno,  saleslistretailer,
                saleslisttotal,saleslistcity,saleslistarea,saleslistgstin,
                saleslistamounttype,saleslistbookingno,saleslistpaymenttype,saleslistcompanyname,saleslistbillcopyrequired,saleslisteinvoice;
        LinearLayout listLL;
        CardView salescard_view;
    }
    public void calculateTotalAmount() {
        Double addamt = 0.0;
        Double cashamt = 0.0;
        Double creditamt = 0.0;

        try {
            DecimalFormat df = new DecimalFormat("0.00");
            for (int i = 0; i < myList.size(); i++) {
                if(!myList.get(i).getFlag().equals("3")  && !myList.get(i).getFlag().equals("6")) {
                    Double qty = Double.parseDouble(myList.get(i).getGrandtotal());
                    addamt = addamt + qty;
                }
            }

            for (int i = 0; i < myList.size(); i++) {
                if(!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6")
                        && myList.get(i).getPaymenttype().equals("2")) {
                    Double qty = Double.parseDouble(myList.get(i).getGrandtotal());
                    creditamt = creditamt +qty;
                }
            }

            for (int i = 0; i < myList.size(); i++) {
                if(!myList.get(i).getFlag().equals("3")  && !myList.get(i).getFlag().equals("6")
                        && myList.get(i).getPaymenttype().equals("1")) {
                    Double qty = Double.parseDouble(myList.get(i).getGrandtotal());
                    cashamt = cashamt + qty;
                }
            }

            SalesListActivity.totalamtval.setText("\u20B9 "+df.format(addamt));
            SalesListActivity.cashtotalamt.setText("\u20B9 "+df.format(cashamt));
            SalesListActivity.credittotalamt.setText("\u20B9 "+df.format(creditamt));
        } catch (Exception ex) {
            Log.i("Calculate  Exception", ex.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = ex.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
}