package com.education.education.user.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EGender {
  MALE,
  FEMALE;

  @JsonCreator
  public static EGender fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return EGender.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.out.println(e);
      return null;
    }
  }
}
