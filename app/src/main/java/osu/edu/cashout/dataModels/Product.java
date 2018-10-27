package osu.edu.cashout.dataModels;

public class Product {

    public Product(){

    }
    
    private String mUpc;
    private String mName;
    private double mPrice;
    private double mAmazonRating;
    private double mCashOutRating;

    public String getUpc() {
        return mUpc;
    }

    public void setUpc(String Upc) {
        this.mUpc = Upc;
    }

    public String getName() {
        return mName;
    }

    public void setName(String Name) {
        this.mName = Name;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double Price) {
        this.mPrice = Price;
    }

    public double getAmazonRating() {
        return mAmazonRating;
    }

    public void setAmazonRating(double AmazonRating) {
        this.mAmazonRating = AmazonRating;
    }

    public double getCashOutRating() {
        return mCashOutRating;
    }

    public void setCashOutRating(double CashOutRating) {
        this.mCashOutRating = CashOutRating;
    }


}
