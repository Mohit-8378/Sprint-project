package com.cg.admission.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.admission.dto.CourseDto;
import com.cg.admission.entity.Branch;
import com.cg.admission.entity.College;
import com.cg.admission.entity.Course;
import com.cg.admission.entity.Program;
import com.cg.admission.entity.ProgramScheduled;
import com.cg.admission.exceptions.ResourceNotFoundException;
import com.cg.admission.repository.CollegeRepository;
import com.cg.admission.repository.CourseRepository;
import com.cg.admission.repository.ProgramRepository;
import com.cg.admission.repository.ProgramScheduledRepository;
import com.cg.admission.service.CourseService;
import com.cg.admission.service.ProgramScheduledService;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramScheduledRepository programScheduledRepository;

    @Autowired
    private ProgramScheduledService programScheduledService;

    // Method to add a new course to a specific college and program
    @Override
    public CourseDto addCourse(Long collegeRegId, Long programId, Course course) {
        CourseDto courseDto = new CourseDto();

        // Retrieve the college and program by their IDs
        College college = collegeRepository.findById(collegeRegId).get();
        Program program = programRepository.findById(programId).get();

        // Check if the course already exists in the program
        List<Course> c = program.getCourses();
        for (Course i : c) {
            if (i.getCourseName().equalsIgnoreCase(course.getCourseName())) {
                throw new ResourceNotFoundException("Course already exists in Program");
            }
        }

        // Add the course to the program and college
        program.getCourses().add(course); 
        college.getCourseList().add(course);

        // Save the course
        courseRepository.save(course);

        // Copy properties from the course entity to the DTO
        BeanUtils.copyProperties(course, courseDto);

        return courseDto;
    }

    // Method to view all course details
    @Override
    public List<CourseDto> viewAllCourseDetails() {
        List<Course> courseList = courseRepository.findAll();

        // Convert the list of course entities to a list of course DTOs
        List<CourseDto> courseDtoList = courseList.stream().map(i -> {
            CourseDto courseDto = new CourseDto();
            BeanUtils.copyProperties(i, courseDto);
            return courseDto;
        }).collect(Collectors.toList());

        // Throw an exception if no courses are found
        if (courseDtoList.isEmpty()) {
            throw new ResourceNotFoundException("No Courses found");
        }

        return courseDtoList;
    }

    // Method to delete a course by its ID
    @Override
    public String deleteCourseById(Long courseId) {
        // Check if the course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No Course found with Id " + courseId));

        // Delete associated program schedules
        List<ProgramScheduled> list = programScheduledRepository.findByCourse_CourseId(courseId);
        for (ProgramScheduled j : list) {
            programScheduledService.deleteProgramScheduledById(j.getScheduledId());
        }
        
        // Delete the course
        courseRepository.deleteById(courseId);
        return "Course Deleted Succesfully";
    }

    // Method to update course details
    @Override
    public Long updateCourseDetails(Long courseId, Course course) {
        Course courseExisted = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No Course found "));

        // Update course details
        courseExisted.setCourseName(course.getCourseName());
        courseExisted.setEligibility(course.getEligibility());
        courseRepository.save(courseExisted);

        return courseId;
    }

    // Method to find course details by course name
    @Override
    public CourseDto findCourseDetailsByCourseName(String courseName) {
        CourseDto courseDto = new CourseDto();
        Course course = courseRepository.findCourseDetailsByCourseName(courseName);
        
        // Throw an exception if no course is found
        if (course == null) {
            throw new ResourceNotFoundException("No Course Found with Course Name " + courseName);
        }

        // Copy properties from the course entity to the DTO
        BeanUtils.copyProperties(course, courseDto);
        return courseDto;
    }

    // Method to find course details by eligibility
    @Override
    public List<CourseDto> findCourseDetailsByEligibility(String eligibility) {
        List<Course> courseListByEligibility = courseRepository.findCourseDetailsByEligibility(eligibility);

        // Convert the list of course entities to a list of course DTOs
        List<CourseDto> courseDtos = courseListByEligibility.stream().map(i -> {
            CourseDto courseDto = new CourseDto();
            BeanUtils.copyProperties(i, courseDto);
            return courseDto;
        }).collect(Collectors.toList());

        // Throw an exception if no courses are found
        if (courseListByEligibility.isEmpty()) {
            throw new ResourceNotFoundException("No Details by " + eligibility + " is present");
        }

        return courseDtos;
    }

    // Method to delete a course by its name
    @Override
    @Transactional//This annotation is used to ensure that a method is executed within a transaction. 
    //If any operation within the method fails, all operations will be rolled back to maintain data integrity. 
    public String deleteCourseByCourseName(String courseName) {
        Course deletedCourse = courseRepository.findCourseDetailsByCourseName(courseName);

        // Throw an exception if no course is found
        if (deletedCourse == null) {
            throw new ResourceNotFoundException("No Course found with Course Name " + courseName);
        }

        // Delete associated program schedules
        List<ProgramScheduled> list = programScheduledRepository.findByCourse_CourseId(deletedCourse.getCourseId());
        for (ProgramScheduled j : list) {
            programScheduledService.deleteProgramScheduledById(j.getScheduledId());
        }
        
        // Delete the course
        courseRepository.deleteByCourseName(courseName);
        return "Course Deleted Succesfully";
    }

    // Method to find course details by course ID
    @Override
    public CourseDto findCourseDetailsByCourseId(Long courseId) {
        CourseDto courseDto = new CourseDto();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No Course Found with Id " + courseId));

        // Copy properties from the course entity to the DTO
        BeanUtils.copyProperties(course, courseDto);
        return courseDto;
    }

    // Method to find branches associated with a course
    @Override
    public List<Branch> findBranchByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No Course Found with Id " + courseId));
        List<Branch> branches = course.getBranches();
        return branches;
    }
}
