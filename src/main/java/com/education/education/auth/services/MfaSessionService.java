package com.education.education.auth.services;

import com.education.education.auth.utils.AuthUtils;
import com.education.education.auth.utils.MFAUtils;
import com.education.education.email.services.EmailService;
import com.education.education.auth.deo.responses.MfaActivityRes;
import com.education.education.auth.deo.responses.SignUpDTOResponse;
import org.springframework.stereotype.Service;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import com.education.education.auth.entities.MfaSession;
import com.education.education.auth.enums.EMfaType;
import org.jboss.aerogear.security.otp.Totp;

import com.education.education.auth.repositories.MfaSessionRepository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class MfaSessionService {

  private final MfaSessionRepository mfaSessionRepository;
  private final UserRepository userRepository;
  private final AuthUtils authUtils;
  private final EmailService emailService;

  public boolean verifySessionCode(UserDetails userdetails, String code, EMfaType type) {

    User user = userRepository.findByUsername(userdetails.getUsername());

    return mfaSessionRepository.findFirstByUserAndTypeAndIsUsedFalseOrderByCreatedAtDesc(user, type)
        .filter(session -> !session.isExpired())
        .map(session -> {
          session.setAttempts(session.getAttempts() + 1);

          if (session.getAttempts() > 5) {
            session.setUsed(true);
            mfaSessionRepository.save(session);
            return false;
          }

          if (session.getCode().equals(code)) {
            session.setUsed(true);
            mfaSessionRepository.save(session);
            return true;
          }

          mfaSessionRepository.save(session);
          return false;
        }).orElse(false);
  }

  public Map<String, String> setupTotp(UserDetails userDetails) {
    User user = userRepository.findByUsername(userDetails.getUsername());

    String secret = MFAUtils.generateSecretKey();
    user.setTotpSecret(secret);
    userRepository.save(user);
    return Map.of(
        "secretKey", secret,
        "qrUrl", MFAUtils.generateQrUrl(secret, user.getEmail()));
  }

  public boolean confirmTotpSetup(UserDetails userDetails, String code) {
    User user = userRepository.findByUsername(userDetails.getUsername());
    if (user.getTotpSecret() == null) {
      return false;
    }

    boolean isValid = verifyAppCode(user.getTotpSecret(), code);
    if (isValid) {
      user.setTotpEnabled(true);
      userRepository.save(user);
      return true;
    }

    return false;
  }

  public boolean verifyAppCode(String secret, String code) {
    try {
      if (secret == null || code == null)
        return false;

      String cleanCode = code.trim();
      Totp totp = new Totp(secret);

      boolean isMathValid = totp.verify(cleanCode);
      boolean isDirectMatch = cleanCode.equals(totp.now());
      System.out.println(totp.now());
      System.out.println("DEBUG: isMathValid: " + isMathValid);
      System.out.println("DEBUG: isDirectMatch: " + isDirectMatch);
      return isMathValid || isDirectMatch;
    } catch (Exception e) {
      System.err.println("TOTP Error: " + e.getMessage());
      return false;
    }
  }

  public void sendMfaEmailCode(UserDetails userDetails) {

    User user = userRepository.findByUsername(userDetails.getUsername());

    String code = String.format("%06d", new Random().nextInt(999999));

    MfaSession session = MfaSession.builder()
        .user(user)
        .code(code)
        .expiry(LocalDateTime.now().plusMinutes(5))
        .type(EMfaType.EMAIL)
        .isUsed(false)
        .build();
    mfaSessionRepository.save(session);

    emailService.sendMfaVerificationEmail(user.getEmail(), code, user.getFirstName());
  }

  public SignUpDTOResponse generateFinalToken(UserDetails userDetails) {
    return authUtils.generateTokenResponse(userDetails);
  }

  public boolean verifyMfa(UserDetails userDetails, String code, EMfaType type) {

    if (type == EMfaType.APP) {
      User user = userRepository.findByUsername(userDetails.getUsername());
      if (user.getTotpSecret() == null || !user.isTotpEnabled()) {
        return false;
      }

      return verifyAppCode(user.getTotpSecret(), code);
    } else {
      return verifySessionCode(userDetails, code, type);
    }
  }

  public List<MfaActivityRes> getMfaActivities() {
    return mfaSessionRepository.findLastMfaActivities(PageRequest.of(0, 15));
  }
}
