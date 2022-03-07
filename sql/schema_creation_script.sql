CREATE DATABASE IF NOT EXISTS todo_list;

USE todo_list;

CREATE TABLE IF NOT EXISTS users
(
    u_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    u_login    VARCHAR(60) UNIQUE,
    u_password VARCHAR(60)
);

CREATE TABLE IF NOT EXISTS tasks
(
    t_id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    t_user_id        BIGINT REFERENCES users (u_id),
    t_title          VARCHAR(50),
    t_description    TEXT(300),
    t_status         ENUM ('PLANNED', 'IN_PROGRESS', 'DONE'),
    t_start_datetime TIMESTAMP,
    t_end_datetime   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_files
(
    tf_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    tf_task_id   BIGINT REFERENCES tasks (t_id),
    tf_uuid      CHAR(36),
    tf_file_name VARCHAR(256)
);