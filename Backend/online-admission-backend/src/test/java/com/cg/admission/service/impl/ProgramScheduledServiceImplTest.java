package com.cg.admission.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cg.admission.dto.ProgramScheduledDto;
import com.cg.admission.dto.ProgramScheduledResponseDto;
import com.cg.admission.entity.Branch;
import com.cg.admission.entity.College;
import com.cg.admission.entity.Course;
import com.cg.admission.entity.Program;
import com.cg.admission.entity.ProgramScheduled;
import com.cg.admission.entity.University;
import com.cg.admission.exceptions.ResourceNotFoundException;
import com.cg.admission.repository.ApplicationRepository;
import com.cg.admission.repository.BranchRepository;
import com.cg.admission.repository.CollegeRepository;
import com.cg.admission.repository.CourseRepository;
import com.cg.admission.repository.ProgramRepository;
import com.cg.admission.repository.ProgramScheduledRepository;
import com.cg.admission.repository.UniversityRepository;

@ExtendWith(MockitoExtension.class) // Integrates Mockito with JUnit 5

public class ProgramScheduledServiceImplTest {

    @Mock // Marks a field to be mocked
    private ProgramScheduledRepository programScheduledRepository;

    @Mock // Marks a field to be mocked
    private UniversityRepository universityRepository;

    @Mock // Marks a field to be mocked
    private CollegeRepository collegeRepository;

    @Mock // Marks a field to be mocked
    private ProgramRepository programRepository;

    @Mock // Marks a field to be mocked
    private CourseRepository courseRepository;

    @Mock // Marks a field to be mocked
    private BranchRepository branchRepository;

    @Mock // Marks a field to be mocked
    private ApplicationRepository applicationRepository;

    @Mock // Marks a field to be mocked
    private ApplicationServiceImpl applicationServiceImpl;

    @InjectMocks // Creates an instance of the class and injects the mocks
    private ProgramScheduledServiceImpl programScheduledServiceImpl;

    private ProgramScheduled programScheduled;
    private ProgramScheduledDto programScheduledDto;
    private ProgramScheduledResponseDto programScheduledResponseDto;
    private University university;
    private College college;
    private Program program;
    private Course course;
    private Branch branch;

    @BeforeEach // Method to be executed before each test
    void setUp() {
        // Initialize test data
        programScheduled = new ProgramScheduled();
        programScheduled.setScheduledId(1L);
        programScheduled.setStartDate(LocalDate.now());
        programScheduled.setEndDate(LocalDate.now().plusDays(10));

        university = new University();
        university.setUniversityId(1L);

        college = new College();
        college.setCollegeRegId(1L);

        program = new Program();
        program.setProgramId(1L);

        course = new Course();
        course.setCourseId(1L);

        branch = new Branch();
        branch.setBranchId(1L);

        programScheduled.setUniversity(university);
        programScheduled.setCollege(college);
        programScheduled.setProgram(program);
        programScheduled.setCourse(course);
        programScheduled.setBranch(branch);

        programScheduledDto = new ProgramScheduledDto();
        programScheduledResponseDto = new ProgramScheduledResponseDto();
    }

    @Test // Marks this method as a test method
    void testViewAllProgramScheduledDetails() {
        // Mock behavior
        when(programScheduledRepository.findAll()).thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.viewAllProgramScheduledDetails();

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findAll();
    }

    @Test // Marks this method as a test method
    void testDeleteProgramScheduledById() {
        // Mock behavior
        when(programScheduledRepository.findById(1L)).thenReturn(Optional.of(programScheduled));
        when(applicationRepository.findBySchedule_scheduledId(1L)).thenReturn(Collections.emptyList());

        // Execute the service call
        String result = programScheduledServiceImpl.deleteProgramScheduledById(1L);

        // Verify the results
        assertEquals("Program Scheduled deleted Succesfully", result);
        verify(programScheduledRepository, times(1)).findById(1L);
        verify(programScheduledRepository, times(1)).deleteById(1L);
    }

    @Test // Marks this method as a test method
    void testDeleteProgramScheduledById_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.deleteProgramScheduledById(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findById(1L);
    }

    @Test // Marks this method as a test method
    void testUpdateProgramScheduled() {
        // Mock behavior
        when(programScheduledRepository.findById(1L)).thenReturn(Optional.of(programScheduled));

        // Update the programScheduled object
        programScheduled.setStartDate(LocalDate.now().plusDays(1));
        programScheduled.setEndDate(LocalDate.now().plusDays(11));

        // Mock behavior
        when(programScheduledRepository.save(any(ProgramScheduled.class))).thenReturn(programScheduled);

        // Execute the service call
        ProgramScheduledDto result = programScheduledServiceImpl.updateProgramScheduled(1L, programScheduled);

        // Verify the results
        assertNotNull(result);
        verify(programScheduledRepository, times(1)).findById(1L);
        verify(programScheduledRepository, times(1)).save(any(ProgramScheduled.class));
    }

    @Test // Marks this method as a test method
    void testAddProgramSchedule() {
        // Mock behavior
        when(universityRepository.findById(1L)).thenReturn(Optional.of(university));
        when(collegeRepository.findById(1L)).thenReturn(Optional.of(college));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(programScheduledRepository.save(any(ProgramScheduled.class))).thenReturn(programScheduled);

        // Execute the service call
        ProgramScheduledDto result = programScheduledServiceImpl.addProgramSchedule(1L, 1L, 1L, 1L, 1L, programScheduled);

        // Verify the results
        assertNotNull(result);
        verify(programScheduledRepository, times(1)).save(any(ProgramScheduled.class));
    }

    @Test // Marks this method as a test method
    void testAddProgramSchedule_NotFound() {
        // Mock behavior
        when(universityRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.addProgramSchedule(1L, 1L, 1L, 1L, 1L, programScheduled);
        });

        // Verify the mock interactions
        verify(universityRepository, times(1)).findById(1L);
    }

    @Test // Marks this method as a test method
    void testFindProgramScheduledById() {
        // Mock behavior
        when(programScheduledRepository.findById(1L)).thenReturn(Optional.of(programScheduled));

        // Execute the service call
        ProgramScheduledDto result = programScheduledServiceImpl.findProgramScheduledById(1L);

        // Verify the results
        assertNotNull(result);
        verify(programScheduledRepository, times(1)).findById(1L);
    }

    @Test // Marks this method as a test method
    void testFindProgramScheduledById_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findProgramScheduledById(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findById(1L);
    }

    @Test // Marks this method as a test method
    void testFindProgramScheduledByCollege_CollegeName() {
        // Mock behavior
        when(programScheduledRepository.findProgramScheduledByCollege_CollegeName(anyString()))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findProgramScheduledByCollege_CollegeName("Test College");

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findProgramScheduledByCollege_CollegeName(anyString());
    }

    @Test // Marks this method as a test method
    void testFindProgramScheduledByCollege_CollegeName_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findProgramScheduledByCollege_CollegeName(anyString()))
            .thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findProgramScheduledByCollege_CollegeName("Test College");
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findProgramScheduledByCollege_CollegeName(anyString());
    }

    @Test // Marks this method as a test method
    void testFindByCollege_CollegeRegId() {
        // Mock behavior
        when(programScheduledRepository.findByCollege_CollegeRegId(1L))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findByCollege_CollegeRegId(1L);

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findByCollege_CollegeRegId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByCollege_CollegeRegId_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findByCollege_CollegeRegId(1L)).thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findByCollege_CollegeRegId(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findByCollege_CollegeRegId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByBranch_BranchId() {
        // Mock behavior
        when(programScheduledRepository.findByBranch_BranchId(1L))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findByBranch_BranchId(1L);

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findByBranch_BranchId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByBranch_BranchId_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findByBranch_BranchId(1L)).thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findByBranch_BranchId(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findByBranch_BranchId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByProgram_ProgramId() {
        // Mock behavior
        when(programScheduledRepository.findByProgram_ProgramId(1L))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findByProgram_ProgramId(1L);

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findByProgram_ProgramId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByProgram_ProgramId_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findByProgram_ProgramId(1L)).thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findByProgram_ProgramId(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findByProgram_ProgramId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByCourse_CourseId() {
        // Mock behavior
        when(programScheduledRepository.findByCourse_CourseId(1L))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findByCourse_CourseId(1L);

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findByCourse_CourseId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByCourse_CourseId_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findByCourse_CourseId(1L)).thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findByCourse_CourseId(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findByCourse_CourseId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByUniversity_UniversityId() {
        // Mock behavior
        when(programScheduledRepository.findByUniversity_UniversityId(1L))
            .thenReturn(Collections.singletonList(programScheduled));

        // Execute the service call
        List<ProgramScheduledDto> result = programScheduledServiceImpl.findByUniversity_UniversityId(1L);

        // Verify the results
        assertFalse(result.isEmpty());
        verify(programScheduledRepository, times(1)).findByUniversity_UniversityId(1L);
    }

    @Test // Marks this method as a test method
    void testFindByUniversity_UniversityId_NotFound() {
        // Mock behavior
        when(programScheduledRepository.findByUniversity_UniversityId(1L)).thenReturn(Collections.emptyList());

        // Verify the exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            programScheduledServiceImpl.findByUniversity_UniversityId(1L);
        });

        // Verify the mock interactions
        verify(programScheduledRepository, times(1)).findByUniversity_UniversityId(1L);
    }

}
