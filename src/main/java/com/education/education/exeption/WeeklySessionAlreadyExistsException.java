package com.education.education.exeption;

public class WeeklySessionAlreadyExistsException extends RuntimeException {
    public WeeklySessionAlreadyExistsException(Integer weekYear, Integer weekNumber) {
        super("A weekly session already exists for " + weekYear + "-W" + weekNumber);
    }
}