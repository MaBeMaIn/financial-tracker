package se.financial_tracker.common.domain;

import java.util.Objects;

public class TypedBooleanBase<T extends TypedBooleanBase<T>> implements TypedBoolean, Comparable<T> {

    private final Boolean value;

    protected TypedBooleanBase(Boolean value) {
        if(value == null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " must not be null");
        }
        this.value = value;
    }

    @Override
    public int compareTo(T other) {
        return Boolean.compare(value, other.value());
    }

    @Override
    public boolean value() {
        return value;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return value == ((TypedBooleanBase<?>) other).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }
}
