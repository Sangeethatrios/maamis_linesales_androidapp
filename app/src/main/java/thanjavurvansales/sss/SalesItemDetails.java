package thanjavurvansales.sss;

import java.util.Comparator;

public class SalesItemDetails {
    String itemcode; String companycode;  String brandcode; String manualitemcode; String itemname; String itemnametamil;
    String unitcode; String unitweightunitcode; String unitweight; String uppunitcode; String uppweight; String itemcategory;
    String parentitemcode; String allowpriceedit; String allownegativestock; String allowdiscount; String stockqty;
    String unitname;String noofdecimals; String oldprice; String newprice; String colourcode; String hsn; String tax;String itemqty;
    String subtotal;String routeallowpricedit;String discount;String freeflag;String purchaseitemcode;String freeitemcode;
    String dumyprice;String ratecount;String freecount;String applyitemscheme;String applyratescheme;String minsalesqty;
    String upp;String itemtype;String ratediscount;String schemeapplicable;

    public SalesItemDetails(String itemcode, String companycode, String brandcode, String manualitemcode,
                            String itemname, String itemnametamil, String unitcode, String unitweightunitcode,
                            String unitweight, String uppunitcode, String uppweight, String itemcategory,
                            String parentitemcode, String allowpriceedit, String allownegativestock,
                            String allowdiscount, String stockqty, String unitname, String noofdecimals,
                            String oldprice, String newprice, String colourcode, String hsn, String tax,
                            String itemqty,String subtotal,String routeallowpricedit,String discount,String freeflag,
                            String purchaseitemcode,String freeitemcode,String dumyprice,String ratecount,
                            String freecount,String minsalesqty,String upp,String itemtype,String ratediscount,String schemeapplicable) {
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
        this.minsalesqty = minsalesqty;
        this.upp = upp;
        this.itemtype = itemtype;
        this.ratediscount = ratediscount;
        this.schemeapplicable = schemeapplicable;
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

    public String getApplyitemscheme() {
        return applyitemscheme;
    }

    public void setApplyitemscheme(String applyitemscheme) {
        this.applyitemscheme = applyitemscheme;
    }

    public String getApplyratescheme() {
        return applyratescheme;
    }

    public void setApplyratescheme(String applyratescheme) {
        this.applyratescheme = applyratescheme;
    }

    public String getMinsalesqty() {
        return minsalesqty;
    }

    public void setMinsalesqty(String minsalesqty) {
        this.minsalesqty = minsalesqty;
    }

    public String getUpp() {
        return upp;
    }

    public void setUpp(String upp) {
        this.upp = upp;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getratediscount() {
        return ratediscount;
    }

    public void setratediscount(String ratediscount) {
        this.ratediscount = ratediscount;
    }

    public String getschemeapplicable() {
        return schemeapplicable;
    }

    public void setschemeapplicable(String schemeapplicable) {
        this.schemeapplicable = schemeapplicable;
    }

    static class SortbyPurchaseItemDesc implements Comparator<SalesItemDetails>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(SalesItemDetails a, SalesItemDetails b)
        {
            return Integer.parseInt(a.purchaseitemcode) - Integer.parseInt(b.purchaseitemcode);
        }
    }
}
