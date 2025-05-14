package trios.linesales;

public class ExpensesDetails {
    public String transactionno;
    public String transactiondate;
    public String expensesheadcode;
    public String amount;
    public String remarks;
    public String expensesheadname;
    public String expensesheadnametamil;
    public String sno;
    public String schedulecode;

    public ExpensesDetails(String transactionno, String transactiondate, String expensesheadcode,
                           String amount, String remarks, String expensesheadname,
                           String expensesheadnametamil, String sno,String schedulecode) {
        this.transactionno = transactionno;
        this.transactiondate = transactiondate;
        this.expensesheadcode = expensesheadcode;
        this.amount = amount;
        this.remarks = remarks;
        this.expensesheadname = expensesheadname;
        this.expensesheadnametamil = expensesheadnametamil;
        this.sno = sno;
        this.schedulecode = schedulecode;
    }

    public String getTransactionno() {
        return transactionno;
    }

    public String getTransactiondate() {
        return transactiondate;
    }

    public String getExpensesheadcode() {
        return expensesheadcode;
    }

    public String getAmount() {
        return amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getExpensesheadname() {
        return expensesheadname;
    }

    public String getExpensesheadnametamil() {
        return expensesheadnametamil;
    }

    public String getSno() {
        return sno;
    }

    public String getSchedulecode() {
        return schedulecode;
    }
}