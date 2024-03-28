package aionem.net.sdk.core.utils;


public class UtilsValidations {


    public static boolean isValidEmail(final String email) {
        return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }

}
