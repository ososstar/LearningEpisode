package com.android.ososstar.learningepisode.lesson;

public class Lesson {

    /**
     * The ID, Name, Description, Image, of the course
     */
    private String mID, mTitle, mDescription, mLink, mVideoURL, mCreationDate, mCourseID;

    /**
     * Create a new Course Object
     */
    public Lesson(String ID, String Title, String Description, String Link, String VideoURL, String CreationDate, String CourseID){
        mID = ID;
        mTitle = Title;
        mDescription = Description;
        mLink = Link;
        mVideoURL = VideoURL;
        mCreationDate = CreationDate;
        mCourseID = CourseID;
    }


    /**
     * Get The ID of the Lesson
     */
    public String getID(){
        return mID;
    }

    /**
     * Get The Title of the Lesson
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Get The Description of the Lesson
     */
    public String getDescription(){
        return mDescription;
    }

    /**
     * Get The Link of the Lesson
     */
    public String getLink(){
        return mLink;
    }

    /**
     * Get The VideoURL of the Lesson
     */
    public String getVideoURL(){
        return mVideoURL;
    }

    /**
     * Get The CreationDate of the Lesson
     */
    public String getCreationDate(){
        return mCreationDate;
    }

    /**
     * Get The CourseID of the Lesson
     */
    public String getCourseID(){
        return mCourseID;
    }



}
