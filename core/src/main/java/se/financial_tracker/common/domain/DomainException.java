package se.financial_tracker.common.domain;

/**
 * Parent of every exception raised by a broken domain rule. Subclasses are named after the rule
 * they protect, never after a layer or a status code; the web adapter is what turns them into
 * HTTP.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
