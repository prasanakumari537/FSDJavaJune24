package com.student.management.controller;

import com.student.management.model.Student;
import com.student.management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student("STU1001", "John", "Doe", "john.doe@test.com", "Computer Science", 3.8, LocalDate.of(2023, 9, 1));
        sampleStudent.setId(1L);
    }

    @Test
    void testAccessStudentsUnauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListStudentsAuthenticated() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(sampleStudent));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/index"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchStudentsAuthenticated() throws Exception {
        when(studentService.searchStudents("John")).thenReturn(List.of(sampleStudent));

        mockMvc.perform(get("/students").param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/index"))
                .andExpect(model().attribute("keyword", "John"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("pageTitle", "Add New Student"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveStudentSuccess() throws Exception {
        when(studentService.saveStudent(any(Student.class))).thenReturn(sampleStudent);

        mockMvc.perform(post("/students")
                        .with(csrf())
                        .param("studentId", "STU1001")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@test.com")
                        .param("department", "Computer Science")
                        .param("gpa", "3.8")
                        .param("enrollmentDate", "2023-09-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowEditForm() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(sampleStudent));

        mockMvc.perform(get("/students/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteStudent() throws Exception {
        mockMvc.perform(get("/students/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
