package com.education.education.user.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EStatus {
  ACTIVE,
  BANNED;

  @JsonCreator
  public static EStatus fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null; // This is the fix!
    }
    try {
      return EStatus.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.out.println(e);
      return null;
    }
  }
}
