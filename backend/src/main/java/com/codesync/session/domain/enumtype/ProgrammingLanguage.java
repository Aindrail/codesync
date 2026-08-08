package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

/*
Information every programming language have that CodeSync will need?

Display Name
↓
File Extension
↓
Comment Style
↓
MIME Type (future)
↓
Platform Language IDs (future)

For Version 1 we'll keep only the first two.

 */
public enum ProgrammingLanguage implements Displayable {
    JAVA("Java", ".java"),

    CPP("C++", ".c++"),

    PYTHON("Python", ".py"),

    JAVASCRIPT("JavaScript", ".js"),

    TYPESCRIPT("TypeScript", ".ts"),

    C("C", ".c"),

    CSHARP("C#", ".cs"),

    GO("Go", ".go"),

    KOTLIN("Kotlin", ".kt"),

    RUST("Rust", ".rs"),

    UNKNOWN("Unknown", ".txt");

    private final String displayName;
    private final String fileExtension;
    private ProgrammingLanguage(String displayName, String fileExtension) {
        this.displayName = displayName;
        this.fileExtension = fileExtension;
    }

    @Override
    public String displayName() {
        return displayName;
    }
    public String DisplayFileExtension() {
        return fileExtension;
    }

}
