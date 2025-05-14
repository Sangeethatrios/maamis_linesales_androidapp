package trios.linesales;

import java.util.Comparator;

class PriceDetails {
    public String itemcode;
    public String itemname;
    public String itemnametamil;
    public String unitcode;
    public String companycode;
    public String colourcode;
    public String unitname;
    public String hsn;
    public String tax;
    public double oldprice;
    public double newprice;
    public double oldorderprice;
    public double neworderprice;
    public String sno;
    public String pricetatus;

    public PriceDetails(String itemcode, String itemname, String itemnametamil, String unitcode,
                        String companycode, String colourcode, String unitname, String hsn, String tax,
                        double oldprice, double newprice, String sno,String pricetatus,
                        double oldorderprice, double neworderprice) {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.itemnametamil = itemnametamil;
        this.unitcode = unitcode;
        this.companycode = companycode;
        this.colourcode = colourcode;
        this.unitname = unitname;
        this.hsn = hsn;
        this.tax = tax;
        this.oldprice = oldprice;
        this.newprice = newprice;
        this.oldorderprice = oldorderprice;
        this.neworderprice = neworderprice;
        this.sno = sno;
        this.pricetatus = pricetatus;
    }

    public String getItemcode() {
        return itemcode;
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

    public double getOldprice() {
        return oldprice;
    }

    public double getNewprice() {
        return newprice;
    }


    public double getOldOrderprice() {
        return oldorderprice;
    }

    public double getNewOrderprice() {
        return neworderprice;
    }


    public String getSno() {
        return sno;
    }

    public String getPricetatus() {
        return pricetatus;
    }

    static class SortbyPriceDesc implements Comparator<PriceDetails>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(PriceDetails a, PriceDetails b)
        {
            return (int) (b.newprice - a.newprice);
        }
    }

    static class SortbyPriceAsc implements Comparator<PriceDetails>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(PriceDetails a, PriceDetails b)
        {
            return (int) (a.newprice - b.newprice);
        }
    }
}
