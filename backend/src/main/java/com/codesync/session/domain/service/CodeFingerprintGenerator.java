package com.codesync.session.domain.service;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.SourceCode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class CodeFingerprintGenerator {

    private static final Set<String> JAVA_KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long",
            "native", "new", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw",
            "throws", "transient", "try", "void", "volatile",
            "while", "record", "sealed", "permits", "var",
            "yield"
    );

    private static final Set<String> CPP_KEYWORDS = Set.of(
            "alignas", "alignof", "and", "and_eq", "asm",
            "auto", "bitand", "bitor", "bool", "break",
            "case", "catch", "char", "char8_t", "char16_t",
            "char32_t", "class", "compl", "concept", "const",
            "consteval", "constexpr", "constinit", "const_cast",
            "continue", "co_await", "co_return", "co_yield",
            "decltype", "default", "delete", "do", "double",
            "dynamic_cast", "else", "enum", "explicit",
            "export", "extern", "false", "float", "for",
            "friend", "goto", "if", "inline", "int",
            "long", "mutable", "namespace", "new", "noexcept",
            "not", "not_eq", "nullptr", "operator", "or",
            "or_eq", "private", "protected", "public",
            "register", "reinterpret_cast", "requires",
            "return", "short", "signed", "sizeof", "static",
            "static_assert", "static_cast", "struct", "switch",
            "template", "this", "thread_local", "throw",
            "true", "try", "typedef", "typeid", "typename",
            "union", "unsigned", "using", "virtual", "void",
            "volatile", "wchar_t", "while", "xor", "xor_eq"
    );

    private static final Set<String> PYTHON_KEYWORDS = Set.of(
            "and", "as", "assert", "async", "await", "break",
            "case", "class", "continue", "def", "del", "elif",
            "else", "except", "False", "finally", "for",
            "from", "global", "if", "import", "in", "is",
            "lambda", "match", "None", "nonlocal", "not",
            "or", "pass", "raise", "return", "True", "try",
            "type", "while", "with", "yield"
    );

    private static final Set<String> JAVASCRIPT_KEYWORDS = Set.of(
            "as", "async", "await", "break", "case", "catch",
            "class", "const", "continue", "debugger", "default",
            "delete", "do", "else", "export", "extends",
            "false", "finally", "for", "from", "function",
            "get", "if", "import", "in", "instanceof", "let",
            "new", "null", "of", "return", "set", "static",
            "super", "switch", "this", "throw", "true", "try",
            "typeof", "undefined", "var", "void", "while",
            "with", "yield"
    );

    private static final Set<String> TYPESCRIPT_KEYWORDS =
            new HashSet<>(JAVASCRIPT_KEYWORDS);

    static {
        TYPESCRIPT_KEYWORDS.addAll(Set.of(
                "abstract", "any", "boolean", "declare",
                "enum", "implements", "interface", "keyof",
                "module", "namespace", "never", "number",
                "private", "protected", "public", "readonly",
                "require", "string", "symbol", "type",
                "unknown", "unique"
        ));
    }

    private static final Set<String> C_KEYWORDS = Set.of(
            "auto", "break", "case", "char", "const", "continue",
            "default", "do", "double", "else", "enum", "extern",
            "float", "for", "goto", "if", "inline", "int",
            "long", "register", "restrict", "return", "short",
            "signed", "sizeof", "static", "struct", "switch",
            "typedef", "union", "unsigned", "void", "volatile",
            "while", "_Bool", "_Complex", "_Imaginary"
    );

    private static final Set<String> CSHARP_KEYWORDS = Set.of(
            "abstract", "as", "base", "bool", "break", "byte",
            "case", "catch", "char", "checked", "class",
            "const", "continue", "decimal", "default", "delegate",
            "do", "double", "else", "enum", "event", "explicit",
            "extern", "false", "finally", "fixed", "float",
            "for", "foreach", "goto", "if", "implicit", "in",
            "int", "interface", "internal", "is", "lock",
            "long", "namespace", "new", "null", "object",
            "operator", "out", "override", "params", "private",
            "protected", "public", "readonly", "ref", "return",
            "sbyte", "sealed", "short", "sizeof", "stackalloc",
            "static", "string", "struct", "switch", "this",
            "throw", "true", "try", "typeof", "uint", "ulong",
            "unchecked", "unsafe", "ushort", "using", "virtual",
            "void", "volatile", "while", "var", "record"
    );

    private static final Set<String> GO_KEYWORDS = Set.of(
            "break", "default", "func", "interface", "select",
            "case", "defer", "go", "map", "struct", "chan",
            "else", "goto", "package", "switch", "const",
            "fallthrough", "if", "range", "type", "continue",
            "for", "import", "return", "var"
    );

    private static final Set<String> KOTLIN_KEYWORDS = Set.of(
            "as", "break", "class", "continue", "do", "else",
            "false", "for", "fun", "if", "in", "interface",
            "is", "null", "object", "package", "return",
            "super", "this", "throw", "true", "try", "typealias",
            "typeof", "val", "var", "when", "while", "by",
            "catch", "constructor", "delegate", "dynamic",
            "field", "file", "finally", "get", "import",
            "init", "param", "property", "receiver", "set",
            "setparam", "where", "actual", "abstract", "annotation",
            "companion", "const", "crossinline", "data", "enum",
            "expect", "external", "final", "infix", "inline",
            "inner", "internal", "lateinit", "noinline",
            "open", "operator", "out", "override", "private",
            "protected", "public", "reified", "sealed", "suspend",
            "tailrec", "vararg"
    );

    private static final Set<String> RUST_KEYWORDS = Set.of(
            "as", "async", "await", "break", "const", "continue",
            "crate", "dyn", "else", "enum", "extern", "false",
            "fn", "for", "if", "impl", "in", "let", "loop",
            "match", "mod", "move", "mut", "pub", "ref",
            "return", "self", "Self", "static", "struct",
            "super", "trait", "true", "type", "unsafe", "use",
            "where", "while", "abstract", "become", "box",
            "do", "final", "macro", "override", "priv",
            "typeof", "unsized", "virtual", "yield", "try"
    );

    public CodeFingerprint generate(
            SourceCode sourceCode,
            ProgrammingLanguage language) {

        if (sourceCode == null) {
            throw new IllegalArgumentException(
                    "Source code cannot be null."
            );
        }

        if (language == null) {
            throw new IllegalArgumentException(
                    "Programming language cannot be null."
            );
        }

        String canonical =
                canonicalize(
                        sourceCode.value(),
                        language
                );

        return new CodeFingerprint(
                sha256(canonical)
        );
    }

    private String canonicalize(
            String source,
            ProgrammingLanguage language) {

        Set<String> keywords =
                keywordsFor(language);

        StringBuilder result =
                new StringBuilder(language.name()).append("|");

        Map<String, String> identifiers =
                new HashMap<>();

        int nextIdentifier = 0;

        int i = 0;

        while (i < source.length()) {

            char current =
                    source.charAt(i);

            if (Character.isWhitespace(current)) {
                i++;
                continue;
            }

            if (current == '/'
                    && i + 1 < source.length()
                    && source.charAt(i + 1) == '/') {

                i = skipLineComment(source, i + 2);
                continue;
            }

            if (current == '#'
                    && language == ProgrammingLanguage.PYTHON) {

                i = skipLineComment(source, i + 1);
                continue;
            }

            if (current == '/'
                    && i + 1 < source.length()
                    && source.charAt(i + 1) == '*') {

                i = skipBlockComment(source, i + 2);
                continue;
            }

            if (current == '"'
                    || current == '\''
                    || current == '`') {

                int end =
                        readStringLiteral(
                                source,
                                i,
                                current
                        );

                result.append(
                        source,
                        i,
                        end
                );

                i = end;
                continue;
            }

            if (Character.isJavaIdentifierStart(current)) {

                int start = i;

                i++;

                while (i < source.length()
                        && Character.isJavaIdentifierPart(
                        source.charAt(i))) {

                    i++;
                }

                String token =
                        source.substring(start, i);

                if (keywords.contains(token)) {

                    result.append(token);

                } else {

                    String normalized =
                            identifiers.get(token);

                    if (normalized == null) {

                        normalized =
                                "ID" + nextIdentifier++;

                        identifiers.put(
                                token,
                                normalized
                        );
                    }

                    result.append(normalized);
                }

                continue;
            }

            if (Character.isDigit(current)) {

                int start = i;

                i++;

                while (i < source.length()
                        && (Character.isLetterOrDigit(
                        source.charAt(i))
                        || source.charAt(i) == '.'
                        || source.charAt(i) == '_')) {

                    i++;
                }

                result.append(
                        source,
                        start,
                        i
                );

                continue;
            }

            int operatorLength =
                    longestOperatorLength(
                            source,
                            i
                    );

            result.append(
                    source,
                    i,
                    i + operatorLength
            );

            i += operatorLength;
        }

        return result.toString();
    }

    private int skipLineComment(
            String source,
            int index) {

        while (index < source.length()
                && source.charAt(index) != '\n') {

            index++;
        }

        return index;
    }

    private int skipBlockComment(
            String source,
            int index) {

        while (index + 1 < source.length()) {

            if (source.charAt(index) == '*'
                    && source.charAt(index + 1) == '/') {

                return index + 2;
            }

            index++;
        }

        return source.length();
    }

    private int readStringLiteral(
            String source,
            int start,
            char quote) {

        int i = start + 1;

        while (i < source.length()) {

            char current =
                    source.charAt(i);

            if (current == '\\') {
                i += 2;
                continue;
            }

            if (current == quote) {
                return i + 1;
            }

            i++;
        }

        return source.length();
    }

    private int longestOperatorLength(
            String source,
            int index) {

        String[] operators = {
                ">>>=", "<<=", ">>=", "===",
                "!==", ">>>", "<<", ">>",
                "++", "--", "&&", "||",
                "==", "!=", "<=", ">=",
                "+=", "-=", "*=", "/=",
                "%=", "&=", "|=", "^=",
                "->", "::", "=>", "??",
                "?.", "**", "...",
                "+", "-", "*", "/",
                "%", "=", "<", ">",
                "!",
                "&", "|", "^", "~",
                "?", ":", ";", ",",
                ".",
                "(", ")", "{", "}", "[", "]"
        };

        for (String operator : operators) {

            if (source.startsWith(
                    operator,
                    index
            )) {

                return operator.length();
            }
        }

        return 1;
    }

    private Set<String> keywordsFor(
            ProgrammingLanguage language) {

        return switch (language) {

            case JAVA -> JAVA_KEYWORDS;

            case CPP -> CPP_KEYWORDS;

            case PYTHON -> PYTHON_KEYWORDS;

            case JAVASCRIPT -> JAVASCRIPT_KEYWORDS;

            case TYPESCRIPT -> TYPESCRIPT_KEYWORDS;

            case C -> C_KEYWORDS;

            case CSHARP -> CSHARP_KEYWORDS;

            case GO -> GO_KEYWORDS;

            case KOTLIN -> KOTLIN_KEYWORDS;

            case RUST -> RUST_KEYWORDS;

            case UNKNOWN -> Set.of();
        };
    }

    private String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }
}