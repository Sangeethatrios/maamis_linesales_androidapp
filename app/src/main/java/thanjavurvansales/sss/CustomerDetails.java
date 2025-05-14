package thanjavurvansales.sss;

public class CustomerDetails {
    public  String customercode;
    public String customername;
    public String customernametamil;
    public String address;
    public String areacode;
    public String mobileno;
    public String telephoneno;
    public String gstin;
    public String areaname;
    public String citycode;
    public String cityname;
    public String emailid;
    public String aadharno;
    public String sno;
    public String customertype;
    public String businesstype;
    public String whatsappno;
    public String mobilenoverificationstatus;
    public CustomerDetails(String customercode, String customername, String customernametamil,
                           String address, String areacode, String mobileno, String telephoneno,
                           String gstin, String areaname,String citycode,String cityname,
                           String emailid,String aadharno, String sno,String customertype, String businesstype,
                           String whatsappno,String mobilenoverificationstatus) {
        this.customercode = customercode;
        this.customername = customername;
        this.customernametamil = customernametamil;
        this.address = address;
        this.areacode = areacode;
        this.mobileno = mobileno;
        this.telephoneno = telephoneno;
        this.gstin = gstin;
        this.areaname = areaname;
        this.citycode = citycode;
        this.cityname = cityname;
        this.emailid = emailid;
        this.aadharno = aadharno;
        this.sno = sno;
        this.customertype = customertype;
        this.businesstype = businesstype;
        this.whatsappno = whatsappno;
        this.mobilenoverificationstatus = mobilenoverificationstatus;
    }

    public String getCustomercode() {
        return customercode;
    }

    public String getCustomername() {
        return customername;
    }

    public String getCustomernametamil() {
        return customernametamil;
    }

    public String getAddress() {
        return address;
    }

    public String getAreacode() {
        return areacode;
    }

    public String getMobileno() {
        return mobileno;
    }

    public String getTelephoneno() {
        return telephoneno;
    }

    public String getGstin() {
        return gstin;
    }

    public String getAreaname() {
        return areaname;
    }

    public String getCitycode() {
        return citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getAadharno() {
        return aadharno;
    }

    public String getSno() {
        return sno;
    }

    public String getCustomertype() {
        return customertype;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public String getwhatsappno() {
        return whatsappno;
    }

    public String getmobilenoverificationstatus() {
        return mobilenoverificationstatus;
    }
}
