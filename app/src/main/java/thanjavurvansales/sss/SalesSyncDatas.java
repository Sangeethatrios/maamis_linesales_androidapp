package thanjavurvansales.sss;

class SalesSyncDatas {
    String[] TransactionNo;
    String[] SalesItemTransactionNo;
    String[] StockTransactionNo;
    public String[] getScheduleCode(){ return TransactionNo;}
    public String[] getSalesItemTransactionNo(){ return SalesItemTransactionNo;}
    public String[] getStockTransactionNo(){ return StockTransactionNo;}
    public SalesSyncDatas(String[] TransactionNo,String[] SalesItemTransactionNo,String[] StockTransactionNo) {
        super();
        this.TransactionNo = TransactionNo;
        this.SalesItemTransactionNo = SalesItemTransactionNo;
        this.StockTransactionNo = StockTransactionNo;
    }
}