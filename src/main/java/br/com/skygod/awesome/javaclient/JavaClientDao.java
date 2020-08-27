package br.com.skygod.awesome.javaclient;

import br.com.skygod.awesome.handler.RestResponseExceptionHandler;
import br.com.skygod.awesome.model.PageableResponse;
import br.com.skygod.awesome.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class JavaClientDao {
    private final RestTemplate restTemplate;

    private final RestTemplate restTemplateAdmin;

    public JavaClientDao() {
        restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/protected/students")
                .basicAuthorization("gustavorocha", "skypass")
                .errorHandler(new RestResponseExceptionHandler())
                .build();

        restTemplateAdmin = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/admin/students")
                .basicAuthorization("skygod", "skypass")
                .errorHandler(new RestResponseExceptionHandler())
                .build();
    }

    public Student findById(long id) {
        return restTemplate.getForObject("/{id}", Student.class, id);
    }

    public List<Student> listAll() {
        return restTemplate.exchange("/?size=10", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Student>>() {
                })
                .getBody()
                .getContent();
    }

    public Student save(Student student) {
        return restTemplateAdmin.postForObject("/", student, Student.class);
    }

    public void update(Student student) {
        restTemplateAdmin.put("/", student);
    }

    public void delete(long id) {
        restTemplateAdmin.delete("/{id}", id);
    }

}
