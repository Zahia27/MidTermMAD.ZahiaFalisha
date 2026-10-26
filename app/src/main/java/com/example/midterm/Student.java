package com.example.midterm;

import androidx.annotation.NonNull;

public class Student {
    private final String studentId;
    private final String name;
    private final boolean enrolled;

    public Student(String studentId, String name, boolean enrolled) {
        this.studentId = studentId;
        this.name = name;
        this.enrolled = enrolled;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public boolean isEnrolled() { return enrolled; }

    @NonNull
    @Override
    public String toString() {
        String status = enrolled ? "Enrolled" : "Not Enrolled";
        return studentId + " - " + name + " (" + status + ")";
    }
}