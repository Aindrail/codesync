package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "submission_attempt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_attempt_number",
                        columnNames = {"session_id", "attempt_number"}
                )
        }
)
public class SubmissionAttemptEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "session_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_session")
    )
    private CodingSessionEntity session;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "platform_submission_id")
    private String platformSubmissionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "solution_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_solution")
    )
    private SolutionEntity solution;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "execution_result_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_execution_result")
    )
    private ExecutionResultEntity executionResult;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected SubmissionAttemptEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public CodingSessionEntity getSession() {
        return session;
    }

    public void setSession(CodingSessionEntity session) {
        this.session = session;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getPlatformSubmissionId() {
        return platformSubmissionId;
    }

    public void setPlatformSubmissionId(String platformSubmissionId) {
        this.platformSubmissionId = platformSubmissionId;
    }

    public SolutionEntity getSolution() {
        return solution;
    }

    public void setSolution(SolutionEntity solution) {
        this.solution = solution;
    }

    public ExecutionResultEntity getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(ExecutionResultEntity executionResult) {
        this.executionResult = executionResult;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}