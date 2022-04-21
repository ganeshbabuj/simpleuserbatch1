CREATE TABLE IF NOT EXISTS user  (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    import_job_id BIGINT,
    PRIMARY KEY (id)
);