package com.example.uniaxe;

public class ModelTeacher {
    private String couName;
    private String couId;
    private String batch;
    private String examType;
    private String syllabusUrl;
    private String key;
    private String teacherName; // Add teacherName field

    // Default constructor (required for Firebase)
    public ModelTeacher() {
        // Firebase needs a default constructor for deserialization
    }

    // Constructor including teacherName (used when creating or updating records)
    public ModelTeacher(String couName, String couId, String batch, String examType, String syllabusUrl, String key, String teacherName) {
        this.couName = couName;
        this.couId = couId;
        this.batch = batch;
        this.examType = examType;
        this.syllabusUrl = syllabusUrl;
        this.key = key;
        this.teacherName = teacherName; // Initialize teacher name
    }

    // Getter and Setter for course name
    public String getCouName() {
        return couName;
    }

    public void setCouName(String couName) {
        this.couName = couName;
    }

    // Getter and Setter for course ID
    public String getCouId() {
        return couId;
    }

    public void setCouId(String couId) {
        this.couId = couId;
    }

    // Getter and Setter for batch
    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    // Getter and Setter for exam type
    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    // Getter and Setter for syllabus URL
    public String getSyllabusUrl() {
        return syllabusUrl;
    }

    public void setSyllabusUrl(String syllabusUrl) {
        this.syllabusUrl = syllabusUrl;
    }

    // Getter and Setter for key (Firebase unique key)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getter and Setter for teacher name
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
