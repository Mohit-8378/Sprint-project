package com.cg.admission.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import com.cg.admission.service.ProgramScheduledService;

public class CourseServiceImplTest {

    // Mocking the repositories and services
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ProgramScheduledRepository programScheduledRepository;

    @Mock
    private ProgramScheduledService programScheduledService;

    // Injecting mocks into the service implementation
    @InjectMocks
    private CourseServiceImpl courseServiceImpl;

    // Initializing mocks before each test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for successful course addition
    @Test
    void testAddCourse_Success() {
        Long collegeRegId = 1L;
        Long programId = 1L;
        Course course = new Course();
        course.setCourseName("Test Course");
        College college = new College();
        Program program = new Program();
        program.setCourses(new ArrayList<>());

        // Mocking repository calls
        when(collegeRepository.findById(collegeRegId)).thenReturn(Optional.of(college));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(courseRepository.save(course)).thenReturn(course);

        // Invoking the service method
        CourseDto result = courseServiceImpl.addCourse(collegeRegId, programId, course);

        // Asserting the results
        assertNotNull(result);
        verify(collegeRepository, times(1)).findById(collegeRegId);
        verify(programRepository, times(1)).findById(programId);
        verify(courseRepository, times(1)).save(course);
    }

    // Test case for adding a course that already exists
    @Test
    void testAddCourse_AlreadyExists() {
        Long collegeRegId = 1L;
        Long programId = 1L;
        Course course = new Course();
        course.setCourseName("Test Course");
        College college = new College();
        Program program = new Program();
        program.setCourses(List.of(course));

        // Mocking repository calls
        when(collegeRepository.findById(collegeRegId)).thenReturn(Optional.of(college));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.addCourse(collegeRegId, programId, course);
        });

        // Verifying interactions
        verify(collegeRepository, times(1)).findById(collegeRegId);
        verify(programRepository, times(1)).findById(programId);
        verify(courseRepository, times(0)).save(course);
    }

    // Test case for viewing all course details successfully
    @Test
    void testViewAllCourseDetails_Success() {
        List<Course> courseList = new ArrayList<>();
        Course course = new Course();
        courseList.add(course);

        // Mocking repository call
        when(courseRepository.findAll()).thenReturn(courseList);

        // Invoking the service method
        List<CourseDto> result = courseServiceImpl.viewAllCourseDetails();

        // Asserting the results
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    // Test case for no courses found
    @Test
    void testViewAllCourseDetails_NoCoursesFound() {
        // Mocking repository call
        when(courseRepository.findAll()).thenReturn(new ArrayList<>());

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.viewAllCourseDetails();
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findAll();
    }

    // Test case for successfully deleting a course by ID
    @Test
    void testDeleteCourseById_Success() {
        Long courseId = 1L;
        Course course = new Course();
        List<ProgramScheduled> programScheduledList = new ArrayList<>();

        // Mocking repository calls
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(programScheduledRepository.findByCourse_CourseId(courseId)).thenReturn(programScheduledList);

        // Invoking the service method
        String result = courseServiceImpl.deleteCourseById(courseId);

        // Asserting the results
        assertEquals("Course Deleted Succesfully", result);
        verify(courseRepository, times(1)).findById(courseId);
        verify(programScheduledRepository, times(1)).findByCourse_CourseId(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    // Test case for no course found when deleting by ID
    @Test
    void testDeleteCourseById_NoCourseFound() {
        Long courseId = 1L;

        // Mocking repository call
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.deleteCourseById(courseId);
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(0)).deleteById(courseId);
    }

    // Test case for successfully updating course details
    @Test
    void testUpdateCourseDetails_Success() {
        Long courseId = 1L;
        Course updatedCourse = new Course();
        updatedCourse.setCourseName("Updated Course");
        updatedCourse.setEligibility("New Eligibility");
        Course existingCourse = new Course();
        existingCourse.setCourseId(courseId);

        // Mocking repository calls
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);

        // Invoking the service method
        Long result = courseServiceImpl.updateCourseDetails(courseId, updatedCourse);

        // Asserting the results
        assertEquals(courseId, result);
        assertEquals("Updated Course", existingCourse.getCourseName());
        assertEquals("New Eligibility", existingCourse.getEligibility());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(existingCourse);
    }

    // Test case for no course found when updating details
    @Test
    void testUpdateCourseDetails_NoCourseFound() {
        Long courseId = 1L;
        Course course = new Course();

        // Mocking repository call
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.updateCourseDetails(courseId, course);
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    // Test case for successfully finding course details by course name
    @Test
    void testFindCourseDetailsByCourseName_Success() {
        String courseName = "Test Course";
        Course course = new Course();
        course.setCourseName(courseName);

        // Mocking repository call
        when(courseRepository.findCourseDetailsByCourseName(courseName)).thenReturn(course);

        // Invoking the service method
        CourseDto result = courseServiceImpl.findCourseDetailsByCourseName(courseName);

        // Asserting the results
        assertNotNull(result);
        verify(courseRepository, times(1)).findCourseDetailsByCourseName(courseName);
    }

    // Test case for no course found when searching by course name
    @Test
    void testFindCourseDetailsByCourseName_NoCourseFound() {
        String courseName = "Non-Existent Course";

        // Mocking repository call
        when(courseRepository.findCourseDetailsByCourseName(courseName)).thenReturn(null);

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.findCourseDetailsByCourseName(courseName);
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findCourseDetailsByCourseName(courseName);
    }

    // Test case for successfully finding course details by eligibility criteria
    @Test
    void testFindCourseDetailsByEligibility_Success() {
        String eligibility = "Eligibility Criteria";
        List<Course> courseList = new ArrayList<>();
        Course course = new Course();
        course.setEligibility(eligibility);
        courseList.add(course);

        // Mocking repository call
        when(courseRepository.findCourseDetailsByEligibility(eligibility)).thenReturn(courseList);

        // Invoking the service method
        List<CourseDto> result = courseServiceImpl.findCourseDetailsByEligibility(eligibility);

        // Asserting the results
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findCourseDetailsByEligibility(eligibility);
    }

    // Test case for no courses found when searching by eligibility criteria
    @Test
    void testFindCourseDetailsByEligibility_NoCoursesFound() {
        String eligibility = "Non-Existent Eligibility";

        // Mocking repository call
        when(courseRepository.findCourseDetailsByEligibility(eligibility)).thenReturn(new ArrayList<>());

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.findCourseDetailsByEligibility(eligibility);
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findCourseDetailsByEligibility(eligibility);
    }

    // Test case for successfully deleting a course by course name
    @Test
    void testDeleteCourseByCourseName_Success() {
        String courseName = "Test Course";
        Course course = new Course();
        List<ProgramScheduled> programScheduledList = new ArrayList<>();

        // Mocking repository calls
        when(courseRepository.findCourseDetailsByCourseName(courseName)).thenReturn(course);
        when(programScheduledRepository.findByCourse_CourseId(course.getCourseId())).thenReturn(programScheduledList);

        // Invoking the service method
        String result = courseServiceImpl.deleteCourseByCourseName(courseName);

        // Asserting the results
        assertEquals("Course Deleted Succesfully", result);
        verify(courseRepository, times(1)).findCourseDetailsByCourseName(courseName);
        verify(programScheduledRepository, times(1)).findByCourse_CourseId(course.getCourseId());
        verify(courseRepository, times(1)).deleteByCourseName(courseName);
    }

    // Test case for no course found when deleting by course name
    @Test
    void testDeleteCourseByCourseName_NoCourseFound() {
        String courseName = "Non-Existent Course";

        // Mocking repository call
        when(courseRepository.findCourseDetailsByCourseName(courseName)).thenReturn(null);

        // Asserting that the appropriate exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            courseServiceImpl.deleteCourseByCourseName(courseName);
        });

        // Verifying interactions
        verify(courseRepository, times(1)).findCourseDetailsByCourseName(courseName);
    }

    // Test case for successfully finding course details by course ID
    @Test
    void testFindCourseDetailsByCourseId_Success() {
        Long courseId = 1L;
        Course course = new Course();
        course.setCourseId(courseId);

        // Mocking repository call
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Invoking the service method
        CourseDto result = courseServiceImpl.findCourseDetailsByCourseId(courseId);

        // Asserting the results
        assertNotNull(result);
        verify(courseRepository, times(1)).findById(courseId);
    }

    // Test case for successfully finding branches by course ID
    @Test
    void testFindBranchByCourse_Success() {
        Long courseId = 1L;
        Course course = new Course();
        List<Branch> branchList = new ArrayList<>();
        course.setBranches(branchList);

        // Mocking repository call
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Invoking the service method
        List<Branch> result = courseServiceImpl.findBranchByCourse(courseId);

        // Asserting the results
        assertNotNull(result);
        assertEquals(branchList, result);
        verify(courseRepository, times(1)).findById(courseId);
    }

}
