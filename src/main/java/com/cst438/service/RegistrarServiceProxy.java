package com.cst438.service;

import com.cst438.domain.*;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.SectionDTO;
import com.cst438.domain.Section;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.dto.UserDTO;
import com.cst438.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;


    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message) {
        try {
            String[] parts = message.split(" ", 2);
            String action = parts[0];

            switch (action) {
                case "addCourse":
                    CourseDTO courseDTO = fromJsonString(parts[1], CourseDTO.class);
                    Course newCourse = new Course();
                    newCourse.setCourseId(courseDTO.courseId());
                    newCourse.setTitle(courseDTO.title());
                    newCourse.setCredits(courseDTO.credits());
                    courseRepository.save(newCourse);
                    break;

                case "updateCourse":
                    CourseDTO updateCourseDTO = fromJsonString(parts[1], CourseDTO.class);
                    Course existingCourse = courseRepository.findById(updateCourseDTO.courseId()).orElse(null);
                    if (existingCourse != null) {
                        existingCourse.setTitle(updateCourseDTO.title());
                        existingCourse.setCredits(updateCourseDTO.credits());
                        courseRepository.save(existingCourse);
                    }
                    break;

                case "deleteCourse":
                    courseRepository.deleteById(parts[1]);
                    break;

                case "addSection":
                    SectionDTO sectionDTO = fromJsonString(parts[1], SectionDTO.class);
                    Section newSection = new Section();
                    newSection.setSecId(sectionDTO.secId());
                    newSection.setSectionNo(sectionDTO.secNo());
                    sectionRepository.save(newSection);
                    break;

                case "updateSection":
                    SectionDTO updateSectionDTO = fromJsonString(parts[1], SectionDTO.class);
                    Section existingSection = sectionRepository.findById(updateSectionDTO.secId()).orElse(null);
                    if (existingSection != null) {
                        existingSection.setSectionNo(updateSectionDTO.secNo());
                        sectionRepository.save(existingSection);
                    }
                    break;

                case "deleteSection":
                    sectionRepository.deleteById(Integer.valueOf(parts[1]));
                    break;

                case "addUser":
                    UserDTO userDTO = fromJsonString(parts[1], UserDTO.class);
                    User newUser = new User();
                    newUser.setId(userDTO.id());
                    newUser.setName(userDTO.name());
                    newUser.setEmail(userDTO.email());
                    userRepository.save(newUser);
                    break;

                case "updateUser":
                    UserDTO updateUserDTO = fromJsonString(parts[1], UserDTO.class);
                    User existingUser = userRepository.findById(updateUserDTO.id()).orElse(null);
                    if (existingUser != null) {
                        existingUser.setName(updateUserDTO.name());
                        existingUser.setEmail(updateUserDTO.email());
                        userRepository.save(existingUser);
                    }
                    break;

                case "deleteUser":
                    userRepository.deleteById(Integer.valueOf(parts[1]));
                    break;

                case "addEnrollment":
                    EnrollmentDTO enrollmentDTO = fromJsonString(parts[1], EnrollmentDTO.class);
                    Enrollment newEnrollment = new Enrollment();
                    newEnrollment.setEnrollmentId(enrollmentDTO.enrollmentId());
                    newEnrollment.setGrade(enrollmentDTO.grade());
                    enrollmentRepository.save(newEnrollment);
                    break;

                case "updateEnrollment":
                    EnrollmentDTO updateEnrollmentDTO = fromJsonString(parts[1], EnrollmentDTO.class);
                    Enrollment existingEnrollment = enrollmentRepository.findById(updateEnrollmentDTO.enrollmentId()).orElse(null);
                    if(existingEnrollment != null) {
                        existingEnrollment.setGrade(updateEnrollmentDTO.grade());
                        enrollmentRepository.save(existingEnrollment);
                    }
                    break;

                case "deleteEnrollment":
                    enrollmentRepository.deleteById(Integer.valueOf(parts[1]));
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGradeUpdateMessage(EnrollmentDTO enrollmentDTO) {
        try {
            String message = "updateEnrollment " + asJsonString(enrollmentDTO);
            sendMessage(message);
        } catch (Exception e) {
            System.err.println("Error sending grade update message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String s) {
        rabbitTemplate.convertAndSend(registrarServiceQueue.getName(), s);
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