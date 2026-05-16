package com.education.education.exeption;

public class PastWeekException extends RuntimeException {
    public PastWeekException(Integer weekYear, Integer weekNumber) {
        super("Weekly session week " + weekYear + "-W" + weekNumber + " is in the past");
    }
}