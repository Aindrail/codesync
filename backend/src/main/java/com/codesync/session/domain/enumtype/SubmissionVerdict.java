package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

public enum SubmissionVerdict implements Displayable {

    ACCEPTED("Accepted"),

    WRONG_ANSWER("Wrong Answer"),

    TIME_LIMIT_EXCEEDED("Time Limit Exceeded"),

    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded"),

    RUNTIME_ERROR("Runtime Error"),

    COMPILATION_ERROR("Compilation Error"),

    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded"),

    PRESENTATION_ERROR("Presentation Error"),

    INTERNAL_ERROR("Internal Error"),

    UNKNOWN("Unknown");

    private final String displayName;

    SubmissionVerdict(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String displayName() {
        return displayName;
    }
}
