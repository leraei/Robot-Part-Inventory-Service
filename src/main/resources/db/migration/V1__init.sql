create schema if not exists test;

CREATE TABLE test.robot_part (
    serialNumber BIGINT,
    manufacturer varchar(255),
    weight BIGINT,
    partName varchar(255)
)