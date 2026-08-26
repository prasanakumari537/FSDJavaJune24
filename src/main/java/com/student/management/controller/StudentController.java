package com.student.management.controller;

import com.student.management.model.Student;
import com.student.management.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Student> students;
        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentService.searchStudents(keyword);
            model.addAttribute("keyword", keyword.trim());
        } else {
            students = studentService.getAllStudents();
        }
        model.addAttribute("students", students);
        return "students/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Add New Student");
        return "students/form";
    }

    @PostMapping
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", student.getId() == null ? "Add New Student" : "Edit Student");
            return "students/form";
        }

        try {
            if (student.getId() == null) {
                studentService.saveStudent(student);
                redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            } else {
                studentService.updateStudent(student.getId(), student);
                redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            }
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", student.getId() == null ? "Add New Student" : "Edit Student");
            return "students/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    model.addAttribute("pageTitle", "Edit Student (" + student.getStudentId() + ")");
                    return "students/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Student not found with ID: " + id);
                    return "redirect:/students";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/view/{id}")
    public String viewStudentDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "students/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Student not found with ID: " + id);
                    return "redirect:/students";
                });
    }
}
