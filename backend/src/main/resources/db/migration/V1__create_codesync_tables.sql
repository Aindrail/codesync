CREATE TABLE platform_problem (
                                  id INTEGER PRIMARY KEY AUTOINCREMENT,

                                  platform_problem_id VARCHAR(255) NOT NULL,
                                  frontend_problem_id VARCHAR(255),
                                  canonical_problem_key VARCHAR(255),

                                  title VARCHAR(500) NOT NULL,
                                  slug VARCHAR(500) NOT NULL,
                                  url VARCHAR(1000) NOT NULL,

                                  platform VARCHAR(50) NOT NULL,
                                  official_difficulty VARCHAR(100),

                                  premium BOOLEAN NOT NULL DEFAULT FALSE,
                                  problem_version VARCHAR(255),

                                  created_at TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP NOT NULL,

                                  CONSTRAINT uk_platform_problem
                                      UNIQUE (platform, platform_problem_id)
);


CREATE TABLE problem_tag (
                             id INTEGER PRIMARY KEY AUTOINCREMENT,

                             problem_id INTEGER NOT NULL,
                             tag VARCHAR(255) NOT NULL,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,

                             CONSTRAINT fk_tag_problem
                                 FOREIGN KEY (problem_id)
                                     REFERENCES platform_problem(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT uk_problem_tag
                                 UNIQUE (problem_id, tag)
);


CREATE TABLE solution (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,

                          code TEXT NOT NULL,
                          language VARCHAR(100) NOT NULL,
                          fingerprint VARCHAR(255) NOT NULL,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);


CREATE INDEX idx_solution_fingerprint
    ON solution(fingerprint);


CREATE TABLE execution_result (
                                  id INTEGER PRIMARY KEY AUTOINCREMENT,

                                  verdict VARCHAR(100) NOT NULL,

                                  runtime_in_millis INTEGER,
                                  runtime_percentile REAL,

                                  memory_in_kb INTEGER,
                                  memory_percentile REAL,

                                  total_test_cases INTEGER,
                                  passed_test_cases INTEGER,

                                  created_at TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP NOT NULL
);


CREATE TABLE coding_session (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,

                                session_id VARCHAR(36) NOT NULL,

                                problem_id INTEGER NOT NULL,

                                status VARCHAR(50) NOT NULL,

                                started_at TIMESTAMP NOT NULL,
                                ended_at TIMESTAMP,

                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL,

                                CONSTRAINT uk_coding_session_session_id
                                    UNIQUE (session_id),

                                CONSTRAINT fk_session_problem
                                    FOREIGN KEY (problem_id)
                                        REFERENCES platform_problem(id)
);


CREATE TABLE submission_attempt (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                                    session_id INTEGER NOT NULL,

                                    attempt_number INTEGER NOT NULL,
                                    platform_submission_id VARCHAR(255),

                                    solution_id INTEGER NOT NULL,
                                    execution_result_id INTEGER NOT NULL,

                                    submitted_at TIMESTAMP NOT NULL,

                                    created_at TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP NOT NULL,

                                    CONSTRAINT uk_session_attempt_number
                                        UNIQUE (session_id, attempt_number),

                                    CONSTRAINT fk_attempt_session
                                        FOREIGN KEY (session_id)
                                            REFERENCES coding_session(id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_attempt_solution
                                        FOREIGN KEY (solution_id)
                                            REFERENCES solution(id),

                                    CONSTRAINT fk_attempt_execution_result
                                        FOREIGN KEY (execution_result_id)
                                            REFERENCES execution_result(id)
);