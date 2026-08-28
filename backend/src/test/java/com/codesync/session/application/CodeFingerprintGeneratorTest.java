package com.codesync.session.application;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.service.CodeFingerprintGenerator;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.SourceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeFingerprintGeneratorTest {

    private CodeFingerprintGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CodeFingerprintGenerator();
    }

    @Test
    void shouldGenerateSameFingerprintForIdenticalCode() {

        String code = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        assertSameFingerprint(code, code);
    }

    @Test
    void shouldIgnoreWhitespaceChanges() {

        String code1 = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
                int   add( int a,int b )
                {
                    return   a+b;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreLineBreakChanges() {

        String code1 = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        String code2 =
                "int add(int a, int b) { return a + b; }";

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreIndentationChanges() {

        String code1 = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
        int add(int a, int b) {
        return a + b;
        }
        """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreLineComments() {

        String code1 = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
                // This function adds two numbers
                int add(int a, int b) {
                    // calculate result
                    return a + b; // return result
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreBlockComments() {

        String code1 = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
                /*
                 * Adds two numbers.
                 */
                int add(int a, int b) {
                    /*
                     * calculation
                     */
                    return a + b;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreVariableRenaming() {

        String code1 = """
                int add(int a, int b) {
                    int result = a + b;
                    return result;
                }
                """;

        String code2 = """
                int add(int x, int y) {
                    int total = x + y;
                    return total;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreFunctionRenaming() {

        String code1 = """
                int addNumbers(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
                int calculateSum(int x, int y) {
                    return x + y;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreMultipleIdentifierRenames() {

        String code1 = """
                int calculate(int first, int second) {
                    int result = first * second;
                    return result;
                }
                """;

        String code2 = """
                int multiply(int left, int right) {
                    int answer = left * right;
                    return answer;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldPreserveIdentifierRelationships() {

        String code1 = """
                int calculate(int a, int b) {
                    return a + a;
                }
                """;

        String code2 = """
                int calculate(int x, int y) {
                    return x + y;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentOperator() {

        String addition = """
                int calculate(int a, int b) {
                    return a + b;
                }
                """;

        String multiplication = """
                int calculate(int a, int b) {
                    return a * b;
                }
                """;

        assertDifferentFingerprint(
                addition,
                multiplication
        );
    }

    @Test
    void shouldDetectDifferentComparisonOperator() {

        String code1 = """
                boolean check(int a, int b) {
                    return a > b;
                }
                """;

        String code2 = """
                boolean check(int a, int b) {
                    return a >= b;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentReturnValue() {

        String code1 = """
                int getValue() {
                    return 10;
                }
                """;

        String code2 = """
                int getValue() {
                    return 20;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldPreserveStringLiteralChanges() {

        String code1 = """
                String message() {
                    return "hello";
                }
                """;

        String code2 = """
                String message() {
                    return "world";
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldPreserveStringLiteralWhitespace() {

        String code1 = """
                String message() {
                    return "hello world";
                }
                """;

        String code2 = """
                String message() {
                    return "helloworld";
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldPreserveNumericLiteralChanges() {

        String code1 = """
                int value() {
                    return 100;
                }
                """;

        String code2 = """
                int value() {
                    return 101;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentConditionLogic() {

        String code1 = """
                boolean valid(int value) {
                    if (value > 0) {
                        return true;
                    }
                    return false;
                }
                """;

        String code2 = """
                boolean valid(int value) {
                    if (value < 0) {
                        return true;
                    }
                    return false;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentLogicalExpression() {

        String code1 = """
                boolean valid(int a, int b) {
                    return a > 0 && b > 0;
                }
                """;

        String code2 = """
                boolean valid(int a, int b) {
                    return a > 0 || b > 0;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentStatementOrder() {

        String code1 = """
                int calculate(int a, int b) {
                    int result = a + b;
                    result = result * 2;
                    return result;
                }
                """;

        String code2 = """
                int calculate(int a, int b) {
                    int result = a * 2;
                    result = result + b;
                    return result;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentParameterOrder() {

        String code1 = """
                int calculate(int a, int b) {
                    return a - b;
                }
                """;

        String code2 = """
                int calculate(int a, int b) {
                    return b - a;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentType() {

        String code1 = """
                int calculate(int a, int b) {
                    return a + b;
                }
                """;

        String code2 = """
                long calculate(long a, long b) {
                    return a + b;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldDetectDifferentLoopCondition() {

        String code1 = """
                int calculate(int n) {
                    int result = 0;
                    for (int i = 0; i < n; i++) {
                        result += i;
                    }
                    return result;
                }
                """;

        String code2 = """
                int calculate(int n) {
                    int result = 0;
                    for (int i = 0; i <= n; i++) {
                        result += i;
                    }
                    return result;
                }
                """;

        assertDifferentFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreRenamedLoopVariable() {

        String code1 = """
                int calculate(int n) {
                    int result = 0;
                    for (int i = 0; i < n; i++) {
                        result += i;
                    }
                    return result;
                }
                """;

        String code2 = """
                int calculate(int n) {
                    int total = 0;
                    for (int index = 0; index < n; index++) {
                        total += index;
                    }
                    return total;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldIgnoreRenamedClassAndMethodIdentifiers() {

        String code1 = """
                class Calculator {
                    int add(int a, int b) {
                        return a + b;
                    }
                }
                """;

        String code2 = """
                class MathHelper {
                    int sum(int x, int y) {
                        return x + y;
                    }
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldPreserveKeywords() {

        String code1 = """
                int calculate(int value) {
                    if (value > 0) {
                        return value;
                    }
                    return 0;
                }
                """;

        String code2 = """
                int calculate(int x) {
                    if (x > 0) {
                        return x;
                    }
                    return 0;
                }
                """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldTreatDifferentLanguagesAsDifferentFingerprints() {

        String code = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        CodeFingerprint javaFingerprint =
                generate(
                        code,
                        ProgrammingLanguage.JAVA
                );

        CodeFingerprint cppFingerprint =
                generate(
                        code,
                        ProgrammingLanguage.CPP
                );

        assertThat(javaFingerprint.value())
                .isNotEqualTo(
                        cppFingerprint.value()
                );
    }

    @Test
    void shouldGenerateStableSha256Fingerprint() {

        String code = """
                int add(int a, int b) {
                    return a + b;
                }
                """;

        CodeFingerprint fingerprint =
                generate(
                        code,
                        ProgrammingLanguage.JAVA
                );

        assertThat(fingerprint.value())
                .hasSize(64)
                .matches("[0-9a-f]{64}");
    }

    @Test
    void shouldRejectNullSourceCode() {

        assertThat(
                org.assertj.core.api.Assertions
                        .catchThrowable(() ->
                                generator.generate(
                                        null,
                                        ProgrammingLanguage.JAVA
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Source code cannot be null."
                );
    }

    @Test
    void shouldRejectNullLanguage() {

        SourceCode source =
                new SourceCode(
                        "int add(int a, int b) { return a + b; }"
                );

        assertThat(
                org.assertj.core.api.Assertions
                        .catchThrowable(() ->
                                generator.generate(
                                        source,
                                        null
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Programming language cannot be null."
                );
    }

    @Test
    void shouldRejectBlankSourceCode() {

        assertThat(
                org.assertj.core.api.Assertions.catchThrowable(() ->
                        new SourceCode("   ")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source code cannot be null or blank.");
    }

    @Test
    void shouldGenerateSameFingerprintForDifferentComments() {

        String code1 = """
            // This calculates the sum
            int add(int a, int b) {
                return a + b;
            }
            """;

        String code2 = """
            /*
             * Completely different comment.
             * This comment should not affect the fingerprint.
             */
            int add(int a, int b) {
                return a + b;
            }
            """;

        assertSameFingerprint(code1, code2);
    }

    @Test
    void shouldNotTreatDifferentIdentifiersAsSameWhenTheirRelationshipsDiffer() {

        String code1 = """
                int calculate(int a, int b) {
                    int c = a + b;
                    return c;
                }
                """;

        String code2 = """
                int calculate(int x, int y) {
                    int z = x * y;
                    return z;
                }
                """;

        assertDifferentFingerprint(
                code1,
                code2
        );
    }

    @Test
    void shouldIgnoreRenamingAcrossMultipleScopes() {

        String code1 = """
                int calculate(int a) {
                    int result = a;

                    if (result > 0) {
                        int doubled = result * 2;
                        return doubled;
                    }

                    return result;
                }
                """;

        String code2 = """
                int compute(int value) {
                    int answer = value;

                    if (answer > 0) {
                        int multiplied = answer * 2;
                        return multiplied;
                    }

                    return answer;
                }
                """;

        assertSameFingerprint(
                code1,
                code2
        );
    }

    private void assertSameFingerprint(
            String code1,
            String code2) {

        CodeFingerprint fingerprint1 =
                generate(
                        code1,
                        ProgrammingLanguage.JAVA
                );

        CodeFingerprint fingerprint2 =
                generate(
                        code2,
                        ProgrammingLanguage.JAVA
                );

        assertThat(fingerprint1.value())
                .isEqualTo(
                        fingerprint2.value()
                );
    }

    private void assertDifferentFingerprint(
            String code1,
            String code2) {

        CodeFingerprint fingerprint1 =
                generate(
                        code1,
                        ProgrammingLanguage.JAVA
                );

        CodeFingerprint fingerprint2 =
                generate(
                        code2,
                        ProgrammingLanguage.JAVA
                );

        assertThat(fingerprint1.value())
                .isNotEqualTo(
                        fingerprint2.value()
                );
    }

    private CodeFingerprint generate(
            String code,
            ProgrammingLanguage language) {

        return generator.generate(
                new SourceCode(code),
                language
        );
    }
}