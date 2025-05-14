package trios.linesales;

class OrderFormDetails {
    public String itemcode;
    public String itemname;
    public String itemnametamil;
    public String unitweight;
    public String companycode;
    public String colourcode;
    public String unitname;
    public String hsn;
    public String tax;
    public String closingstk;
    public String qty;
    public String sno;
    public String uppweight;
    public String status;

    public String getItemcode() {
        return itemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemnametamil() {
        return itemnametamil;
    }

    public String getUnitweight() {
        return unitweight;
    }

    public String getCompanycode() {
        return companycode;
    }

    public String getColourcode() {
        return colourcode;
    }

    public String getUnitname() {
        return unitname;
    }

    public String getHsn() {
        return hsn;
    }

    public String getTax() {
        return tax;
    }

    public String getClosingstk() {
        return closingstk;
    }

    public String getQty() {
        return qty;
    }

    public String getSno() {
        return sno;
    }

    public String getUppweight() {
        return uppweight;
    }

    public String getStatus() {
        return status;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public OrderFormDetails(String itemcode, String itemname, String itemnametamil, String unitweight,
                            String companycode, String colourcode, String unitname, String hsn, String tax,
                            String closingstk, String qty, String sno,String uppweight,String status) {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.itemnametamil = itemnametamil;
        this.unitweight = unitweight;
        this.companycode = companycode;
        this.colourcode = colourcode;
        this.unitname = unitname;
        this.hsn = hsn;
        this.tax = tax;
        this.closingstk = closingstk;
        this.qty = qty;
        this.sno = sno;
        this.uppweight = uppweight;
        this.status = status;
    }
}
