package osu.edu.cashout;


public class Review {

    private double mScore;
    private String mTitle;
    private String mDescription;
    private String mUserId;
    private String mUpc;
    private String mDateCreated;
    private String mReviewId;

    public Review(){

    }

    public double getScore() {
        return mScore;
    }

    public void setScore(Double mScore) {
        this.mScore = mScore;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String id) {
        this.mUserId = id;
    }

    public String getUpc(){
        return mUpc;
    }

    public void setUpc(String upc) {
        this.mUpc = upc;
    }

    public String getDate(){
        return mDateCreated;
    }

    public void setDate(String date){
        this.mDateCreated = date;
    }

    public String getReviewId(){
        return mReviewId;
    }

    public void setReviewId(String id){
        this.mReviewId = id;
    }

}
