package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

/*
 * Represents the type of AI interaction performed during a coding session.
 * These cover all planned AI features:

Classification → Folder categorization
Hint → Live coding assistance
Review → Wrong answer analysis
README → README generation
Explanation → Explain accepted solution

Future additions like COMPLEXITY_ANALYSIS can be added without affecting the existing design.
 */
public enum AIInteractionType implements Displayable {

    CLASSIFICATION("Classification"),

    HINT("Hint"),

    REVIEW("Review"),

    README("README Generation"),

    EXPLANATION("Explanation");

    private final String displayName;

    AIInteractionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String displayName() {
        return displayName;
    }
}