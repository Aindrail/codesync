package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "problem_tag",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_tag",
                        columnNames = {"problem_id", "tag"}
                )
        }
)
public class ProblemTagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tag_problem")
    )
    private PlatformProblemEntity problem;

    @Column(name = "tag", nullable = false)
    private String tag;

    protected ProblemTagEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public PlatformProblemEntity getProblem() {
        return problem;
    }

    public void setProblem(PlatformProblemEntity problem) {
        this.problem = problem;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}