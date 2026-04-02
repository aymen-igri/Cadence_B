package com.education.education.auth.utils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthUtils {
    private String mySecret = Dotenv.load().get("MY_SECRET_KEY");
}

