package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    @Transactional
    public Student saveStudent(Student student) {
        if (student.getId() == null) {
            if (studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
                throw new IllegalArgumentException("Student ID '" + student.getStudentId() + "' already exists.");
            }
            if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email '" + student.getEmail() + "' is already in use.");
            }
        } else {
            Optional<Student> existingByStudentId = studentRepository.findByStudentId(student.getStudentId());
            if (existingByStudentId.isPresent() && !existingByStudentId.get().getId().equals(student.getId())) {
                throw new IllegalArgumentException("Student ID '" + student.getStudentId() + "' is already assigned to another student.");
            }

            Optional<Student> existingByEmail = studentRepository.findByEmail(student.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(student.getId())) {
                throw new IllegalArgumentException("Email '" + student.getEmail() + "' is already assigned to another student.");
            }
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        // Validate uniqueness if changed
        if (!existingStudent.getStudentId().equalsIgnoreCase(updatedStudent.getStudentId())) {
            if (studentRepository.findByStudentId(updatedStudent.getStudentId()).isPresent()) {
                throw new IllegalArgumentException("Student ID '" + updatedStudent.getStudentId() + "' is already taken.");
            }
        }

        if (!existingStudent.getEmail().equalsIgnoreCase(updatedStudent.getEmail())) {
            if (studentRepository.findByEmail(updatedStudent.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email '" + updatedStudent.getEmail() + "' is already taken.");
            }
        }

        existingStudent.setStudentId(updatedStudent.getStudentId());
        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setGpa(updatedStudent.getGpa());
        existingStudent.setEnrollmentDate(updatedStudent.getEnrollmentDate());

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(keyword.trim());
    }
}
