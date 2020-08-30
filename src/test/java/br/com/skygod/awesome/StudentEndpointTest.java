package br.com.skygod.awesome;

import br.com.skygod.awesome.model.Student;
import br.com.skygod.awesome.repository.StudentRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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

        Student student =
                new Student(1L, "Skyzindoplay", "skyzin@doplay.com");

        BDDMockito.when(studentRepository.findOne(1L)).thenReturn(student);

        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL + "/{id}", String.class, student.getId());

        assertThat(studentEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectAndStudentDoesNotExistShouldReturnStatusCode404() {
        System.out.println("\33[33mPort: " + port + "\33[m");

        ResponseEntity<String> studentEntity =
                restTemplate.getForEntity(USER_URL + "/{id}", String.class, -1);
        assertThat(studentEntity.getStatusCode().value()).isEqualTo(404);
    }
}
