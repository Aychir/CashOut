package osu.edu.cashout.dataModels;

public class ScannedProducts {
    private String mUpc;
    private String mUid;

    //TODO: Implementation of date member and getter/setter

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

}
