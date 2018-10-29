package osu.edu.cashout.dataModels;

import android.graphics.Bitmap;

public class Product {

    public Product(){

    }
    
    private String mUpc;
    private String mName;
    private double mLowestPrice;
    private double mHighestPrice;
    private double mRating;
    private Bitmap mImage;

    //TODO: Possible implementation of a thumbnail attribute

    public String getUpc() {
        return mUpc;
    }

    public void setUpc(String upc) {
        this.mUpc = upc;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public double getLowestPrice() {
        return mLowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.mLowestPrice = lowestPrice;
    }

    public double getHighestPrice() {
        return mHighestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.mHighestPrice = highestPrice;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        this.mRating = rating;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        this.mImage = image;
    }


}
