package osu.edu.cashout.dataModels;

public class User {

    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private String mPassword;

    //Note: We obviously do not want to store password of the user in this database
    //  Also date of birth will have to wait, if we implement it at all

    public User(){

    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPassword() { return mPassword; }

    public void setPassword(String mPassword) { this.mPassword = mPassword; }
}
