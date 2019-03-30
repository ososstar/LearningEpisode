package com.android.ososstar.learningepisode.feedback;

public class Feedback {

    /**
     * The ID, feedbackType, Date, studentComment, studentAttachedImage, adminReply, of the Feedback
     */
    private String mID, mFeedbackType, mDate, mStudentComment, mStudentAttachedImage, mAdminReply, mStudentID;

    /**
     * Create a new Feedback Object
     */
    public Feedback(String ID, String FeedbackType, String Date, String StudentComment, String StudentAttachedImage, String AdminReply, String StudentID) {
        mID = ID;
        mFeedbackType = FeedbackType;
        mDate = Date;
        mStudentComment = StudentComment;
        mStudentAttachedImage = StudentAttachedImage;
        mAdminReply = AdminReply;
        mStudentID = StudentID;
    }

    /**
     * Get The ID of the Feedback
     */
    public String getID() {
        return mID;
    }

    /**
     * Get The FeedbackType of the Feedback
     */
    public String getFeedbackType() {
        return mFeedbackType;
    }

    /**
     * Get The Date of the Feedback
     */
    public String getDate() {
        return mDate;
    }


    /**
     * Get The StudentComment of the Feedback
     */
    public String getStudentComment() {
        return mStudentComment;
    }

    /**
     * Get The StudentAttachedImage of the Feedback
     */
    public String getStudentAttachedImage() {
        return mStudentAttachedImage;
    }

    /**
     * Get The AdminReply of the Feedback
     */
    public String getAdminReply() {
        return mAdminReply;
    }

    /**
     * Get The StudentID of the Feedback
     */
    public String getStudentID() {
        return mStudentID;
    }

}
