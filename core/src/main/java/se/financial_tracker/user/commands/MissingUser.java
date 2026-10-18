package se.financial_tracker.user.commands;

public class MissingUser extends RuntimeException {
    public MissingUser(String message) {
        super(message);
    }
}
