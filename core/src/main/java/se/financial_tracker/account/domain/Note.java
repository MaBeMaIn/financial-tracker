package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedStringBase;

/**
 * The user's own remark on a transaction. A transaction does not need one (FR-3.6), so a
 * transaction without a note holds no {@code Note} at all rather than an empty one.
 */
public final class Note extends TypedStringBase<Note> {

    public static final int MAX_LENGTH = 500;

    private Note(String value) {
        super(value);
    }

    public static Note of(String value) {
        Note note = new Note(value);
        if (note.value().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Note must be at most " + MAX_LENGTH + " characters");
        }
        return note;
    }
}
