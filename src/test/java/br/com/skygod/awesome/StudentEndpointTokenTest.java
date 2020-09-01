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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTokenTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;

    private HttpEntity<Void> protectedHeader;
    private HttpEntity<Void> adminHeader;
    private HttpEntity<Void> wrongHeader;

    private static final String ADM_URL = "/v1/admin/students";
    private static final String USER_URL = "/v1/protected/students";

    @Before
    public void configProtectedHeaders() {
        String str = "{\"username\": \"gustavorocha\", \"password\": \"skypass\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class)
                .getHeaders();

        this.protectedHeader = new HttpEntity<>(headers);
    }

    @Before
    public void configAdminHeaders() {
        String str = "{\"username\": \"skygod\", \"password\": \"skypass\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class)
                .getHeaders();
        this.adminHeader = new HttpEntity<>(headers);
    }

    @Before
    public void configWrongHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "null");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Before
    public void setup() {
        Student student = new Student(1L, "Robson", "robsonsato01@hotmail.com");
        BDDMockito.when(studentRepository.findOne(student.getId())).thenReturn(student);
    }

    @Test
    public void listStudentWhenTokenIsIncorrectShouldReturnStatusCode403() {
        ResponseEntity<String> studentEntity =
                restTemplate.exchange(USER_URL, GET, wrongHeader, String.class);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    public void getStudentsByIdWhenTokenIsIncorrectShouldReturnStatusCode403() {
        ResponseEntity<String> studentEntity =
                restTemplate.exchange(USER_URL + "/1", GET, wrongHeader, String.class);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    public void listStudentWhenTokenIsCorrectShouldReturnStatusCode200() {
        ResponseEntity<String> studentEntity =
                restTemplate.exchange(USER_URL, GET, protectedHeader, String.class);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdWhenTokenIsCorrectShouldReturnStatusCode200() {
        ResponseEntity<String> studentEntity =
                restTemplate.exchange(USER_URL + "/{id}", GET, protectedHeader, String.class, 1L);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdTokenIsCorrectAndStudentDoesNotExistShouldReturnStatusCode404() {
        ResponseEntity<String> studentEntity =
                restTemplate.exchange(USER_URL + "/{id}", GET, protectedHeader, String.class, -1);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode200() {
        BDDMockito.doNothing().when(studentRepository).delete(1L);
        ResponseEntity<String> exchange = restTemplate.exchange(ADM_URL + "/1",
                DELETE, adminHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentDoesNotExistsShouldReturnStatusCode404() throws
            Exception {
        String token = adminHeader.getHeaders().get("Authorization").get(0);

        BDDMockito.doNothing().when(studentRepository).delete(-1L);
        mockMvc.perform(MockMvcRequestBuilders
                .delete(ADM_URL + "/{id}", -1)
                .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void deleteWhenUserDoesNotHaveRoleShouldReturnStatusCode403() throws
            Exception {
        String token = protectedHeader.getHeaders().get("Authorization").get(0);
        BDDMockito.doNothing().when(studentRepository).delete(1L);
        mockMvc.perform(MockMvcRequestBuilders
                .delete(ADM_URL + "/{id}", 1L)
                .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400() {
        Student student = new Student("", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<String> studentPost =
                restTemplate.exchange(ADM_URL, POST, new HttpEntity<>(student, adminHeader.getHeaders()), String.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createWhenEmailIsNullShouldReturnStatusCode400() throws
            Exception {
        String token = adminHeader.getHeaders().get("Authorization").get(0);
        Student student = new Student("skyzin", "");
        mockMvc.perform(MockMvcRequestBuilders
                .post(ADM_URL)
                .header("Authorization", token)
                .requestAttr("student", student))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createWhenEmailDoesNotValidShouldReturnStatusCode400() {
        Student student = new Student("skyzin", "skyzinhotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<Student> studentPost = restTemplate.exchange(ADM_URL, POST, new HttpEntity<>(student,
                adminHeader.getHeaders()), Student.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createShouldPersistAndReturnStatusCode201() {
        Student student = new Student("skyzin", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<Student> studentPost = restTemplate.exchange(ADM_URL, POST,
                new HttpEntity<>(student, adminHeader.getHeaders()), Student.class);
        assertThat(studentPost.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void updateWhenIdIsNullShouldReturnStatusCode400() throws
            Exception {
        String token = adminHeader.getHeaders().get("Authorization").get(0);
        Student student = new Student(null, "skyzin", "skyzin@hotmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .put(ADM_URL)
                .header("Authorization", token)
                .requestAttr("student", student))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateShouldPersistShouldAndReturnStatusCode200() {
        Student student = new Student(1L, "skyzin", "skyzin@hotmail.com");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<Student> exchange = restTemplate.exchange(ADM_URL, PUT, new HttpEntity<Student>(student,
                adminHeader.getHeaders()), Student.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }
}
