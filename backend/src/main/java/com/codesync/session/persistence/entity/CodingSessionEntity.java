package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "coding_session",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coding_session_session_id",
                        columnNames = "session_id"
                )
        }
)
public class CodingSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Domain SessionId.
     * The database Long id remains the internal surrogate primary key.
     */
    @Column(name = "session_id", nullable = false, unique = true)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_session_problem")
    )
    private PlatformProblemEntity problem;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SubmissionAttemptEntity> submissionAttempts = new ArrayList<>();

    protected CodingSessionEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public PlatformProblemEntity getProblem() {
        return problem;
    }

    public void setProblem(PlatformProblemEntity problem) {
        this.problem = problem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public List<SubmissionAttemptEntity> getSubmissionAttempts() {
        return submissionAttempts;
    }

    public void setSubmissionAttempts(
            List<SubmissionAttemptEntity> submissionAttempts) {
        this.submissionAttempts = submissionAttempts;
    }

    public void addSubmissionAttempt(SubmissionAttemptEntity attempt) {
        submissionAttempts.add(attempt);
        attempt.setSession(this);
    }

    public void removeSubmissionAttempt(SubmissionAttemptEntity attempt) {
        submissionAttempts.remove(attempt);
        attempt.setSession(null);
    }
}