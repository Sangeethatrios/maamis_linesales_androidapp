package thanjavurvansales.sss;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class JSONParser {
    public ArrayList<ScheduleDatas> parseScheduleDataList(JSONObject object) {

        ArrayList<ScheduleDatas> varianceDatas = new ArrayList<ScheduleDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray RDjsonObj = null;

            String[] getRDobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("ScheduleTransactionDetails")){
                    RDjsonObj=jsonObj.getJSONArray("ScheduleTransactionDetails");
                    getRDobj = new String[RDjsonObj.length()];
                    for(int j=0;j<RDjsonObj.length();j++){
                        getRDobj[j]=RDjsonObj.getString(j);
                    }
                }else{
                    getRDobj = new String[0];
                }

                varianceDatas.add(new ScheduleDatas(getRDobj));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    //Customer details
    public ArrayList<CustomerDatas> parseCustomerDataList(JSONObject object) {

        ArrayList<CustomerDatas> varianceDatas = new ArrayList<CustomerDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray RDjsonObj = null;

            String[] getRDobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("CustomerTransactionDetails")){
                    RDjsonObj=jsonObj.getJSONArray("CustomerTransactionDetails");
                    getRDobj = new String[RDjsonObj.length()];
                    for(int j=0;j<RDjsonObj.length();j++){
                        getRDobj[j]=RDjsonObj.getString(j);
                    }
                }else{
                    getRDobj = new String[0];
                }

                varianceDatas.add(new CustomerDatas(getRDobj));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<ExpenseDetails> parseExpenseDataList(JSONObject object) {

        ArrayList<ExpenseDetails> varianceDatas = new ArrayList<ExpenseDetails>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray RDjsonObj = null;

            String[] getRDobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("ExpensesTransactionDetails")){
                    RDjsonObj=jsonObj.getJSONArray("ExpensesTransactionDetails");
                    getRDobj = new String[RDjsonObj.length()];
                    for(int j=0;j<RDjsonObj.length();j++){
                        getRDobj[j]=RDjsonObj.getString(j);
                    }
                }else{
                    getRDobj = new String[0];
                }

                varianceDatas.add(new ExpenseDetails(getRDobj));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<ReceiptTransactionDetails> parseReceiptDataList(JSONObject object) {

        ArrayList<ReceiptTransactionDetails> varianceDatas = new ArrayList<ReceiptTransactionDetails>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray RDjsonObj = null;

            String[] getRDobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("ReceiptTransactionDetails")){
                    RDjsonObj=jsonObj.getJSONArray("ReceiptTransactionDetails");
                    getRDobj = new String[RDjsonObj.length()];
                    for(int j=0;j<RDjsonObj.length();j++){
                        getRDobj[j]=RDjsonObj.getString(j);
                    }
                }else{
                    getRDobj = new String[0];
                }

                varianceDatas.add(new ReceiptTransactionDetails(getRDobj));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<ScheduleDatas> parseOrderDataList(JSONObject object) {

        ArrayList<ScheduleDatas> varianceDatas = new ArrayList<ScheduleDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray RDjsonObj = null;

            String[] getRDobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("OrderTransactionDetails")){
                    RDjsonObj=jsonObj.getJSONArray("OrderTransactionDetails");
                    getRDobj = new String[RDjsonObj.length()];
                    for(int j=0;j<RDjsonObj.length();j++){
                        getRDobj[j]=RDjsonObj.getString(j);
                    }
                }else{
                    getRDobj = new String[0];
                }

                varianceDatas.add(new ScheduleDatas(getRDobj));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<SalesSyncDatas> parseSalesDataList(JSONObject object) {

        ArrayList<SalesSyncDatas> varianceDatas = new ArrayList<SalesSyncDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            JSONArray RTjsonObj = null;
            String[] getSTobj;
            String[] getSITobj;
            String[] getRTobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("SalesTransactionDetails")){
                    STjsonObj=jsonObj.getJSONArray("SalesTransactionDetails");
                    getSTobj = new String[STjsonObj.length()];
                    for(int j=0;j<STjsonObj.length();j++){
                        getSTobj[j]=STjsonObj.getString(j);
                    }
                }else{
                    getSTobj = new String[0];
                }
                if(jsonObj.has("SalesItemTransactionDetails")){
                    SITjsonObj=jsonObj.getJSONArray("SalesItemTransactionDetails");
                    getSITobj = new String[SITjsonObj.length()];
                    for(int j=0;j<SITjsonObj.length();j++){
                        getSITobj[j]=SITjsonObj.getString(j);
                    }
                }else{
                    getSITobj = new String[0];
                }
                if(jsonObj.has("SalesStockTransactionDetails")) {
                    RTjsonObj=jsonObj.getJSONArray("SalesStockTransactionDetails");
                    getRTobj = new String[RTjsonObj.length()];
                    for(int j=0;j<RTjsonObj.length();j++){
                        getRTobj[j]=RTjsonObj.getString(j);
                    }
                }else{
                    getRTobj = new String[0];
                }

                varianceDatas.add(new SalesSyncDatas(getSTobj ,getSITobj,getRTobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<ScheduleDatas> parseCashReport(JSONObject object) {

        ArrayList<ScheduleDatas> varianceDatas = new ArrayList<ScheduleDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            String[] getSTobj;
            String[] getSITobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("CashTransactionDetails")){
                    STjsonObj=jsonObj.getJSONArray("CashTransactionDetails");
                    getSTobj = new String[STjsonObj.length()];
                    for(int j=0;j<STjsonObj.length();j++){
                        getSTobj[j]=STjsonObj.getString(j);
                    }
                }else{
                    getSTobj = new String[0];
                }
                if(jsonObj.has("DenominationTransactionDetails")){
                    SITjsonObj=jsonObj.getJSONArray("DenominationTransactionDetails");
                    getSITobj = new String[SITjsonObj.length()];
                    for(int j=0;j<SITjsonObj.length();j++){
                        getSITobj[j]=SITjsonObj.getString(j);
                    }
                }else{
                    getSITobj = new String[0];
                }

                varianceDatas.add(new ScheduleDatas(getSTobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }


    public ArrayList<ScheduleDatas> parseCashCloseReport(JSONObject object) {

        ArrayList<ScheduleDatas> varianceDatas = new ArrayList<ScheduleDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            String[] getSTobj;
            String[] getSITobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("CashTransactionDetails")){
                    STjsonObj=jsonObj.getJSONArray("CashTransactionDetails");
                    getSTobj = new String[STjsonObj.length()];
                    for(int j=0;j<STjsonObj.length();j++){
                        getSTobj[j]=STjsonObj.getString(j);
                    }
                }else{
                    getSTobj = new String[0];
                }
                if(jsonObj.has("ScheduleTransactionDetails")){
                    SITjsonObj=jsonObj.getJSONArray("ScheduleTransactionDetails");
                    getSITobj = new String[SITjsonObj.length()];
                    for(int j=0;j<SITjsonObj.length();j++){
                        getSITobj[j]=SITjsonObj.getString(j);
                    }
                }else{
                    getSITobj = new String[0];
                }

                varianceDatas.add(new ScheduleDatas(getSTobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<ScheduleDatas> parseNilStockReport(JSONObject object) {

        ArrayList<ScheduleDatas> varianceDatas = new ArrayList<ScheduleDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            String[] getSTobj;
            String[] getSITobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);

                if(jsonObj.has("ScheduleTransactionDetails")){
                    SITjsonObj=jsonObj.getJSONArray("ScheduleTransactionDetails");
                    getSITobj = new String[SITjsonObj.length()];
                    for(int j=0;j<SITjsonObj.length();j++){
                        getSITobj[j]=SITjsonObj.getString(j);
                    }
                }else{
                    getSITobj = new String[0];
                }

                varianceDatas.add(new ScheduleDatas(getSITobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }
    public ArrayList<SalesSyncDatas> parseSalesCancelDataList(JSONObject object) {

        ArrayList<SalesSyncDatas> varianceDatas = new ArrayList<SalesSyncDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            JSONArray RTjsonObj = null;
            String[] getSTobj;
            String[] getSITobj=new String[0];
            String[] getRTobj;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("SalesTransactionDetails")){
                    STjsonObj=jsonObj.getJSONArray("SalesTransactionDetails");
                    getSTobj = new String[STjsonObj.length()];
                    for(int j=0;j<STjsonObj.length();j++){
                        getSTobj[j]=STjsonObj.getString(j);
                    }
                }else{
                    getSTobj = new String[0];
                }

                if(jsonObj.has("SalesStockTransactionDetails")) {
                    RTjsonObj=jsonObj.getJSONArray("SalesStockTransactionDetails");
                    getRTobj = new String[RTjsonObj.length()];
                    for(int j=0;j<RTjsonObj.length();j++){
                        getRTobj[j]=RTjsonObj.getString(j);
                    }
                }else{
                    getRTobj = new String[0];
                }

                varianceDatas.add(new SalesSyncDatas(getSTobj ,getSITobj,getRTobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }

    public ArrayList<SalesSyncDatas> parseSalesReceiptDataList(JSONObject object) {

        ArrayList<SalesSyncDatas> varianceDatas = new ArrayList<SalesSyncDatas>();

        try {
            JSONArray jsonArray = object.getJSONArray("TransactionDetails");
            JSONObject jsonObj = null;
            JSONArray STjsonObj = null;
            JSONArray SITjsonObj = null;
            JSONArray RTjsonObj = null;
            String[] getSTobj;
            String[] getSITobj=new String[0];
            String[] getRTobj=new String[0];
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.has("SalesTransactionDetails")){
                    STjsonObj=jsonObj.getJSONArray("SalesTransactionDetails");
                    getSTobj = new String[STjsonObj.length()];
                    for(int j=0;j<STjsonObj.length();j++){
                        getSTobj[j]=STjsonObj.getString(j);
                    }
                }else{
                    getSTobj = new String[0];
                }


                varianceDatas.add(new SalesSyncDatas(getSTobj ,getSITobj,getRTobj));
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("JSONParserTransList", e.getMessage());
        }

        return varianceDatas;
    }
}
