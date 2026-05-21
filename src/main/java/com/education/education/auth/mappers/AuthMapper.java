package com.education.education.auth.mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.education.education.auth.deo.responses.MfaActivityRes;
import com.education.education.auth.enums.EMfaType;

@Component
public class AuthMapper {

  public MfaActivityRes toMfaActivityRes(
      String username,
      EMfaType type,
      int attempts,
      LocalDateTime time,
      boolean isUsed) {
    return new MfaActivityRes(
        username,
        type,
        attempts,
        time,
        isUsed);
  }
}
