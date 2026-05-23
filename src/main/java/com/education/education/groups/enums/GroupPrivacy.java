package com.education.education.groups.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GroupPrivacy {
  PUBLIC,
  PRIVATE;

  @JsonCreator
  public static GroupPrivacy fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return GroupPrivacy.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid privacy level: " + value + ", " + e);
    }
  }
}
