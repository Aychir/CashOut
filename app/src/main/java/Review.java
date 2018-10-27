//import java.util.Date;

public class Review {

    private String mReviewId;
    private Float mScore;
    private String mTitle;
    private String mDescription;
//    private Date mDateCreated;

    public Review(){

    }

    public String getReviewId() {
        return mReviewId;
    }

    public void setReviewId(String mReviewId) {
        this.mReviewId = mReviewId;
    }

    public Float getScore() {
        return mScore;
    }

    public void setScore(Float mScore) {
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

}
