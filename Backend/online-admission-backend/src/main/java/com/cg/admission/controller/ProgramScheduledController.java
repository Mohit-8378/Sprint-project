package com.cg.admission.controller;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.admission.dto.ProgramScheduledDto;
import com.cg.admission.entity.ProgramScheduled;
import com.cg.admission.service.ProgramScheduledService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProgramScheduledController {

	@Autowired
	private ProgramScheduledService programScheduledService;

	// Endpoint to add a new program schedule
	//	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/programscheduled/{universityId}/{collegeId}/{programId}/{courseId}/{branchId}")
	public ResponseEntity<ProgramScheduledDto> addProgramSchedule(@PathVariable Long universityId,
			@PathVariable Long collegeId, @PathVariable Long programId, @PathVariable Long courseId,
			@PathVariable Long branchId, @RequestBody ProgramScheduled programScheduled) {
		// Test this after mapping
		return new ResponseEntity<>(programScheduledService.addProgramSchedule(universityId, collegeId, programId,
				courseId, branchId, programScheduled), HttpStatus.CREATED);
		// Done
	}

	// Endpoint to view all program scheduled details
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/allprogramscheduled")
	public ResponseEntity<List<ProgramScheduledDto>> viewAllProgramScheduledDetails() {
		return new ResponseEntity<List<ProgramScheduledDto>>(programScheduledService.viewAllProgramScheduledDetails(),
				HttpStatus.OK);
	}

	// Endpoint to find a program schedule by its ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/{scheduledId}")
	public ResponseEntity<ProgramScheduledDto> findProgramScheduledById(@PathVariable Long scheduledId) {
		return new ResponseEntity<ProgramScheduledDto>(programScheduledService.findProgramScheduledById(scheduledId),
				HttpStatus.OK);
	}

	// Endpoint to delete a program schedule by its ID
	//	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteprogramscheduled/{scheduledId}")
	public ResponseEntity<String> deleteProgramScheduledById(@PathVariable Long scheduledId) {
		return new ResponseEntity<String>(programScheduledService.deleteProgramScheduledById(scheduledId),
				HttpStatus.OK);
	}

	// Endpoint to update a program schedule
	//	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update/programscheduled/{scheduledId}")
	public ResponseEntity<ProgramScheduledDto> updateProgramScheduled(@PathVariable("scheduledId") Long scheduledId,
			@RequestBody ProgramScheduled programScheduled) {
		return new ResponseEntity<ProgramScheduledDto>(
				programScheduledService.updateProgramScheduled(scheduledId, programScheduled), HttpStatus.OK);
	}

	// Endpoint to find program schedules by college name
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/collegename/{collegeName}")
	public ResponseEntity<List<ProgramScheduledDto>> findProgramScheduledByCollege_CollegeName(
			@PathVariable String collegeName) {
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findProgramScheduledByCollege_CollegeName(collegeName), HttpStatus.OK);
	}

	// Endpoint to find program schedules by start date
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/date/{startDate}")
	public ResponseEntity<List<ProgramScheduledDto>> findProgramScheduledByStartDate(
			@PathVariable LocalDate startDate) {
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findProgramScheduledByStartDate(startDate), HttpStatus.OK);
	}
	
	// Endpoint to find program schedules by college registration ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/collegeId/{collegeRegId}")
	public ResponseEntity<List<ProgramScheduledDto>> findByCollege_CollegeRegId(@PathVariable Long collegeRegId){
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findByCollege_CollegeRegId(collegeRegId), HttpStatus.OK);
	}
	
	// Endpoint to find program schedules by branch ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/branchId/{branchId}")
	public ResponseEntity<List<ProgramScheduledDto>> findByBranch_BranchId(@PathVariable Long branchId){
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findByBranch_BranchId(branchId), HttpStatus.OK);
	}
	
	// Endpoint to find program schedules by program ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/programId/{programId}")
	public ResponseEntity<List<ProgramScheduledDto>> findByProgram_ProgramId(@PathVariable Long programId) {
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findByProgram_ProgramId(programId), HttpStatus.OK);
	}
	
	// Endpoint to find program schedules by course ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/courseId/{courseId}")
	public ResponseEntity<List<ProgramScheduledDto>> findByCourse_CourseId(@PathVariable Long courseId) {
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findByCourse_CourseId(courseId), HttpStatus.OK);
	}
	
	// Endpoint to find program schedules by university ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/programscheduled/universityId/{universityId}")
	public ResponseEntity <List<ProgramScheduledDto>> findByUniversity_UniversityId(@PathVariable Long universityId) {
		return new ResponseEntity<List<ProgramScheduledDto>>(
				programScheduledService.findByUniversity_UniversityId(universityId), HttpStatus.OK);
	}
}
