package thanjavurvansales.sss;

class SalesListDetails {
    public String billcode;
    public String voucherdate;
    public String retailercode;
    public String retailername;
    public String retailernametamil;
    public String paymenttype;
    public String grandtotal;
    public String sno;
    public String schedulecode;
    public String retailercity;
    public String flag;
    public String area;
    public String gstinumber;
    public String bookingno;
    public String companyshortname;
    public String cashpaidstatus;
    public String billcopystatus;
    public String transactionno;
    public String financialyearcode;
    public String companycode;
    public int syncstatus;

    public String getBillcode() {
        return billcode;
    }

    public String getVoucherdate() {
        return voucherdate;
    }

    public String getRetailercode() {
        return retailercode;
    }

    public String getRetailername() {
        return retailername;
    }

    public String getRetailernametamil() {
        return retailernametamil;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public String getSno() {
        return sno;
    }

    public String getSchedulecode() {
        return schedulecode;
    }

    public String getRetailercity() {
        return retailercity;
    }

    public String getFlag() {
        return flag;
    }

    public String getArea() {
        return area;
    }
    public String getGstinumber() {
        return gstinumber;
    }
    public String getBookingno() {
        return bookingno;
    }
    public String getCompanyshortname(){
        return  companyshortname;
    }

    public String getCashpaidstatus(){
        return  cashpaidstatus;
    }

    public String getBillcopystatus(){
        return  billcopystatus;
    }
    public String getTransactionno(){
        return  transactionno;
    }
    public String getFinancialyearcode(){return  financialyearcode;}
    public String getCompanycode(){return  companycode;}

    public int getSyncstatus() {
        return syncstatus;
    }

    public void setSyncstatus(int syncstatus) {
        this.syncstatus = syncstatus;
    }

    public SalesListDetails(String billcode, String voucherdate, String retailercode,
                            String retailername, String retailernametamil, String paymenttype,
                            String grandtotal, String sno, String schedulecode, String retailercity, String flag,
                            String area, String gstinumber, String bookingno,
                            String companyshortname, String billcopystatus, String cashpaidstatus,
                            String transactionno, String financialyearcode, String companycode,int syncstatus) {
        this.billcode = billcode;
        this.voucherdate = voucherdate;
        this.retailercode = retailercode;
        this.retailername = retailername;
        this.retailernametamil = retailernametamil;
        this.paymenttype = paymenttype;
        this.grandtotal = grandtotal;
        this.sno = sno;
        this.schedulecode = schedulecode;
        this.retailercity = retailercity;
        this.flag = flag;
        this.area = area;
        this.gstinumber = gstinumber;
        this.bookingno = bookingno;
        this.companyshortname = companyshortname;
        this.billcopystatus = billcopystatus;
        this.cashpaidstatus = cashpaidstatus;
        this.transactionno = transactionno;
        this.financialyearcode = financialyearcode;
        this.companycode = companycode;
        this.syncstatus=syncstatus;

    }
}
