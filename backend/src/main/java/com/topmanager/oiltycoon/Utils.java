package com.topmanager.oiltycoon;

public class Utils {
    public static final String NAME_NOT_VALID_MESSAGE = "Name must be not blank and in range from 6 to 25";
    public static final String EMAIL_NOT_VALID = "Email not valid";


    public static final String EMAIL_REGEX = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";
    public static final String BASE_URL = "http://localhost:8080";

    public enum MailMessage {
        REGISTRATION_CONFIRM("Registration Confirmation", "Confirm your account: "+BASE_URL+"/verification?token=%s"),
        RESET_PASSWORD("Reset password", "You forgot your password. This link helps you to reset and change password: "
                +BASE_URL+"/reset-password?token=%s");

        private String subject;
        private String message;

        MailMessage(String subject, String message) {
            this.subject = subject;
            this.message = message;
        }

        public String getSubject() {
            return subject;
        }

        public String getMessage() {
            return message;
        }

    }

}
