package trios.linesales;

class SalesItemListDetails {
    String itemcode;String itemname;String qty;
    String weight;String price;String discount;String amount;String freeitemstatus;
    String unitname;String hsn;String tax;String noofdecimals;String colourcode;

    public SalesItemListDetails(String itemcode, String itemname, String qty, String weight,
                                String price, String discount, String amount, String freeitemstatus,
                                String unitname, String hsn, String tax,String noofdecimals,String colourcode) {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.qty = qty;
        this.weight = weight;
        this.price = price;
        this.discount = discount;
        this.amount = amount;
        this.freeitemstatus = freeitemstatus;
        this.unitname = unitname;
        this.hsn = hsn;
        this.tax = tax;
        this.noofdecimals = noofdecimals;
        this.colourcode=colourcode;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public String getQty() {
        return qty;
    }

    public String getWeight() {
        return weight;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getAmount() {
        return amount;
    }

    public String getFreeitemstatus() {
        return freeitemstatus;
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
    public String getNoofdecimals(){
        return  noofdecimals;
    }
    public String getColourcode(){
        return colourcode;
    }
}
