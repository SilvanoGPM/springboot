package br.com.skygod.awesome.javaclient;

import br.com.skygod.awesome.model.PageableResponse;
import br.com.skygod.awesome.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JavaSpringClienteTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/protected/students")
                .basicAuthorization("skygod", "skypass")
                .build();

//        Student student = restTemplate.getForObject("/{id}", Student.class, 16);
//        ResponseEntity<Student> responseEntity = restTemplate.getForEntity("/{id}", Student.class, 16);
//        Student[] students = restTemplate.getForObject("/", Student[].class);
//        ResponseEntity<List<Student>> list = restTemplate.exchange("/", HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<Student>>() {
//                });
//
//        System.out.println(student);
//        System.out.println(responseEntity);
//        System.out.println(Arrays.toString(students));
//        System.out.println(list.getBody().size());

        ResponseEntity<PageableResponse<Student>> listPageable = restTemplate.exchange("/?page=2&sort=email," +
                        "desc&name,asc",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Student>>() {
                });

        System.out.println(listPageable);

    }
}
