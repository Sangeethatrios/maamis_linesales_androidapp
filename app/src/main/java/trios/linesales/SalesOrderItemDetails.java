package trios.linesales;

public class SalesOrderItemDetails {
    String itemcode; String companycode;  String brandcode; String manualitemcode; String itemname; String itemnametamil;
    String unitcode; String unitweightunitcode; String unitweight; String uppunitcode; String uppweight; String itemcategory;
    String parentitemcode; String allowpriceedit; String allownegativestock; String allowdiscount; String stockqty;
    String unitname;String noofdecimals; String oldprice; String newprice; String colourcode; String hsn; String tax;String itemqty;
    String subtotal;String routeallowpricedit;String discount;String freeflag;String purchaseitemcode;String freeitemcode;
    String dumyprice;String ratecount;String freecount;String orderstatus;String maxorderqty;
    public SalesOrderItemDetails(String itemcode, String companycode, String brandcode, String manualitemcode,
                            String itemname, String itemnametamil, String unitcode, String unitweightunitcode,
                            String unitweight, String uppunitcode, String uppweight, String itemcategory,
                            String parentitemcode, String allowpriceedit, String allownegativestock,
                            String allowdiscount, String stockqty, String unitname, String noofdecimals,
                            String oldprice, String newprice, String colourcode, String hsn, String tax,
                            String itemqty,String subtotal,String routeallowpricedit,String discount,String freeflag,
                            String purchaseitemcode,String freeitemcode,String dumyprice,String ratecount,String freecount
                            ,String orderstatus,String maxorderqty) {
        this.itemcode = itemcode;
        this.companycode = companycode;
        this.brandcode = brandcode;
        this.manualitemcode = manualitemcode;
        this.itemname = itemname;
        this.itemnametamil = itemnametamil;
        this.unitcode = unitcode;
        this.unitweightunitcode = unitweightunitcode;
        this.unitweight = unitweight;
        this.uppunitcode = uppunitcode;
        this.uppweight = uppweight;
        this.itemcategory = itemcategory;
        this.parentitemcode = parentitemcode;
        this.allowpriceedit = allowpriceedit;
        this.allownegativestock = allownegativestock;
        this.allowdiscount = allowdiscount;
        this.stockqty = stockqty;
        this.unitname = unitname;
        this.noofdecimals = noofdecimals;
        this.oldprice = oldprice;
        this.newprice = newprice;
        this.colourcode = colourcode;
        this.hsn = hsn;
        this.tax = tax;
        this.itemqty = itemqty;
        this.subtotal = subtotal;
        this.routeallowpricedit = routeallowpricedit;
        this.discount = discount;
        this.freeflag = freeflag;
        this.purchaseitemcode = purchaseitemcode;
        this.freeitemcode = freeitemcode;
        this.dumyprice = dumyprice;
        this.ratecount = ratecount;
        this.freecount = freecount;
        this.orderstatus = orderstatus;
        this.maxorderqty = maxorderqty;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getCompanycode() {
        return companycode;
    }

    public String getBrandcode() {
        return brandcode;
    }

    public String getManualitemcode() {
        return manualitemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemnametamil() {
        return itemnametamil;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public String getUnitweightunitcode() {
        return unitweightunitcode;
    }

    public String getUnitweight() {
        return unitweight;
    }

    public String getUppunitcode() {
        return uppunitcode;
    }

    public String getUppweight() {
        return uppweight;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public String getParentitemcode() {
        return parentitemcode;
    }

    public String getAllowpriceedit() {
        return allowpriceedit;
    }

    public String getAllownegativestock() {
        return allownegativestock;
    }

    public String getAllowdiscount() {
        return allowdiscount;
    }

    public String getStockqty() {
        return stockqty;
    }

    public String getUnitname() {
        return unitname;
    }

    public String getNoofdecimals() {
        return noofdecimals;
    }

    public String getOldprice() {
        return oldprice;
    }

    public String getNewprice() {
        return newprice;
    }

    public String getColourcode() {
        return colourcode;
    }

    public String getHsn() {
        return hsn;
    }

    public String getTax() {
        return tax;
    }

    public String getItemqty() {
        return itemqty;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getRouteallowpricedit() {
        return routeallowpricedit;
    }

    public String getDiscount() {
        return discount;
    }

    public String getFreeflag() {
        return freeflag;
    }

    public String getPurchaseitemcode() {
        return purchaseitemcode;
    }

    public String getFreeitemcode() {
        return freeitemcode;
    }

    public String getDumyprice() {
        return dumyprice;
    }

    public String getRatecount() {
        return ratecount;
    }

    public String getFreecount() {
        return freecount;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public String getMaxorderqty() {
        return maxorderqty;
    }

    public void setItemqty(String itemqty) {
        this.itemqty = itemqty;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public void setNewprice(String newprice) {
        this.newprice = newprice;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setFreeflag(String discount) {
        this.freeflag = freeflag;
    }

    public void setPurchaseitemcode(String purchaseitemcode) { this.purchaseitemcode = purchaseitemcode;    }

    public void setFreeitemcode(String freeitemcode) {
        this.freeitemcode = freeitemcode;
    }

    public void setDumyprice(String dumyqty) {
        this.dumyprice = dumyprice;
    }


}
