# 🏥 MediBook ( Doctor Appointment Booking System)

A full-stack web application built with **Java Spring MVC**, **Hibernate**, and **Thymeleaf** that lets patients find doctors, book appointments, and manage their healthcare — all from one place.

-----

## 💡 The Problem

Anyone who has ever tried to book a doctor’s appointment knows how frustrating it can be.

You call a clinic, get put on hold, get told the doctor isn’t available, try to find another doctor, repeat the whole process. Or you show up and wait for hours with no idea where you are in the queue. Patients often have no visibility into a doctor’s availability beforehand, and doctors have no centralized way to manage their schedule or patient load.

On top of that — if you need a specialist, you have no easy way to even know *which* doctor handles what, let alone whether they’re taking new patients or what their consultation fee is.

This kind of fragmented, phone-based system wastes everyone’s time.

-----

## ✅ How This Project Solves It

This system brings patients and doctors onto a single platform where everything is transparent and organized.

- Patients can **search for doctors** by name or specialization, see their qualifications, fee, hospital, and location — then book directly from their available slots.
- Doctors can **manage their own schedule** by creating time slots and confirming or completing appointments at their own pace.

No phone calls. No guessing. Everything in one dashboard.

-----

## 🚀 Features

### For Patients

- Register and log in as a patient
- Search doctors by **name** or **specialization**
- View doctor profiles — qualification, experience, bio, hospital, city, consultation fee
- Browse a doctor’s **available time slots**
- **Book an appointment** with reason, age, gender, blood group, and weight
- View upcoming and past appointments
- **Cancel** a pending appointment
- Personal **notes section** on the dashboard — add, edit, and delete notes
- Appointment stats on dashboard (total, upcoming, completed, cancelled)

### For Doctors

- Register with full professional details (specialization, hospital, address, consultation fee, bio)
- Dashboard with live stats — pending, confirmed, completed, cancelled counts
- **Create time slots** (date, start time, end time)
- Delete unbooked slots
- **Confirm** or **cancel** patient appointments
- Mark appointments as **completed**
- Full appointment history with status filter
- Edit profile (qualification, fee, bio, address, etc.)

### For Admins

- Secure admin dashboard
- View all registered users (patients + doctors)
- Delete users from the system
- View **all appointments** across the platform with status filters
- **Manage specializations** — add or delete doctor specializations
- Stats overview — total patients, doctors, today’s appointments, specialization count

-----

## 🛠️ Tech Stack

|Layer          |Technology                    |
|---------------|------------------------------|
|Language       |Java 17                       |
|Framework      |Spring MVC 5.3.27             |
|ORM            |Hibernate 5.6.15              |
|Templating     |Thymeleaf 3.1.1               |
|Database       |MySQL 8                       |
|Connection Pool|Apache Commons DBCP2          |
|Validation     |Hibernate Validator 6         |
|Build Tool     |Maven                         |
|Server         |Apache Tomcat (WAR deployment)|
|Frontend       |HTML5, CSS3, Vanilla JS       |

-----

## 🗂️ Project Structure

```
doctor-appointment-system/
├── src/
│   └── main/
│       ├── java/com/dabs/
│       │   ├── config/          # Spring MVC, Hibernate, App config
│       │   ├── controller/      # AuthController, PatientController, DoctorController, AdminController
│       │   ├── dao/             # DAO interfaces + implementations
│       │   ├── model/           # JPA entities (User, Doctor, DoctorSlot, Appointment, PatientNote, Specialization)
│       │   ├── service/         # Business logic layer
│       │   └── util/            # PasswordUtil (hashing)
│       └── webapp/
│           ├── static/
│           │   ├── css/         # style.css, common.css, patient.css, doctor.css
│           │   └── js/          # main.js
│           └── WEB-INF/
│               ├── templates/
│               │   ├── auth/    # login.html, register.html
│               │   ├── patient/ # dashboard, search, book, my-appointments, history, edit-note
│               │   ├── doctor/  # dashboard, manage-slots, my-appointments, history, edit-profile
│               │   └── admin/   # dashboard, all-users, all-appointments, manage-specializations
│               └── web.xml
├── schema.sql
└── pom.xml
```

-----

## 🗄️ Database Schema

The database is called `appointment_db` and has 5 tables:

- **users** — stores all users (patients, doctors, admins) with role enum
- **specializations** — doctor specializations (Cardiologist, Dermatologist, etc.)
- **doctors** — extended profile for doctor users (linked to users table)
- **doctor_slots** — time slots created by doctors with AVAILABLE/BOOKED status
- **appointments** — bookings made by patients, linked to a slot; status can be PENDING, CONFIRMED, COMPLETED, or CANCELLED

-----

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8+
- Apache Tomcat 9+

### 1. Clone the repo

```bash
git clone https://github.com/your-username/doctor-appointment-system.git
cd doctor-appointment-system
```

### 2. Set up the database

```bash
mysql -u root -p < schema.sql
```

This creates the `appointment_db` database, all tables, seeds some specializations, and creates a default admin account.

**Default Admin Login:**

```
Email:    admin@dabs.com
Password: admin123
```

### 3. Configure the database connection

Open `src/main/java/com/dabs/config/HibernateConfig.java` and update your MySQL credentials:

```java
dataSource.setUsername("your_mysql_username");
dataSource.setPassword("your_mysql_password");
```

### 4. Build the project

```bash
mvn clean package
```

This generates `target/doctor-appointment-system-1.0.war`

### 5. Deploy to Tomcat

Copy the WAR file into your Tomcat `webapps/` directory and start Tomcat. Then open:

```
http://localhost:8080/doctor-appointment-system-1.0/
```

-----

## 👥 User Roles

|Role     |Access                                                          |
|---------|----------------------------------------------------------------|
|`PATIENT`|Search doctors, book/cancel appointments, personal notes        |
|`DOCTOR` |Manage slots, confirm/complete/cancel appointments, edit profile|
|`ADMIN`  |Full system overview, manage users and specializations          |

-----

## 📸 Pages Overview

|Page                         |Route                     |
|-----------------------------|--------------------------|
|Login                        |`/login`                  |
|Register                     |`/register`               |
|Patient Dashboard            |`/patient/dashboard`      |
|Search Doctors               |`/patient/search`         |
|Book Appointment             |`/patient/book/{slotId}`  |
|My Appointments (Patient)    |`/patient/my-appointments`|
|Appointment History (Patient)|`/patient/history`        |
|Doctor Dashboard             |`/doctor/dashboard`       |
|Manage Slots                 |`/doctor/slots`           |
|My Appointments (Doctor)     |`/doctor/appointments`    |
|Appointment History (Doctor) |`/doctor/history`         |
|Edit Doctor Profile          |`/doctor/profile/edit`    |
|Admin Dashboard              |`/admin/dashboard`        |
|All Users                    |`/admin/users`            |
|All Appointments             |`/admin/appointments`     |
|Manage Specializations       |`/admin/specializations`  |

-----

## 🔐 Security Notes

- Passwords are hashed before being stored in the database
- Session-based authentication with role checks on every protected route
- Unauthorized access attempts redirect to `/login`
- Each controller validates the user’s session role before processing any request

-----

## 🌱 Seed Data (from schema.sql)

The SQL script comes with 5 default specializations:

- Cardiologist
- Dermatologist
- Neurologist
- Orthopedist
- General Physician

And one default admin user so you can log in and start managing the system immediately.

-----

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you’d like to change.

-----

