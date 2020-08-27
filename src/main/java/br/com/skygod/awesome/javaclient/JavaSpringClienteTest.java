package br.com.skygod.awesome.javaclient;

import br.com.skygod.awesome.model.PageableResponse;
import br.com.skygod.awesome.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class JavaSpringClienteTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/protected/students")
                .basicAuthorization("skygod", "skypass")
                .build();

        RestTemplate restTemplateAdmin = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/v1/admin/students")
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

        Student studentPost = new Student();
        studentPost.setName("John Wick");
        studentPost.setEmail("johnwick@outlook.com");

        ResponseEntity<Student> exchangePost = restTemplateAdmin.exchange("/", HttpMethod.POST,
                new HttpEntity<>(studentPost, createJSONHeader()), Student.class);

        Student returnedStudentPost = restTemplateAdmin.postForObject("/", studentPost, Student.class);

        System.out.println(listPageable);
        System.out.println(exchangePost);
        System.out.println(returnedStudentPost);
    }

    private static HttpHeaders createJSONHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
