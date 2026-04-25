package trios.linesales;

class PremiumCustomerOrderListDetails {
    public String billcode;
    public String orderdate;
    public String customercode;
    public String customername;
    public String customernametamil;
    public String status;
    public String grandtotal;
    public String sno;
    public String customercity;
    public String flag;
    public String area;
    public String gstinumber;
    public String bookingno;
    public String companyshortname;
    public String transactionno;
    public String financialyearcode;
    public String companycode;
    public String ordertime;

    public String getBillcode() {
        return billcode;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public String getCustomercode() {
        return customercode;
    }

    public String getCustomername() {
        return customername;
    }

    public String getCustomernametamil() {
        return customernametamil;
    }

    public String getStatus() {
        return status;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public String getSno() {
        return sno;
    }

    public String getCustomercity() {
        return customercity;
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

    public String getCompanyshortname() {
        return companyshortname;
    }

    public String getTransactionno() {
        return transactionno;
    }

    public String getFinancialyearcode() {
        return financialyearcode;
    }

    public String getCompanycode() {
        return companycode;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public PremiumCustomerOrderListDetails(String sno, String billcode, String voucherdate, String retailercode,
                                           String retailername, String retailernametamil, String status,
                                           String grandtotal, String retailercity, String flag,
                                           String area, String gstinumber, String bookingno, String companyshortname,
                                           String transactionno, String financialyearcode, String companycode,
                                           String ordertime) {
        this.billcode = billcode;
        this.orderdate = voucherdate;
        this.customercode = retailercode;
        this.customername = retailername;
        this.customernametamil = retailernametamil;
        this.status = status;
        this.grandtotal = grandtotal;
        this.sno = sno;
        this.customercity = retailercity;
        this.flag = flag;
        this.area = area;
        this.gstinumber = gstinumber;
        this.bookingno = bookingno;
        this.companyshortname = companyshortname;
        this.transactionno = transactionno;
        this.financialyearcode = financialyearcode;
        this.companycode = companycode;
        this.ordertime = ordertime;
    }
}