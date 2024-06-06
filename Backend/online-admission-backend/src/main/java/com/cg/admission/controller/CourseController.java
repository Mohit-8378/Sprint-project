package com.cg.admission.controller;
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

import com.cg.admission.dto.CourseDto;
import com.cg.admission.entity.Branch;
import com.cg.admission.entity.Course;
import com.cg.admission.service.CourseService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class CourseController {

	@Autowired
	private CourseService courseService;
	
	// Endpoint to view all course details
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/courses")
	public ResponseEntity<List<CourseDto>> viewAllCourseDetails() {
		List<CourseDto> courseDetail = courseService.viewAllCourseDetails();
		return new ResponseEntity<List<CourseDto>>(courseDetail, HttpStatus.OK);
	}
	
	// Endpoint to find course details by course name
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/course/coursename/{courseName}")
	public ResponseEntity<CourseDto> findCourseDetailsByCourseName(@PathVariable String courseName) {
		return new ResponseEntity<CourseDto>(courseService.findCourseDetailsByCourseName(courseName), HttpStatus.OK);
	}
	
	// Endpoint to find course details by eligibility
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/course/eligibility/{eligibility}")
	public ResponseEntity<List<CourseDto>> findCourseDetailsByEligibility(@PathVariable String eligibility) {
		return new ResponseEntity<List<CourseDto>>(courseService.findCourseDetailsByEligibility(eligibility),
				HttpStatus.OK);
	}
	
	// Endpoint to find course details by course ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/course/coursedetail/{courseId}")	
	public ResponseEntity<CourseDto> findCourseDetailsByCourseId(@PathVariable Long courseId) {
		return new ResponseEntity<CourseDto>(courseService.findCourseDetailsByCourseId(courseId), HttpStatus.OK);
	}
	
	// Endpoint to add a new course
	//	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/course/{collegeRegId}/program/{progrmId}")
	public ResponseEntity<CourseDto> addCourse(@PathVariable Long collegeRegId, @Valid @PathVariable Long progrmId, @RequestBody Course course) {
		return new ResponseEntity<>(courseService.addCourse(collegeRegId, progrmId, course), HttpStatus.CREATED);
	}
	
	// Endpoint to update course details
	//	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/course/update/{courseId}")
	public ResponseEntity<Long> updateCourseDetails(@PathVariable Long courseId, @Valid @RequestBody Course course) {
		return new ResponseEntity<Long>(courseService.updateCourseDetails(courseId, course), HttpStatus.OK);
	}
	
	// Endpoint to delete a course by its ID
	//	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/course/{courseId}")
	public ResponseEntity<String> deleteCourseById(@PathVariable Long courseId) {
		return new ResponseEntity<String>(courseService.deleteCourseById(courseId), HttpStatus.OK);
	}
	
	// Endpoint to delete a course by its name
	//	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/course/delete/coursename/{courseName}")
	public ResponseEntity<String> deleteCourseByCourseName(@PathVariable String courseName) {
		return new ResponseEntity<String>(courseService.deleteCourseByCourseName(courseName), HttpStatus.OK);
	}
	
	// Endpoint to find branches by course ID
	//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/courses/{courseId}/branches")
	public ResponseEntity<List<Branch>> findBranchByCourse(@PathVariable Long courseId) {
		return new ResponseEntity<List<Branch>>(courseService.findBranchByCourse(courseId), HttpStatus.OK);
	}
}
