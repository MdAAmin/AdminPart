package com.example.uniaxe;

public class Model {
    private String courseId, courseName, year, pdfUrl, key, semester, examType, pdfType;

    // Default constructor
    public Model() {}

    // Parameterized constructor
    public Model(String courseId, String courseName, String examType, String year, String semester, String pdfType, String pdfUrl, String key) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.examType = examType;
        this.year = year;
        this.semester = semester;
        this.pdfType = pdfType;
        this.pdfUrl = pdfUrl;
        this.key = key;
    }

    // Getter and setter for courseId
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // Getter and setter for courseName
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Getter and setter for examType
    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    // Getter and setter for year
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    // Getter and setter for semester
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    // Getter and setter for pdfType
    public String getPdfType() {
        return pdfType;
    }

    public void setPdfType(String pdfType) {
        this.pdfType = pdfType;
    }

    // Getter and setter for pdfUrl
    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    // Getter and setter for key
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
