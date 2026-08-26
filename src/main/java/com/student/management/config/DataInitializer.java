package com.student.management.config;

import com.student.management.model.Student;
import com.student.management.model.User;
import com.student.management.service.StudentService;
import com.student.management.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService, StudentService studentService) {
        return args -> {
            // Seed Admin User
            if (userService.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", "admin123", "System Administrator", "admin@university.edu");
                userService.registerUser(admin, "ROLE_ADMIN");
            }

            // Seed Sample Students if database is empty
            if (studentService.getAllStudents().isEmpty()) {
                studentService.saveStudent(new Student("STU1001", "Alice", "Smith", "alice.smith@university.edu", "Computer Science", 3.85, LocalDate.of(2022, 9, 1)));
                studentService.saveStudent(new Student("STU1002", "Bob", "Johnson", "bob.johnson@university.edu", "Electrical Engineering", 3.60, LocalDate.of(2021, 9, 1)));
                studentService.saveStudent(new Student("STU1003", "Carol", "Williams", "carol.williams@university.edu", "Mechanical Engineering", 3.92, LocalDate.of(2023, 1, 15)));
                studentService.saveStudent(new Student("STU1004", "David", "Brown", "david.brown@university.edu", "Computer Science", 3.45, LocalDate.of(2022, 9, 1)));
                studentService.saveStudent(new Student("STU1005", "Emma", "Davis", "emma.davis@university.edu", "Business Administration", 3.78, LocalDate.of(2021, 9, 1)));
            }
        };
    }
}
