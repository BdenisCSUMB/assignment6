package com.cst438.controller;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.SectionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.cst438.test.utils.TestUtils.asJsonString;
import static com.cst438.test.utils.TestUtils.fromJsonString;
import static org.junit.jupiter.api.Assertions.*;

/*
 * example of unit test to add a section to an existing course
 */

@AutoConfigureMockMvc
@SpringBootTest
public class AssignmentControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Test
    public void addSection() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for new assignment.

        AssignmentDTO assignment = new AssignmentDTO(
                0,
                "test",
                "2024-05-05",
                "cst338",
                1,
                6
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(assignment)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        // primary key should have a non-zero value from the database
        assertNotEquals(0, result.id());
        // check to see if title matches
        assertEquals("test", result.title());
        // check expected due date
        assertEquals("2024-05-05", result.dueDate());
        // check other fields of the DTO for expected values
        assertEquals("cst338", result.courseId());
        assertEquals(1, result.secId());
        assertEquals(6, result.secNo());

        // check the database
        Assignment a = assignmentRepository.findById(result.id()).orElse(null);
        assertNotNull(a);
        assertEquals("test", a.getTitle());

        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/assignments/"+result.id()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        a = assignmentRepository.findById(result.secNo()).orElse(null);
        assertNull(a);  // section should not be found after delete
    }

    @Test
    public void addAssignmentFailsSecNo( ) throws Exception {

        MockHttpServletResponse response;

        // course id cst599 does not exist.
        AssignmentDTO assignment = new AssignmentDTO(
                0,
                "test",
                "9999-05-05",
                "cst338",
                1,
                -1
        );;

        // issue the POST request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(assignment)))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(404, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("section not found", message);

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