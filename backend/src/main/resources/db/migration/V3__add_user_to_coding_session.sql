ALTER TABLE coding_session
    ADD COLUMN user_id INTEGER NOT NULL
        REFERENCES app_user(id);