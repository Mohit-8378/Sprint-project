package com.cg.admission.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.cg.admission.dto.ProgramScheduledDto;
import com.cg.admission.dto.ProgramScheduledResponseDto;
import com.cg.admission.entity.Application;
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
import com.cg.admission.service.ProgramScheduledService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProgramScheduledServiceImpl implements ProgramScheduledService {

    // Repositories and services used in this service implementation
    private ProgramScheduledRepository programScheduledRepository;
    private UniversityRepository universityRepository;
    private CollegeRepository collegeRepository;
    private ProgramRepository programRepository;
    private CourseRepository courseRepository;
    private BranchRepository branchRepository;
    private ApplicationServiceImpl applicationServiceImpl;
    private ApplicationRepository applicationRepository;

    // Method to view all program scheduled details
    @Override
    public List<ProgramScheduledDto> viewAllProgramScheduledDetails() {
        // Retrieve all scheduled programs from the repository
        List<ProgramScheduled> programScheduledList = programScheduledRepository.findAll();

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtoList = programScheduledList.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();

            // Copy properties from various entities to the DTO
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);
            //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found
        if (programScheduledDtoList.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled Found");
        }

        return programScheduledDtoList;
    }

    // Method to delete a program scheduled by its ID
    @Override
    public String deleteProgramScheduledById(Long scheduledId) {
        // Retrieve the program scheduled entity by its ID
        ProgramScheduled deletedProgramScheduled = programScheduledRepository.findById(scheduledId)
                .orElseThrow(() -> new ResourceNotFoundException("No Program found with Id " + scheduledId));

        Long deletedScheduledId = deletedProgramScheduled.getScheduledId();

        // Retrieve all applications associated with the scheduled program and delete them
        List<Application> list = applicationRepository.findBySchedule_scheduledId(deletedScheduledId);
        for (Application i : list) {
            applicationServiceImpl.deleteApplicationByid(i.getApplicationId());
        }

        // Delete the scheduled program
        programScheduledRepository.deleteById(deletedScheduledId);

        return "Program Scheduled deleted Successfully";
    }

    // Method to update an existing program scheduled by its ID
    @Override
    public ProgramScheduledDto updateProgramScheduled(Long scheduledId, ProgramScheduled programScheduled) {
        ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();

        // Retrieve the existing program scheduled entity by its ID
        ProgramScheduled programScheduledExisted = programScheduledRepository.findById(scheduledId)
                .orElseThrow(() -> new ResourceNotFoundException("No Program found with Id " + scheduledId));

        // Update the start and end dates
        programScheduledExisted.setStartDate(programScheduled.getStartDate());
        programScheduledExisted.setEndDate(programScheduled.getEndDate());

        // Save the updated entity
        programScheduledRepository.save(programScheduledExisted);

        // Copy properties to the DTO
        BeanUtils.copyProperties(programScheduledExisted, programScheduledDto);

        return programScheduledDto;
    }

    // Method to add a new program schedule
    @Override
    public ProgramScheduledDto addProgramSchedule(Long universityId, Long collegeId, Long programId, Long courseId,
                                                  Long branchId, ProgramScheduled programScheduled) {
        ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();

        // Retrieve related entities by their IDs
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("No university found with Id " + universityId));
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("No college found with Id " + collegeId));
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("No program found with Id " + universityId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found with Id " + courseId));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("No branch found with Id " + branchId));

        // Set related entities to the scheduled program
        programScheduled.setBranch(branch);
        programScheduled.setCollege(college);
        programScheduled.setUniversity(university);
        programScheduled.setCourse(course);
        programScheduled.setProgram(program);

        // Save the new scheduled programScheduled
        programScheduledRepository.save(programScheduled);

        // Copy properties to the response DTO
        ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
        BeanUtils.copyProperties(programScheduled.getBranch(), dto);
        BeanUtils.copyProperties(programScheduled.getProgram(), dto);
        BeanUtils.copyProperties(programScheduled.getCourse(), dto);
        BeanUtils.copyProperties(programScheduled.getCollege(), dto);
        BeanUtils.copyProperties(programScheduled.getUniversity(), dto);

      //setting values of prorgamschedulerepsonsedto in programscheduledto
        programScheduledDto.setProgramScheduledResponseDto(dto);
        BeanUtils.copyProperties(programScheduled, programScheduledDto);

        return programScheduledDto;
    }

    // Method to find a program scheduled by its ID
    @Override
    public ProgramScheduledDto findProgramScheduledById(Long scheduledId) {
        ProgramScheduled programScheduled = programScheduledRepository.findById(scheduledId)
                .orElseThrow(() -> new ResourceNotFoundException("No Program Scheduled present with ID" + scheduledId));

        ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();

        // Copy properties to the DTO
        BeanUtils.copyProperties(programScheduled, programScheduledDto);
        ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
        BeanUtils.copyProperties(programScheduled.getBranch(), dto);
        BeanUtils.copyProperties(programScheduled.getCourse(), dto);
        BeanUtils.copyProperties(programScheduled.getProgram(), dto);
        BeanUtils.copyProperties(programScheduled.getCollege(), dto);
        BeanUtils.copyProperties(programScheduled.getUniversity(), dto);

      //setting values of prorgamschedulerepsonsedto in programscheduledto
        programScheduledDto.setProgramScheduledResponseDto(dto);

        return programScheduledDto;
    }

    // Method to find program schedules by college name
    @Override
    public List<ProgramScheduledDto> findProgramScheduledByCollege_CollegeName(String collegeName) {
        List<ProgramScheduled> programScheduledListByCollegeName = programScheduledRepository
                .findProgramScheduledByCollege_CollegeName(collegeName);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByCollegeName.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given college name
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + collegeName);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by start date
    @Override
    public List<ProgramScheduledDto> findProgramScheduledByStartDate(LocalDate startDate) {
        List<ProgramScheduled> programList = programScheduledRepository.findProgramScheduledByStartDate(startDate);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programList.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Copy properties from the list of entities to the list of DTOs
        BeanUtils.copyProperties(programList, programScheduledDtos);

        // Throw an exception if no programs are found for the given start date
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled by StartDate " + startDate);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by college registration ID
    @Override
    public List<ProgramScheduledDto> findByCollege_CollegeRegId(Long collegeRegId) {
        List<ProgramScheduled> programScheduledListByCollegeId = programScheduledRepository
                .findByCollege_CollegeRegId(collegeRegId);

        // Convert each scheduled programScheduled entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByCollegeId.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given college registration ID
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + collegeRegId);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by branch ID
    @Override
    public List<ProgramScheduledDto> findByBranch_BranchId(Long branchId) {
        List<ProgramScheduled> programScheduledListByBranchId = programScheduledRepository
                .findByBranch_BranchId(branchId);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByBranchId.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given branch ID
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + branchId);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by program ID
    @Override
    public List<ProgramScheduledDto> findByProgram_ProgramId(Long programId) {
        List<ProgramScheduled> programScheduledListByProgramId = programScheduledRepository
                .findByProgram_ProgramId(programId);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByProgramId.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given program ID
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + programId);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by course ID
    @Override
    public List<ProgramScheduledDto> findByCourse_CourseId(Long courseId) {
        List<ProgramScheduled> programScheduledListByCourseId = programScheduledRepository
                .findByCourse_CourseId(courseId);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByCourseId.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given course ID
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + courseId);
        }

        return programScheduledDtos;
    }

    // Method to find program schedules by university ID
    @Override
    public List<ProgramScheduledDto> findByUniversity_UniversityId(Long universityId) {
        List<ProgramScheduled> programScheduledListByUniversityId = programScheduledRepository
                .findByUniversity_UniversityId(universityId);

        // Convert each scheduled program entity to a DTO
        List<ProgramScheduledDto> programScheduledDtos = programScheduledListByUniversityId.stream().map(i -> {
            ProgramScheduledDto programScheduledDto = new ProgramScheduledDto();
            ProgramScheduledResponseDto dto = new ProgramScheduledResponseDto();
            BeanUtils.copyProperties(i.getBranch(), dto);
            BeanUtils.copyProperties(i.getCourse(), dto);
            BeanUtils.copyProperties(i.getProgram(), dto);
            BeanUtils.copyProperties(i.getCollege(), dto);
            BeanUtils.copyProperties(i.getUniversity(), dto);

          //setting values of prorgamschedulerepsonsedto in programscheduledto
            programScheduledDto.setProgramScheduledResponseDto(dto);
            BeanUtils.copyProperties(i, programScheduledDto);

            return programScheduledDto;
        }).collect(Collectors.toList());

        // Throw an exception if no programs are found for the given university ID
        if (programScheduledDtos.isEmpty()) {
            throw new ResourceNotFoundException("No Program Scheduled for " + universityId);
        }

        return programScheduledDtos;
    }
}
