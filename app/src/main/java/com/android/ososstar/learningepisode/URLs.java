package com.android.ososstar.learningepisode;

public final class URLs {


    //genymotion
//    private static final String ROOT_URL = "http://10.0.3.2/learning_episode/";
    //ad-hoc network
    private static final String ROOT_URL = "http://192.168.137.1/learning_episode/";
    //web
//    private static final String ROOT_URL = "https://learningepisode.000webhostapp.com/learning_episode/";

    private static final String ACCOUNT_API = ROOT_URL+"apiA.php?apicall=";
    public static final String URL_LOGIN = ACCOUNT_API + "login";
    public static final String URL_REGISTER = ACCOUNT_API + "signup";
    public static final String URL_REQUEST_STUDENT_LIST = ACCOUNT_API + "request_student_list";
    public static final String URL_MODIFY_STUDENT_DATA = ACCOUNT_API + "modify_student_data";
    public static final String URL_DELETE_STUDENT_DATA = ACCOUNT_API + "delete_student_data";
    public static final String URL_REQUEST_STUDENT_NAME = ACCOUNT_API + "request_student_name";

    private static final String COURSE_API = ROOT_URL+"apiC.php?apicall=";
    public static final String URL_INSERT_NEW_COURSE = COURSE_API + "insert_new_course";
    public static final String URL_REQUEST_COURSE_LIST = COURSE_API + "request_course_list";
    public static final String URL_REQUEST_COURSE_DATA = COURSE_API + "request_course_data";
    public static final String URL_MODIFY_COURSE_DATA = COURSE_API + "modify_course_data";
    public static final String URL_DELETE_COURSE_DATA = COURSE_API + "delete_course_data";

    public static final String URL_INSERT_NEW_ENROLL = COURSE_API + "insert_new_enroll";
    public static final String URL_DELETE_ENROLL_DATA = COURSE_API + "delete_enroll_data";
    public static final String URL_REQUEST_STUDENT_ENROLLS = COURSE_API + "request_student_enrolls";
    public static final String URL_CHECK_STUDENT_ENROLL = COURSE_API + "check_student_enroll";

    public static final String URL_INSERT_NEW_LESSON = COURSE_API + "insert_new_lesson";
    public static final String URL_REQUEST_COURSE_LESSONS = COURSE_API + "request_course_lessons";
    public static final String URL_REQUEST_LESSON_DATA = COURSE_API + "request_lesson_data";
    public static final String URL_MODIFY_LESSON_DATA = COURSE_API + "modify_lesson_data";
    public static final String URL_DELETE_LESSON_DATA = COURSE_API + "delete_lesson_data";

    public static final String URL_INSERT_NEW_QUESTION = COURSE_API + "insert_new_question";
    public static final String URL_REQUEST_QUESTION_LIST = COURSE_API + "request_question_list";
    public static final String URL_REQUEST_QUESTION_DATA = COURSE_API + "request_question_data";
    public static final String URL_MODIFY_QUESTION_DATA = COURSE_API + "modify_question_data";
    public static final String URL_DELETE_QUESTION_DATA = COURSE_API + "delete_question_data";

    public static final String URL_RECORD_STUDENT_ANSWER = COURSE_API + "record_student_answer";
    public static final String URL_SHOW_STUDENT_ANSWER = COURSE_API + "show_student_answer";
    public static final String URL_DELETE_STUDENT_ANSWER = COURSE_API + "delete_student_answer";

    private static final String FEEDBACK_API = ROOT_URL + "apiF.php?apicall=";
    public static final String URL_INSERT_NEW_FEEDBACK = FEEDBACK_API + "insert_new_feedback";
    public static final String URL_REQUEST_STUDENT_FEEDBACK_LIST = FEEDBACK_API + "request_student_feedback_list";
    public static final String URL_REQUEST_FEEDBACK_LIST = FEEDBACK_API + "request_feedback_list";
    public static final String URL_MODIFY_FEEDBACK = FEEDBACK_API + "modify_feedback";
    public static final String URL_DELETE_FEEDBACK = FEEDBACK_API + "delete_feedback";














}
