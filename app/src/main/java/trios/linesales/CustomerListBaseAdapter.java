package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

class CustomerListBaseAdapter  extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<CustomerDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");

    public CustomerListBaseAdapter(Context context, ArrayList<CustomerDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public CustomerDetails getItem(int position) {
        return (CustomerDetails) myList.get(position);
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

      final  ViewHolder mHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customerlist, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.RetailerlistSno = (TextView) convertView.findViewById(R.id.RetailerlistSno);
                /* mHolder.Retailerlistretailercode = (TextView) convertView.findViewById(R.id.Retailerlistretailercode);*/
                mHolder.Retailerlistretailername = (TextView) convertView.findViewById(R.id.Retailerlistretailername);
                mHolder.Retailercity = (TextView) convertView.findViewById(R.id.Retailercity);
                mHolder.Retailermobileno = (TextView) convertView.findViewById(R.id.Retailermobileno);
                mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                mHolder.retailercard_view = (CardView)convertView.findViewById(R.id.retailercard_view);
                mHolder.Retailergstin = (TextView)convertView.findViewById(R.id.Retailergstin);
                mHolder.RetailerArea = (TextView)convertView.findViewById(R.id.RetailerArea);
                mHolder.Retailertelephoneno = (TextView)convertView.findViewById(R.id.Retailertelephoneno);
                mHolder.Verifiedstatus =(TextView)convertView.findViewById(R.id.Verifiedstatus);


                convertView.setTag(mHolder);
                convertView.setTag(R.id.RetailerlistSno, mHolder.RetailerlistSno);
                convertView.setTag(R.id.Retailerlistretailername, mHolder.Retailerlistretailername);
                convertView.setTag(R.id.Retailercity, mHolder.Retailercity);
                convertView.setTag(R.id.Retailermobileno, mHolder.Retailermobileno);
                convertView.setTag(R.id.listLL, mHolder.listLL);
                convertView.setTag(R.id.retailercard_view,mHolder.retailercard_view);
                convertView.setTag(R.id.Retailergstin,mHolder.Retailergstin);
                convertView.setTag(R.id.RetailerArea,mHolder.RetailerArea);
                convertView.setTag(R.id.Retailertelephoneno,mHolder.Retailertelephoneno);
                convertView.setTag(R.id.Verifiedstatus,mHolder.Verifiedstatus);

                mHolder.Retailermobileno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                          if(!mHolder.Retailermobileno.getText().toString().equals("")) {

                                Toast toast = Toast.makeText(context,"Making Call", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:"+mHolder.Retailermobileno.getText().toString().trim()));
                                context.startActivity(callIntent);
                            }else{
                                Toast toast = Toast.makeText(context,"unable to call", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                    }
                });
            } catch (Exception e) {
                Log.i("Customer", e.toString());
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

            CustomerDetails currentListData = getItem(position);
            mHolder.RetailerlistSno.setText(currentListData.getSno());
            if(!currentListData.getCustomernametamil().equals("") && !currentListData.getCustomernametamil().equals("null")
                && !currentListData.getCustomernametamil().equals(null) ) {
                mHolder.Retailerlistretailername.setText(currentListData.getCustomernametamil());
            }else{
                mHolder.Retailerlistretailername.setText(currentListData.getCustomername());
            }
            mHolder.RetailerArea.setText(currentListData.getAreaname());
            mHolder.Retailercity.setText(currentListData.getCityname() );
            if(currentListData.getMobileno().equals("null")){
                mHolder.Retailermobileno.setText("");
            }else{
                mHolder.Retailermobileno.setText(currentListData.getMobileno());
            }

            if(!currentListData.getTelephoneno().equals("") && !currentListData.getTelephoneno().equals("null")) {
                mHolder.Retailertelephoneno.setText(currentListData.getTelephoneno());
                mHolder.Retailertelephoneno.setVisibility(View.VISIBLE);
            }else{
                mHolder.Retailertelephoneno.setText("");
                mHolder.Retailertelephoneno.setVisibility(View.GONE);
            }
            if(currentListData.getmobilenoverificationstatus().equals("1")){
                mHolder.Verifiedstatus.setVisibility(View.VISIBLE);
            }else{
                mHolder.Verifiedstatus.setVisibility(View.GONE);
            }



            if(!currentListData.getGstin().equals("") && !currentListData.getGstin().equals("null")) {
                mHolder.Retailergstin.setText("GSTIN : "+currentListData.getGstin());
            }else{
                mHolder.Retailergstin.setText("");
            }


            mHolder.RetailerlistSno.setTag(position);
            mHolder.Retailerlistretailername.setTag(position);
            mHolder.Retailercity.setTag(position);
            mHolder.Retailermobileno.setTag(position);
            mHolder.Retailergstin.setTag(position);
            mHolder.RetailerArea.setTag(position);
            mHolder.Retailertelephoneno.setTag(position);
            mHolder.Verifiedstatus.setTag(position);


            if (position % 2 == 1) {
                mHolder.retailercard_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                mHolder.retailercard_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
            }

        } catch (Exception e) {
            Log.i("Customer value", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        return convertView;
    }
    private class ViewHolder {
        TextView RetailerlistSno, RetailerArea
                ,Retailergstin,Retailerlistretailername,Retailercity,Retailermobileno,Retailertelephoneno,Verifiedstatus;
        LinearLayout listLL;
        CardView retailercard_view;

    }
}
