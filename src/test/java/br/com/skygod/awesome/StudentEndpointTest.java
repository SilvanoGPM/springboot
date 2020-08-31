package br.com.skygod.awesome;

import br.com.skygod.awesome.model.Student;
import br.com.skygod.awesome.repository.StudentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;

    private static final String ADM_URL = "/v1/admin/students";
    private static final String USER_URL = "/v1/protected/students";

    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().basicAuthorization(
                    "skygod",
                    "skypass"
            );
        }
    }

    @Before
    public void setup() {
        Student student = new Student(1L, "Robson", "robsonsato01@hotmail.com");
        BDDMockito.when(studentRepository.findOne(student.getId())).thenReturn(student);
    }

    @Test
    public void listStudentWhenUsernameOrPasswordAreIncorrectShouldReturnStatusCode401() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL, String.class);

        assertThat(studentEntity.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    public void getStudentsByIdWhenUsernameOrPasswordAreIncorrectShouldReturnStatusCode401() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL + "/1", String.class);

        assertThat(studentEntity.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    public void listStudentWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        List<Student> studentList = asList(
                new Student(1L, "Skyzindoplay", "skyzin@doplay.com"),
                new Student(2L, "Rafelalinda", "rafazinha4@doplay.com")
        );

        BDDMockito.when(studentRepository.findAll()).thenReturn(studentList);

        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL, String.class);

        assertThat(studentEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL + "/{id}", String.class, 1L);

        assertThat(studentEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectAndStudentDoesNotExistShouldReturnStatusCode404() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL + "/{id}", String.class, -1);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode200() {
        BDDMockito.doNothing().when(studentRepository).delete(1L);
        ResponseEntity<String> exchange = restTemplate.exchange(ADM_URL + "/{id}",
                DELETE, null, String.class, 1L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = {"USER", "ADMIN"})
    public void deleteWhenUserHasRoleAdminAndStudentDoesNotExistsShouldReturnStatusCode404() throws
            Exception {
        BDDMockito.doNothing().when(studentRepository).delete(-1L);
//        ResponseEntity<String> exchange = restTemplate.exchange(ADM_URL + "/{id}",
//                DELETE, null, String.class, -1L);
//        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);

        mockMvc.perform(MockMvcRequestBuilders
                .delete(ADM_URL + "/{id}", -1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @WithMockUser(username = "xx", password = "xx")
    public void deleteWhenUserDoesNotHaveRoleShouldReturnStatusCode403() throws
            Exception {
        BDDMockito.doNothing().when(studentRepository).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders
                .delete(ADM_URL + "/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400() {
        Student student = new Student("", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<String> studentPost = restTemplate.postForEntity(ADM_URL, student, String.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = {"USER", "ADMIN"})
    public void createWhenEmailIsNullShouldReturnStatusCode400() throws
            Exception {
        Student student = new Student("skyzin", "");
        mockMvc.perform(MockMvcRequestBuilders
                .post(ADM_URL).requestAttr("student", student))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createWhenEmailDoesNotValidShouldReturnStatusCode400() {
        Student student = new Student("skyzin", "skyzinhotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<Student> studentPost = restTemplate.postForEntity(ADM_URL, student, Student.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createShouldPersistAndReturnStatusCode201() {
        Student student = new Student("skyzin", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<Student> studentPost = restTemplate.postForEntity(ADM_URL, student, Student.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = {"USER", "ADMIN"})
    public void updateWhenIdIsNullShouldReturnStatusCode400() throws
            Exception {
        Student student = new Student(null, "skyzin", "skyzin@hotmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .put(ADM_URL).requestAttr("student", student))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateShouldPersistShouldAndReturnStatusCode200() {
        Student student = new Student(1L, "skyzin", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<Student> exchange = restTemplate.exchange(ADM_URL, PUT, new HttpEntity<Student>(student) {
        }, Student.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

}
