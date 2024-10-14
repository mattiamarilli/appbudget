CREATE USER IF NOT EXISTS 'testuser'@'%' IDENTIFIED BY 'testpassword';
GRANT ALL PRIVILEGES ON * . * TO 'testuser'@'%';
CREATE DATABASE IF NOT EXISTS appbudget;