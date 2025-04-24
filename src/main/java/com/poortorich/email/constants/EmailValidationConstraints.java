package com.poortorich.email.constants;


import java.util.regex.Pattern;

public class EmailValidationConstraints {

    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z]{2,}$");
}
