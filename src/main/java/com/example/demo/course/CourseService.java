package com.example.demo.course;

import com.example.demo.course.exceptions.CourseDoesNotExistException;
import com.example.demo.course.exceptions.NameTakenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getCourses(){
        return courseRepository.findAll();
    }

    public void addNewCourse(Course course) {
        checkName(course);
        courseRepository.save(course);

    }

    public void deleteCourse(Long studentId) {
        boolean exists = courseRepository.existsById(studentId);
        if(!exists){
            throw new CourseDoesNotExistException(CourseDoesNotExistException.COURSE_DO_NOT_EXIST + studentId);
        }
        courseRepository.deleteById(studentId);
    }

    @Transactional
    public void updateCourse(Long courseId,String name, Integer amountOfPoints, Integer maxNumberOfStudents, Boolean mandatory) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseDoesNotExistException(CourseDoesNotExistException.COURSE_DO_NOT_EXIST + courseId));

        if(name != null && name.length() > 0 && !Objects.equals(course.getName(), name)){
            course.setName(name);
        }

        if(amountOfPoints != null && amountOfPoints > 0 && !Objects.equals(course.getAmountOfPoints(), amountOfPoints)){
            course.setAmountOfPoints(amountOfPoints);
        }

        if(amountOfPoints != null && maxNumberOfStudents > 0 && !Objects.equals(course.getMaxNumberOfStudents(), maxNumberOfStudents)){
            course.setMaxNumberOfStudents(maxNumberOfStudents);
        }

        course.setMandatory(mandatory);

    }

    public void checkName(Course course) {
        Optional<Course> courseOptional = courseRepository.findCourseByName(course.getName());
        if (courseOptional.isPresent()){
            throw new NameTakenException(NameTakenException.NAME_TAKEN_EXCEPTION);
        }
    }
}
