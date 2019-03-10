package com.android.ososstar.learningepisode.course;

public class Course {

    /**
     * The ID, Name, Description, Image, of the course
     */
    private String mID, mName, mDescription, mImage, mEnrolls, mCreationDate;

    /**
     * Create a new Course Object
     */
    public Course(String ID, String Name, String Description, String Image, String Enrolls, String CreationDate){
        mID = ID;
        mName = Name;
        mDescription = Description;
        mImage = Image;
        mEnrolls = Enrolls;
        mCreationDate = CreationDate;
    }

    /**
     * Get The ID of the Course
     */
    public String getCourseID(){
        return mID;
    }

    /**
     * Get The Name of the Course
     */
    public String getCourseName(){
        return mName;
    }

    /**
     * Get The Description of the Course
     */
    public String getCourseDescription(){
        return mDescription;
    }

    /**
     * Get The Image of the Course
     */
    public String getCourseImage(){
        return mImage;
    }

    /**
     * Get The Total Enrolls of the Course
     */
    public String getCourseEnrolls(){
        return mEnrolls;
    }

    /**
     * Get The Creation Date of the Course
     */
    public String getCourseCreationDate(){
        return mCreationDate;
    }

}
