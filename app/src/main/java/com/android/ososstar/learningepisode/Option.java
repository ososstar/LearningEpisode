package com.android.ososstar.learningepisode;

/**
 * {@link Option} represents an option that the user wants to show.
 * It contains a Title and an Image for that Option.
 */
public class Option {

    /**
     * A Title for the Option
     */
    private String mTitle;

    /** Image Resource ID for the Option **/
    private static final int NO_IMAGE_PROVIDED = -1;
    private int mImageResourceId = NO_IMAGE_PROVIDED;

    /**
     * Create a new Option object.
     *
     * @param title is the Title of the option
     * @param imageResourceId is the image resource id for the word
     *
     */
    public Option(String title, int imageResourceId){
        mTitle = title;
        mImageResourceId = imageResourceId;
    }

    /**
     * Another Constructor
     * */
    public Option (String title){
        mTitle = title;
    }

    /**
     * Get the Title translation of the Option.
     */
    public String getTitle (){
        return mTitle;
    }

    /**
     * Get the Image Resource ID of the Option.
     */
    public int getImageResourceId(){
        return mImageResourceId;
    }

    /**
     * returns whether or not there is an image for this word
     */
    public boolean hasImage(){
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }
}
