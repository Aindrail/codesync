package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_app_user_github_user_id",
                        columnNames = "github_user_id"
                )
        }
)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "github_user_id",
            nullable = false,
            unique = true
    )
    private String githubUserId;

    protected UserEntity() {
        // Required by JPA
    }

    public static UserEntity create() {
        return new UserEntity();
    }

    public Long getId() {
        return id;
    }

    public String getGithubUserId() {
        return githubUserId;
    }

    public void setGithubUserId(String githubUserId) {
        this.githubUserId = githubUserId;
    }
}