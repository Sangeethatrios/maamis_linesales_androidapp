package trios.linesales;

class SalesReturnListDetails {
    public String billcode;
    public String voucherdate;
    public String retailercode;
    public String retailername;
    public String retailernametamil;
    public String routecode;
    public String routename;
    public String routenametamil;
    public String repcode;
    public String paymenttype;
    public String grandtotal;
    public String sno;
    public String formatvoucherdate;
    public String receiptamount;
    public String vancode;
    public String bitmapimage;
    public String schedulecode;
    public String retailercity;
    public String flag;
    public String area;

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

    public String getRoutecode() {
        return routecode;
    }

    public String getRoutename() {
        return routename;
    }

    public String getRoutenametamil() {
        return routenametamil;
    }

    public String getRepcode() {
        return repcode;
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

    public String getFormatvoucherdate(){return  formatvoucherdate;}

    public String getReceiptamount(){return receiptamount;}

    public String getArea(){return area;}

    public String getVancode(){return vancode;}
    public String getBitmapimage() {return bitmapimage;}
    public String getSchedulecode() {return schedulecode;}
    public String getRetailercity() {return  retailercity;}
    public String getFlag() {return  flag;}
    public SalesReturnListDetails(String billcode, String voucherdate, String retailercode, String retailername,
                            String retailernametamil, String routecode, String routename, String routenametamil,
                            String repcode, String paymenttype, String grandtotal, String sno,String formatvoucherdate,String receiptamount,
                            String vancode,String bitmapimage,String schedulecode,String retailercity,String flag,String area) {
        super();
        this.billcode = billcode;
        this.voucherdate = voucherdate;
        this.retailercode = retailercode;
        this.retailername = retailername;
        this.retailernametamil = retailernametamil;
        this.routecode = routecode;
        this.routename = routename;
        this.routenametamil = routenametamil;
        this.repcode = repcode;
        this.paymenttype = paymenttype;
        this.grandtotal = grandtotal;
        this.sno = sno;
        this.formatvoucherdate = formatvoucherdate;
        this.receiptamount = receiptamount;
        this.vancode = vancode;
        this.bitmapimage =bitmapimage;
        this.schedulecode = schedulecode;
        this.retailercity = retailercity;
        this.flag = flag;
        this.area = area;
    }
}
