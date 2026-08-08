package com.codesync.session.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Table(
        name = "platform_problem",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_platform_problem",
                        columnNames = {"platform", "platform_problem_id"}
                )
        }
)
public class PlatformProblemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_problem_id", nullable = false)
    private String platformProblemId;

    @Column(name = "frontend_problem_id")
    private String frontendProblemId;

    @Column(name = "canonical_problem_key")
    private String canonicalProblemKey;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String platform;

    @Column(name = "official_difficulty")
    private String officialDifficulty;

    @Column(nullable = false)
    private boolean premium;

    @Column(name = "problem_version")
    private String problemVersion;

    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProblemTagEntity> tags = new java.util.ArrayList<>();

    protected PlatformProblemEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getPlatformProblemId() {
        return platformProblemId;
    }

    public void setPlatformProblemId(String platformProblemId) {
        this.platformProblemId = platformProblemId;
    }

    public String getFrontendProblemId() {
        return frontendProblemId;
    }

    public void setFrontendProblemId(String frontendProblemId) {
        this.frontendProblemId = frontendProblemId;
    }

    public String getCanonicalProblemKey() {
        return canonicalProblemKey;
    }

    public void setCanonicalProblemKey(String canonicalProblemKey) {
        this.canonicalProblemKey = canonicalProblemKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOfficialDifficulty() {
        return officialDifficulty;
    }

    public void setOfficialDifficulty(String officialDifficulty) {
        this.officialDifficulty = officialDifficulty;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getProblemVersion() {
        return problemVersion;
    }

    public void setProblemVersion(String problemVersion) {
        this.problemVersion = problemVersion;
    }
    public List<ProblemTagEntity> getTags() {
        return tags;
    }

    public void setTags(java.util.List<ProblemTagEntity> tags) {
        this.tags = tags;
    }

    public void addTag(ProblemTagEntity tag) {
        tags.add(tag);
        tag.setProblem(this);
    }

    public void removeTag(ProblemTagEntity tag) {
        tags.remove(tag);
        tag.setProblem(null);
    }
}