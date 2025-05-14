package thanjavurvansales.sss;


class CashNotpaidDetails {
    public String transactionno,bookingno,customercode,grandtotal,customernametamil,schedulecode,areanametamil;

    public CashNotpaidDetails(String transactionno, String bookingno, String customercode,
                              String grandtotal, String customernametamil, String schedulecode, String areanametamil) {
        this.transactionno = transactionno;
        this.bookingno = bookingno;
        this.customercode = customercode;
        this.grandtotal = grandtotal;
        this.customernametamil = customernametamil;
        this.schedulecode = schedulecode;
        this.areanametamil = areanametamil;
    }

    public String getTransactionno() {
        return transactionno;
    }

    public void setTransactionno(String transactionno) {
        this.transactionno = transactionno;
    }

    public String getBookingno() {
        return bookingno;
    }

    public void setBookingno(String bookingno) {
        this.bookingno = bookingno;
    }

    public String getCustomercode() {
        return customercode;
    }

    public void setCustomercode(String customercode) {
        this.customercode = customercode;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public void setGrandtotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }

    public String getCustomernametamil() {
        return customernametamil;
    }

    public void setCustomernametamil(String customernametamil) {
        this.customernametamil = customernametamil;
    }

    public String getSchedulecode() {
        return schedulecode;
    }

    public void setSchedulecode(String schedulecode) {
        this.schedulecode = schedulecode;
    }

    public String getAreanametamil() {
        return areanametamil;
    }

    public void setAreanametamil(String areanametamil) {
        this.areanametamil = areanametamil;
    }
}
