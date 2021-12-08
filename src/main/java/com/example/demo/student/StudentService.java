package com.example.demo.student;

import com.example.demo.course.Course;
import com.example.demo.student.exceptions.EmailTakenException;
import com.example.demo.student.exceptions.StudentDoesNotExistException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Data
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    public void addStudent(Student student) throws EmailTakenException{
        checkIfEmailTaken(student);
        studentRepository.save(student);

    }

    public void deleteStudent(Long studentId) throws StudentDoesNotExistException{
        boolean exists = studentRepository.existsById(studentId);
        if(!exists){
            throw new StudentDoesNotExistException(StudentDoesNotExistException.ERROR_THERE_IS_NO_STUDENT_WITH_ID + studentId);
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String name, String email) throws EmailTakenException, StudentDoesNotExistException {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentDoesNotExistException(StudentDoesNotExistException.ERROR_THERE_IS_NO_STUDENT_WITH_ID + studentId));

        if(name != null && name.length() > 0 && !student.getName().equals(name)){
            student.setName(name);
        }

        if (email != null && email.length() > 0 && !student.getEmail().equals(email)){
            checkIfEmailTaken(student);
            student.setEmail(email);
        }
    }

    public void checkIfEmailTaken(Student student) throws EmailTakenException{
        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()){
            throw new EmailTakenException(EmailTakenException.EMAIL_TAKEN_EXCEPTION);
        }
    }

    public List<String> getStudentCourses(Long studentId) throws StudentDoesNotExistException{
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentDoesNotExistException(StudentDoesNotExistException.ERROR_THERE_IS_NO_STUDENT_WITH_ID + studentId));

        List<String> response = new ArrayList<>();

        for(Course c: student.getCourses()){
            response.add(c.getName());
        }
        return response;
    }
}
