package com.android.ososstar.learningepisode.question;

public class Question {

    /**
     * The ID, Title, Choice1, Choice2, Choice3, Answer, LessonID of the question
     */
    private String mID, mTitle, mChoice1, mChoice2, mChoice3, mAnswer, mLessonID;

    public int checkedId = -1;

    /**
     * Create a new Lesson Object
     */
    public Question(String ID, String Title, String Choice1,String Choice2, String Choice3, String Answer, String LessonID) {
        mID = ID;
        mTitle = Title;
        mChoice1 = Choice1;
        mChoice2 = Choice2;
        mChoice3 = Choice3;
        mAnswer = Answer;
        mLessonID = LessonID;
    }

    /**
     * Get The ID of the Question
     */
    public String getID() {
        return mID;
    }

    /**
     * Get The Title of the Question
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get The Choice1 of the Question
     */
    public String getChoice1() {
        return mChoice1;
    }

    /**
     * Get The Choice2 of the Question
     */
    public String getChoice2() {
        return mChoice2;
    }

    /**
     * Get The Choice3 of the Question
     */
    public String getChoice3() {
        return mChoice3;
    }

    /**
     * Get The Answer of the Question
     */
    public String getAnswer() {
        return mAnswer;
    }

    /**
     * Get The LessonID that related to the Question
     */
    public String getLessonID() {
        return mLessonID;
    }

    public void setCheckedId(int checkedId) {
        this.checkedId = checkedId;
    }

    public int getCheckedId() {
        return checkedId;
    }
}
