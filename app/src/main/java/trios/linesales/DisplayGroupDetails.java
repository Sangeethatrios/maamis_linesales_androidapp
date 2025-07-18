package trios.linesales;


public class DisplayGroupDetails {
    String displayGroupCode, displayGroupName, displayGroupNameTamil;

    public DisplayGroupDetails(String displayGroupCode, String displayGroupName, String displayGroupNameTamil) {
        this.displayGroupCode = displayGroupCode;
        this.displayGroupName = displayGroupName;
        this.displayGroupNameTamil = displayGroupNameTamil;
    }

    public String getDisplayGroupCode() {
        return displayGroupCode;
    }

    public void setDisplayGroupCode(String displayGroupCode) {
        this.displayGroupCode = displayGroupCode;
    }

    public String getDisplayGroupName() {
        return displayGroupName;
    }

    public void setDisplayGroupName(String displayGroupName) {
        this.displayGroupName = displayGroupName;
    }

    public String getDisplayGroupNameTamil() {
        return displayGroupNameTamil;
    }

    public void setDisplayGroupNameTamil(String displayGroupNameTamil) {
        this.displayGroupNameTamil = displayGroupNameTamil;
    }
}