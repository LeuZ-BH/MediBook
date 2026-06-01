CREATE DATABASE IF NOT EXISTS appointment_db;
USE appointment_db;

CREATE TABLE specializations (
    spec_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role ENUM('PATIENT','DOCTOR','ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    spec_id INT NOT NULL,
    qualification VARCHAR(200),
    experience_years INT DEFAULT 0,
    bio TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (spec_id) REFERENCES specializations(spec_id)
);

CREATE TABLE doctor_slots (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('AVAILABLE','BOOKED') DEFAULT 'AVAILABLE',
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY unique_slot (doctor_id, slot_date, start_time)
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    slot_id INT NOT NULL UNIQUE,
    reason VARCHAR(500),
    status ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (slot_id) REFERENCES doctor_slots(slot_id)
);

-- Seed data
INSERT INTO specializations (name, description) VALUES
('Cardiologist', 'Heart and cardiovascular system'),
('Dermatologist', 'Skin, hair and nails'),
('Neurologist', 'Brain and nervous system'),
('Orthopedist', 'Bones and joints'),
('General Physician', 'General health and wellness');

INSERT INTO users (name, email, password_hash, phone, role) VALUES
('Admin User', 'admin@dabs.com', 'admin123', '9999999999', 'ADMIN');

