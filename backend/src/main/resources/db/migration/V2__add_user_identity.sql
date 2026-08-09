CREATE TABLE app_user (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          github_user_id VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          CONSTRAINT uk_app_user_github_user_id
                              UNIQUE (github_user_id)
);