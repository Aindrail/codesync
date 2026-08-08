package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "solution",
        indexes = {
                @Index(
                        name = "idx_solution_fingerprint",
                        columnList = "fingerprint"
                )
        }
)
public class SolutionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    protected SolutionEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}