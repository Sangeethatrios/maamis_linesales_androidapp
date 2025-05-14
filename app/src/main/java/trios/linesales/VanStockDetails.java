package trios.linesales;

public class VanStockDetails {
    public String sno;
    public String ItemName;
    public String Inward;
    public String Outward;
    public String Closing;
    public String Opening;
    public String UOM;
    public String noofdecimals;
    public String colourcode;
    public String sales;

    public String getSno() {
        return sno;
    }

    public String getClosing() {
        return Closing;
    }

    public String getInward() {
        return Inward;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getOutward() {
        return Outward;
    }

    public String getOpening() {
        return Opening;
    }

    public String getUOM(){return UOM;}

    public String getNoofdecimals(){return noofdecimals;}

    public String getColourcode(){return colourcode;}

    public String getSales(){return sales;}

    public VanStockDetails(String ItemName, String Opening,String Inward,
                           String Outward, String Closing, String sno,String UOM,String noofdecimals,String colourcode,
                           String sales)
    {
        super();
        this.ItemName = ItemName;
        this.Inward = Inward;
        this.Opening = Opening;
        this.Outward = Outward;
        this.Closing = Closing;
        this.sno=sno;
        this.UOM = UOM;
        this.noofdecimals = noofdecimals;
        this.colourcode = colourcode;
        this.sales = sales;

    }
}
