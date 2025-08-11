-- Create database
CREATE DATABASE student_db;
USE student_db;

-- Create table that matches your Student class and DAO queries
CREATE TABLE students (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

-- Optional: Insert sample data
INSERT INTO students (id, name, department, age) VALUES
('22B91A0537', 'abhiram', 'CSE', 20),
('22B91A1289', 'Bhanu Prakash', 'IT', 20),
('22B91A12A0', 'Mahesh Babu', 'IT', 20),
('22B91A12H8', 'Mohan', 'IT', 20),
('22B91A54F8', 'Rahul Bhushan', 'AIDS', 20);
select * from students;
