package osu.edu.cashout.dataModels;


public class Product {
    private String mUpc;
    private String mName;
    private double mLowestPrice;
    private double mHighestPrice;
    private double mRating;
    private String mReview;
    private String mImage;

    //TODO: Date scanned attribute?

    public Product(){

    }

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

//    public String getImageUrl(){
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            this.getImage().compress(Bitmap.CompressFormat.PNG, 100, baos);
//            byte[] b = baos.toByteArray();
//            String temp = Base64.encodeToString(b, Base64.DEFAULT);
//            return temp;
//        } catch (NullPointerException e) {
//            return null;
//        }
//    }

    /*
    * Note:
    *
    *
    * */

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        this.mReview = review;
    }


}
