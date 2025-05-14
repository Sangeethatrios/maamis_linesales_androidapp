package trios.linesales;

public class SalesItemWiseReportDetails {
    public String sno;
    public String itemcode;
    public String itemname;
    public String qty;
    public String uom;
    public String amount;
    public String colourcode;

    public String getUom() {
        return uom;
    }

    public String getSno() {
        return sno;
    }

    public String getAmount() {
        return amount;
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

    public String getColourcode(){return colourcode; }

    public SalesItemWiseReportDetails(String itemcode, String itemname, String qty,String uom,
                                      String amount, String sno, String colourcode)
    {
        super();
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.qty = qty;
        this.uom = uom;
        this.amount = amount;
        this.sno=sno;
        this.colourcode = colourcode;
    }
}
