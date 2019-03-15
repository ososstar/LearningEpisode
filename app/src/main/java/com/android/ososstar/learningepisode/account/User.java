package com.android.ososstar.learningepisode.account;

public class User {

    /**
     * The Username, Email, Name of the User
     */
    private int mID, mType;
    private String mUsername, mEmail, mName, mImageURL, mDate;

    /**
     * Create a new User Object
     */
    public User(int ID, String Username, String Email, String Name, String ImageURL, int Type, String Date) {
        mID = ID;
        mUsername = Username;
        mEmail = Email;
        mName = Name;
        mType = Type;
        mImageURL = ImageURL;
        mDate = Date;
    }

    /**
     * Get The ID of the User
     */
    public int getID(){
        return mID;
    }

    /**
     * Get The Username of the User
     */
    public String getUsername(){
        return mUsername;
    }

    /**
     * Get The Email of the User
     */
    public String getEmail(){
        return mEmail;
    }

    /**
     * Get The Name of the User
     */
    public String getName(){
        return mName;
    }

    /**
     * Get The Profile Picture of the User
     */
    public String getImageURL() {
        return mImageURL;
    }

    /**
     * Get The Type of the User
     */
    public int getType(){
        return mType;
    }

    /**
     * Get The Creation Date of the User
     */
    public String getDate(){
        return mDate;
    }
}
