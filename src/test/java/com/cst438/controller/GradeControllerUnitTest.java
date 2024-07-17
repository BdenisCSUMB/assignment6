package com.cst438.controller;

import com.cst438.domain.AssignmentRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Unit test for Assignments: Add, Add past due, Add bad section number

@AutoConfigureMockMvc
@SpringBootTest
public class GradeControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Test
    public void addGrades() throws Exception {

        MockHttpServletResponse response;
        int testAssignment = 3;

        // Get list of GradeDTO from a valid assignment
        // issue a http GET request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data
        response = mvc.perform(MockMvcRequestBuilders.get("/assignments/" + testAssignment + "/grades")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        List<GradeDTO> grades = fromJsonStringToList(response.getContentAsString(), GradeDTO.class);

        // create new DTOs for test grades ....
        List<GradeDTO> updatedGrades = new ArrayList<>();

        for (GradeDTO g : grades) {
            GradeDTO updatedGrade = new GradeDTO(
                    g.gradeId(),
                    g.studentName(),
                    g.studentEmail(),
                    g.assignmentTitle(),
                    g.courseId(),
                    g.sectionId(),
                    33 // Set the score to 33 for each new GradeDTO
            );
            updatedGrades.add(updatedGrade);
        }
        //System.out.println(asJsonString(updatedGrades));

        // push new grades to the server
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(updatedGrades)))
                .andReturn()
                .getResponse();


        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check the database
        response = mvc.perform(MockMvcRequestBuilders.get("/assignments/" + testAssignment + "/grades")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        List<GradeDTO> testResult = fromJsonStringToList(response.getContentAsString(), GradeDTO.class);

        // check test grades
        for (GradeDTO g : testResult) {
            assertEquals(33, g.score());
        }

        // push old grades back to server
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(grades)))
                .andReturn()
                .getResponse();
        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check to see if database matches original grades
        response = mvc.perform(MockMvcRequestBuilders.get("/assignments/" + testAssignment + "/grades")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        testResult = fromJsonStringToList(response.getContentAsString(), GradeDTO.class);
        assertEquals(testResult, grades);
    }

    @Test
    public void addFinalGrades() throws Exception {

        MockHttpServletResponse response;
        int sectionNo = 11;

        // Get list of EnrollmentDTOs from a valid Section
        // issue a http GET request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data
        response = mvc.perform(MockMvcRequestBuilders.get("/sections/"+sectionNo +"/enrollments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        List<EnrollmentDTO> grades = fromJsonStringToList(response.getContentAsString(), EnrollmentDTO.class);

        // create new DTOs for test FINAL grades ....
        List<EnrollmentDTO> updatedGrades = new ArrayList<>();

        for (EnrollmentDTO g : grades) {
            EnrollmentDTO updatedGrade = new EnrollmentDTO(
                    g.enrollmentId(),
                    "A",
                    g.studentId(),
                    g.name(),
                    g.email(),
                    g.courseId(),
                    g.sectionId(),
                    g.sectionNo(),
                    g.building(),
                    g.room(),
                    g.times(),
                    g.credits(),
                    g.year(),
                    g.semester()
            );
            updatedGrades.add(updatedGrade);
        }
        //System.out.println(asJsonString(updatedGrades));

        // push new grades to the server
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(updatedGrades)))
                .andReturn()
                .getResponse();


        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check the database
        response = mvc.perform(MockMvcRequestBuilders.get("/sections/"+sectionNo +"/enrollments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        List<EnrollmentDTO> testResult = fromJsonStringToList(response.getContentAsString(), EnrollmentDTO.class);

        // check test grades
        for (EnrollmentDTO g : testResult) {
            assertEquals("A", g.grade());
        }

        // push old grades back to server
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(grades)))
                .andReturn()
                .getResponse();
        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check to see if database matches original grades
        response = mvc.perform(MockMvcRequestBuilders.get("/sections/"+sectionNo +"/enrollments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        testResult = fromJsonStringToList(response.getContentAsString(), EnrollmentDTO.class);
        assertEquals(testResult, grades);
    }
    @Test
    public void addGradeFailsAssignmentNo( ) throws Exception {

        MockHttpServletResponse response;
        int testAssignment = -1;

        // Get list of GradeDTO from a invalid assignment
        // issue a http GET request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data
        response = mvc.perform(MockMvcRequestBuilders.get("/assignments/" + testAssignment + "/grades")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(404, response.getStatus());
        assertEquals("assignment not found", response.getErrorMessage());
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> fromJsonStringToList(String str, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, valueType);
            return objectMapper.readValue(str, listType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON string to list of objects", e);
        }
    }

}