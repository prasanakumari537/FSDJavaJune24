package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student("STU1001", "John", "Doe", "john.doe@test.com", "Computer Science", 3.8, LocalDate.of(2023, 9, 1));
        student1.setId(1L);

        student2 = new Student("STU1002", "Jane", "Smith", "jane.smith@test.com", "Data Science", 3.9, LocalDate.of(2023, 9, 1));
        student2.setId(2L);
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        List<Student> students = studentService.getAllStudents();

        assertEquals(2, students.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        Optional<Student> found = studentService.getStudentById(1L);

        assertTrue(found.isPresent());
        assertEquals("STU1001", found.get().getStudentId());
    }

    @Test
    void testSaveStudentSuccess() {
        Student newStudent = new Student("STU1003", "Bob", "Wilson", "bob@test.com", "Math", 3.5, LocalDate.now());
        when(studentRepository.findByStudentId("STU1003")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("bob@test.com")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        Student saved = studentService.saveStudent(newStudent);

        assertNotNull(saved);
        assertEquals("STU1003", saved.getStudentId());
        verify(studentRepository, times(1)).save(newStudent);
    }

    @Test
    void testSaveStudentDuplicateStudentIdThrowsException() {
        Student duplicate = new Student("STU1001", "Bob", "Wilson", "newbob@test.com", "Math", 3.5, LocalDate.now());
        when(studentRepository.findByStudentId("STU1001")).thenReturn(Optional.of(student1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.saveStudent(duplicate);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testUpdateStudentSuccess() {
        Student updateInfo = new Student("STU1001", "John", "Updated", "john.doe@test.com", "Computer Science", 3.95, LocalDate.of(2023, 9, 1));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        Student updated = studentService.updateStudent(1L, updateInfo);

        assertEquals("Updated", updated.getLastName());
        assertEquals(3.95, updated.getGpa());
    }

    @Test
    void testDeleteStudentSuccess() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchStudents() {
        when(studentRepository.searchStudents("Computer")).thenReturn(List.of(student1));

        List<Student> results = studentService.searchStudents("Computer");

        assertEquals(1, results.size());
        assertEquals("STU1001", results.get(0).getStudentId());
    }
}
