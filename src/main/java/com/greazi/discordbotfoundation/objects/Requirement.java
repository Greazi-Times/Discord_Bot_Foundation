package com.greazi.discordbotfoundation.objects;

import java.util.Objects;

public class Requirement {

    private final DefinedQuery<?> query;
    private final int matchesRequired;
    private final String unmatchMessage;

    public Requirement(final DefinedQuery<?> query, final int matchesRequired, final String unmatchMessage) {
        Objects.requireNonNull(query, "Defined Query cannot be null!");
        Objects.requireNonNull(unmatchMessage, "Message cannot be null!");

        this.query = query;
        this.matchesRequired = matchesRequired;
        this.unmatchMessage = unmatchMessage;
    }

    public boolean check() {
        return query.query().amount() >= matchesRequired;
    }

    public String getUnmatchMessage() {
        return unmatchMessage;
    }
}