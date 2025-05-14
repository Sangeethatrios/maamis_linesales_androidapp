package thanjavurvansales.sss;


class ReceiptDetails {
    public String transactionno;
    public String receiptdate;
    public String voucherno;
    public String refno;
    public String companycode;
    public String vancode;
    public String customercode;
    public String schedulecode;
    public String receiptremarkscode;
    public String receiptmode;
    public String chequerefno;
    public String amount;
    public String note;
    public String customernametamil;
    public String areaname;
    public String cityname;
    public String shortname;
    public String flag;
    public String financialyrcode;
    public String sno;

    public ReceiptDetails(String transactionno, String receiptdate, String voucherno, String refno, String companycode,
                          String vancode, String customercode, String schedulecode, String receiptremarkscode,
                          String receiptmode, String chequerefno, String amount, String note,
                          String customernametamil, String areaname, String cityname, String shortname,
                          String flag,String financialyrcode, String sno) {
        this.transactionno = transactionno;
        this.receiptdate = receiptdate;
        this.voucherno = voucherno;
        this.refno = refno;
        this.companycode = companycode;
        this.vancode = vancode;
        this.customercode = customercode;
        this.schedulecode = schedulecode;
        this.receiptremarkscode = receiptremarkscode;
        this.receiptmode = receiptmode;
        this.chequerefno = chequerefno;
        this.amount = amount;
        this.note = note;
        this.customernametamil = customernametamil;
        this.areaname = areaname;
        this.cityname = cityname;
        this.shortname = shortname;
        this.flag = flag;
        this.financialyrcode = financialyrcode;
        this.sno = sno;
    }

    public String getTransactionno() {
        return transactionno;
    }

    public String getReceiptdate() {
        return receiptdate;
    }

    public String getVoucherno() {
        return voucherno;
    }

    public String getRefno() {
        return refno;
    }

    public String getCompanycode() {
        return companycode;
    }

    public String getVancode() {
        return vancode;
    }

    public String getCustomercode() {
        return customercode;
    }

    public String getSchedulecode() {
        return schedulecode;
    }

    public String getReceiptremarkscode() {
        return receiptremarkscode;
    }

    public String getReceiptmode() {
        return receiptmode;
    }

    public String getChequerefno() {
        return chequerefno;
    }

    public String getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getCustomernametamil() {
        return customernametamil;
    }

    public String getAreaname() {
        return areaname;
    }

    public String getCityname() {
        return cityname;
    }

    public String getShortname() {
        return shortname;
    }

    public String getFlag() {
        return flag;
    }

    public String getFinancialyrcode(){
        return  financialyrcode;
    }

    public String getSno() {
        return sno;
    }
}

