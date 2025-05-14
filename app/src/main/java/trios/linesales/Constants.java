package trios.linesales;

public class Constants {

    public static final String NULL_VALUE="null";
    public static final int CUSTOMER_CATEGORY_SALES =1;
    public static final int CUSTOMER_CATEGORY_ORDER =2;
    public static final int CUSTOMER_CATEGORY_BOTH =3;
    public static final int CUSTOMER_CATEGORY_RECEIPT =4; //for receipt
    public static final int DOWNLOAD_SALES_BILL_INVOICE =1;
    public static final int DOWNLOAD_PDF=2;
    public static final int DOWNLOAD_VENDER_IMAGE=3;

    public static final String KEY_API_DOWNLOAD_EINVOICE="Einvoice.php";
    public static final int KEY_INDEX_0 = 0;
    public static final int KEY_INDEX_1 = 1;
    public static final int KEY_INDEX_2 = 2;
    public static final int KEY_INDEX_3 = 3;
    public static final int KEY_INDEX_4 = 4;
    public static final int KEY_INDEX_5 = 5;
    public static final int KEY_INDEX_6 = 6;
    public static final int KEY_INDEX_7 = 7;
    public static final int KEY_INDEX_8 = 8;


    public static final String DOWNLOAD_UPLOAD_Einvoice="downloadorupload";
    public static final String Share_Einvoice="share";

    public static final String whatsAppURL = "https://wa.me/";
    public static final String KEY_MENU_ITEM_VIEW="View";
    public static final String KEY_MENU_ITEM_EDIT="Edit";
    public static final String KEY_MENU_ITEM_UPLOAD="Upload";
    public static final String KEY_MENU_ITEM_DOWNLOAD="Download";
    public static final String KEY_MENU_ITEM_SHARE="Share";

    public static final String KEY_ORDER_TO_SALES_TRANS_NO="orderTransNo";
    public static final String KEY_ORDER_TO_SALES_FINANCIALYEAR="orderFinancialyear";
    public static final String KEY_ORDER_TO_SALES_COMPANYCODE="orderCompanycode";
    public static final String KEY_GET_MENU_SCHEDULECODE = "menu_getschedulecode";
    public static final String KEY_GET_MENU_ROUTECODE = "menu_getroutecode";
    public static final String KEY_GETAREACODE = "getareacode";
    public static final String KEY_GETVANCODE = "getvancode";
    public static final String KEY_GETBUSINESSTYPE = "getbusinesstype";


    //tblnotpurchase
    public static final String TBL_NOT_PURCHASED ="tblnotpurchased";
    public static final String TBL_COLUMN_BILLDATE ="billdate";

    public static final String TBL_COLUMN_CUSTOMERCODE="customercode";
    public static final String TBL_COLUMN_FINANCIALYEARCODE="financialyearcode";
    public static final String TBL_COLUMN_VANCODE="vancode";
    public static final String TBL_COLUMN_AUTONUM="autonum";
    public static final String TBL_COLUMN_SCHEDULECODE="schedulecode";
    public static final String TBL_COLUMN_CREATEDDATE="createddate";
    public static final String TBL_COLUMN_UPDATEDDATE="updateddate";
    public static final String TBL_COLUMN_REMARKS="remarks";
    public static final String TBL_COLUMN_LATLONG="latlong";
    public static final String TBL_COLUMN_SYNCSTATUS="syncstatus";
    public static final String TBL_COLUMN_SALESTIME="salestime";


    //tblnotpurchasedremarks
    public static final String TBL_NOT_PURCHASED_REMARKS ="tblnotpurchasedremarks";
    public static final String TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS ="notpurchasedremarks";
    public static final String TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS_CODE ="notpurchasedremarkscode";

    public static final String KEY_GET_GPSTRACKINGSTATUS = "getgpstrackingstatus";
    public static final String RESULT_SUCCESS="success";
    public static final String RESULT_FAILURE="failure";
    public static final String KEY_DEVICEID = "deviceid";
    public static final String KEY_GETFORMATDATE = "getformatdate";
    public static final String KEY_GETFINANCIALYEARCODE = "getfinanceyrcode";
    public static final String KEY_GET_SCHEDULE_SCHEDULECODE = "schedule_getschedulecode";
    public static final int PERMISSION_LOCATION=136;

    public static final String LATLONG_NULL_VALUE = "null,null";
    public static final String LATLONG_ZERO_VALUE = "0.0,0.0";



    //table name constants
    public static final String TBLSALES="tblsales";
    public static final String TBLSALESORDER="tblsalesorder";
    public static final String TBLGENERALSETTINGS="tblgeneralsettings";
    public static final String COLUMNAUTOAPPROVAL="orderautoapproval";
    public static final String KEY_MENU_ITEM_UPI="UPI";
    public static final String FOLDER_EINVOICE = "Einvoice";
    public static final String FOLDER_UPI_IMAGES = "upiimages";
    public static final String FOLDER_CASH_SUMMARY = "Cash_summary";

    public static final String SWIPE_MENU_VIEW = "VIEW";
    public static final String SWIPE_MENU_EDIT = "EDIT";
    public static final String SWIPE_MENU_UPI = "UPI";
    public static final String SWIPE_MENU_DOWNLOAD = "DOWNLOAD";
    public static final String SWIPE_MENU_UPLOAD = "UPLOAD";
    public static final String SWIPE_MENU_SHARE = "SHARE";

    public static final int DEFAULT_PERMISSION = 999;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 1000;
    public static final int STORAGE_PERMISSION_ANDROID_11 = 1001;
    public static final int STORAGE_PERMISSION_ANDROID_13 = 1002;
    public static final int STORAGE_PERMISSION_ANDROID_14 = 1003;
    public static final int BLUETOOTH_PERMISSION_ANDROID_12_AND_ABOVE = 1004;

}
