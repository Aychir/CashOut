package osu.edu.cashout.dataModels;

public class ScannedProducts {
    private String mUpc;
    private String mUid;
    private String mDate;

    public ScannedProducts(){

    }

    public String getUpc(){
        return this.mUpc;
    }

    public void setUpc(String code){
        this.mUpc = code;
    }

    public String getUid(){
        return this.mUid;
    }

    public void setUid(String id){
        this.mUid = id;
    }

    public String getDate(){ return this.mDate; }

    public void setDate(String curDate){ this.mDate = curDate; }

}
