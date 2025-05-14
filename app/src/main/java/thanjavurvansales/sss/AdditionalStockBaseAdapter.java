package thanjavurvansales.sss;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.Color;
        import android.support.v7.widget.CardView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import java.text.DecimalFormat;
        import java.util.ArrayList;

public class AdditionalStockBaseAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<AdditionalStockDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");

    public AdditionalStockBaseAdapter( Context context,ArrayList<AdditionalStockDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public AdditionalStockDetails getItem(int position) {
        return (AdditionalStockDetails) myList.get(position);
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
            convertView = inflater.inflate(R.layout.additionalstocklist, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.listsno = (TextView) convertView.findViewById(R.id.addtionalsno);
                mHolder.listitemname = (TextView) convertView.findViewById(R.id.addtionalitemname);

                mHolder.listqty = (TextView) convertView. findViewById(R.id.addtionalqty);

                mHolder.additionalstocklist = (LinearLayout) convertView. findViewById(R.id.additionalstocklist);

                mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);

            } catch (Exception e) {
                Log.i("Van stock", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        try {
            AdditionalStockDetails currentListData = getItem(position);
            mHolder.listsno.setText(currentListData.getSno());
            mHolder.listitemname.setText(currentListData.getItemName()+" - "+currentListData.getUOM());

            DecimalFormat df;
//            String getdecimalvalue  = currentListData.getNoofdecimals();
//            String getnoofdigits = "0";
//            if(getdecimalvalue.equals("0")){
//                getnoofdigits = "";
//            }
//            if(getdecimalvalue.equals("1")){
//                getnoofdigits = "0";
//            }
//            if(getdecimalvalue.equals("2")){
//                getnoofdigits = "00";
//            }
//            if(getdecimalvalue.equals("3")){
//                getnoofdigits = "000";
//            }

//            df = new DecimalFormat("0.'"+getnoofdigits+"'");
//            if(getnoofdigits.equals("")) {
//                mHolder.listinward.setText(currentListData.getInward());
//            }else{
//                mHolder.listinward.setText(df.format(Double.parseDouble(currentListData.getInward())));
//            }
//            if(getnoofdigits.equals("")) {
//                mHolder.listoutward.setText(currentListData.getOutward());
//            }else{
//                mHolder.listoutward.setText(df.format(Double.parseDouble(currentListData.getOutward())));
//            }
//            if(getnoofdigits.equals("")) {
//                mHolder.listclosing.setText(currentListData.getClosing());
//            }else{
//                mHolder.listclosing.setText(df.format(Double.parseDouble(currentListData.getClosing())));
//            }
//            if(getnoofdigits.equals("")) {
//                mHolder.listopening.setText(currentListData.getOpening());
//            }else{
//                mHolder.listopening.setText(df.format(Double.parseDouble(currentListData.getOpening())));
//            }
//
//            if(getnoofdigits.equals("")) {
//                mHolder.listsales.setText("\n"+currentListData.getSales());
//            }else{
//                mHolder.listsales.setText("\n"+df.format(Double.parseDouble(currentListData.getSales())));
//            }

            mHolder.listqty.setText(currentListData.getQty());
            mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));


            if (position % 2 == 1) {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
            }
        } catch (Exception e) {
            Log.i("Additional stock value", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        return convertView;
    }
    private class ViewHolder {
        TextView listsno, listitemname,listqty;
        LinearLayout additionalstocklist;
        CardView card_view;

    }
}