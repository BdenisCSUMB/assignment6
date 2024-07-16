package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

// Student add enrollment

@AutoConfigureMockMvc
@SpringBootTest
public class StudentControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    public void addEnrollment() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for new enrollment.
        // the primary key is set to 0, it will
        // set by the database when the enrollment is inserted.
        EnrollmentDTO enrollment = new EnrollmentDTO(
                0,
                null,
                5,
                "test student",
                "tstudent@csumb.edu",
                "cst363",
                1,
                11,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2024,
                "Fall"
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/"+enrollment.sectionNo())
                                .param("studentId", String.valueOf(enrollment.studentId()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.enrollmentId());
        // check other fields of the DTO for expected values
        assertEquals("cst363", result.courseId());
        assertEquals(5, result.studentId());
        assertEquals("test student", result.name());
        assertEquals("tstudent@csumb.edu", result.email());
        assertEquals(1, result.sectionId());
        assertEquals(11, result.sectionNo());
        assertEquals("052", result.building());
        assertEquals("104", result.room());
        assertEquals("M W 10:00-11:50", result.times());
        assertEquals(4, result.credits());
        assertEquals(2024, result.year());
        assertEquals("Fall", result.semester());

        // check the database
        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);
        assertEquals("cst363", e.getSection().getCourse().getCourseId());
        assertEquals("test student", e.getStudent().getName());
        assertEquals("tstudent@csumb.edu", e.getStudent().getEmail());
        assertEquals(1, e.getSection().getSecId());
        assertEquals(11, e.getSection().getSectionNo());
        assertEquals("052", e.getSection().getBuilding());
        assertEquals("104", e.getSection().getRoom());
        assertEquals("M W 10:00-11:50", e.getSection().getTimes());
        assertEquals(4, e.getSection().getCourse().getCredits());
        assertEquals(2024, e.getSection().getTerm().getYear());
        assertEquals("Fall", e.getSection().getTerm().getSemester());

        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/enrollments/"+result.enrollmentId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);  // section should not be found after delete
    }

    @Test
    public void addEnrollmentFailDuplicate() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for existing enrollment
        EnrollmentDTO enrollment = new EnrollmentDTO(
                49,
                null,
                5,
                "test student",
                "tstudent@csumb.edu",
                "cst363",
                1,
                10,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2024,
                "Fall"
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + enrollment.sectionNo())
                                .param("studentId", String.valueOf(enrollment.studentId()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        // check the response code for 400
        assertEquals(400, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("already enrolled in this section", message);
    }

    @Test
    public void addEnrollmentFailSecNoInvalid() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for enrollment attempt with invalid section number
        EnrollmentDTO enrollment = new EnrollmentDTO(
                0,
                null,
                5,
                "test student",
                "tstudent@csumb.edu",
                "cst363",
                1,
                12,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2024,
                "Fall"
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + enrollment.sectionNo())
                                .param("studentId", String.valueOf(enrollment.studentId()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        // check the response code for 400
        assertEquals(404, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("section number not found", message);
    }

    @Test
    public void addEnrollmentFailPastAddDeadline() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for enrollment attempt past the add deadline
        EnrollmentDTO enrollment = new EnrollmentDTO(
                0,
                null,
                5,
                "test student",
                "tstudent@csumb.edu",
                "cst338",
                1,
                1,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2023,
                "Fall"
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + enrollment.sectionNo())
                                .param("studentId", String.valueOf(enrollment.studentId()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        // check the response code for 400
        assertEquals(400, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("cannot enroll in this section due to date", message);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

        /*
        // return data converted from String to DTO
        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);


        // primary key should have a non zero value from the database
        assertNotEquals(0, result.enrollmentId());
        // check other fields of the DTO for expected values
        assertEquals("cst363", result.courseId());
        assertEquals(5, result.studentId());
        assertEquals("test student", result.name());
        assertEquals("tstudent@csumb.edu", result.email());
        assertEquals(1, result.sectionId());
        assertEquals(11, result.sectionNo());
        assertEquals("052", result.building());
        assertEquals("104", result.room());
        assertEquals("M W 10:00-11:50", result.times());
        assertEquals(4, result.credits());
        assertEquals(2024, result.year());
        assertEquals("Fall", result.semester());


        // check the database
        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);
        assertEquals("cst363", e.getSection().getCourse().getCourseId());
        assertEquals("test student", e.getStudent().getName());
        assertEquals("tstudent@csumb.edu", e.getStudent().getEmail());
        assertEquals(1, e.getSection().getSecId());
        assertEquals(11, e.getSection().getSectionNo());
        assertEquals("052", e.getSection().getBuilding());
        assertEquals("104", e.getSection().getRoom());
        assertEquals("M W 10:00-11:50", e.getSection().getTimes());
        assertEquals(4, e.getSection().getCourse().getCredits());
        assertEquals(2024, e.getSection().getTerm().getYear());
        assertEquals("Fall", e.getSection().getTerm().getSemester());


        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/enrollments/"+result.enrollmentId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);  // section should not be found after delete
    }



//    @Test
//    public void addSectionFailsBadCourse( ) throws Exception {
//
//        MockHttpServletResponse response;
//
//        // course id cst599 does not exist.
//        SectionDTO section = new SectionDTO(
//                0,
//                2024,
//                "Spring",
//                "cst599",
//                "",
//                1,
//                "052",
//                "104",
//                "W F 1:00-2:50 pm",
//                "Joshua Gross",
//                "jgross@csumb.edu"
//        );
//
//        // issue the POST request
//        response = mvc.perform(
//                        MockMvcRequestBuilders
//                                .post("/sections")
//                                .accept(MediaType.APPLICATION_JSON)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(asJsonString(section)))
//                .andReturn()
//                .getResponse();
//
//        // response should be 400, BAD_REQUEST
//        assertEquals(404, response.getStatus());
//
//        // check the expected error message
//        String message = response.getErrorMessage();
//        assertEquals("course not found cst599", message);
//
//    }

   */