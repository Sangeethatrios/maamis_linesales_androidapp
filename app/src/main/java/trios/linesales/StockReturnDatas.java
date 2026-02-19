package trios.linesales;

class StockReturnDatas {
    String ScheduleCode, StockTransferno;

    public String getScheduleCode(){ return ScheduleCode;}

    public String getStockTransferno() {
        return StockTransferno;
    }

    public StockReturnDatas(String ScheduleCode, String StockTransferno) {
        super();
        this.ScheduleCode = ScheduleCode;
        this.StockTransferno = StockTransferno;
    }
}
