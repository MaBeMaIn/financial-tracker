package se.financial_tracker.common.domain;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * Generates UUID version 7 identifiers: a 48-bit millisecond timestamp, then a counter, then
 * random bits (RFC 9562). Identifiers minted later therefore sort after identifiers minted
 * earlier, which keeps database inserts landing at the end of the index instead of scattered
 * across it — the reason this project does not use version 4.
 *
 * <p>Identifiers are generated here, in the application, never by the database: an aggregate has
 * its identity from the moment {@code create(...)} builds it, before anything is saved.
 *
 * <p>The counter makes identifiers minted inside the same millisecond ordered as well. If more
 * than 4096 arrive in one millisecond, generation borrows the next millisecond rather than repeat
 * a value, and the same happens if the system clock moves backwards, so the sequence never
 * regresses.
 */
public final class Uuid7 {

    private static final int VERSION = 7;
    private static final int VARIANT = 2;

    private static final long TIMESTAMP_MASK = 0xFFFFFFFFFFFFL;
    private static final long MAX_COUNTER = 0xFFFL;
    private static final long RANDOM_MASK = 0x3FFFFFFFFFFFFFFFL;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static long lastMillis = Long.MIN_VALUE;
    private static long counter;

    private Uuid7() {}

    /** A new identifier, ordered after every identifier this generator has handed out before. */
    public static synchronized UUID newUuid() {
        long now = Instant.now().toEpochMilli();

        if (now > lastMillis) {
            lastMillis = now;
            counter = 0;
        } else {
            counter++;
            if (counter > MAX_COUNTER) {
                lastMillis++;
                counter = 0;
            }
        }

        long mostSignificantBits = (lastMillis & TIMESTAMP_MASK) << 16 | (long) VERSION << 12 | counter;
        long leastSignificantBits = (long) VARIANT << 62 | RANDOM.nextLong() & RANDOM_MASK;

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    /** Whether a value carries version 7 and the variant RFC 9562 requires. */
    public static boolean isUuid7(UUID value) {
        return value != null && value.version() == VERSION && value.variant() == VARIANT;
    }

    /** The instant encoded in a version 7 identifier, for diagnostics — not a domain concept. */
    public static Instant timestampOf(UUID value) {
        if (!isUuid7(value)) {
            throw new IllegalArgumentException("not a UUID version 7: " + value);
        }
        return Instant.ofEpochMilli(value.getMostSignificantBits() >>> 16 & TIMESTAMP_MASK);
    }
}
