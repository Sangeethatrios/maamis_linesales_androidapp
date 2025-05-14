package thanjavurvansales.sss;

public class AdditionalStockDetails {
    public String sno;
    public String ItemName;
    public String Qty;
    public String UOM;
    public String colourcode;



    public String getSno() {
        return sno;
    }

    public String getQty() {
        return Qty;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getUOM(){return UOM;}

    public String getColourcode(){return colourcode;}


    public AdditionalStockDetails(String ItemName, String Qty,
                            String sno,String UOM,String colourcode
                         )
    {
        super();
        this.ItemName = ItemName;
        this.Qty = Qty;
        this.sno=sno;
        this.UOM = UOM;
        this.colourcode = colourcode;


    }
}
