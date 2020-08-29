package br.com.skygod.awesome;

import br.com.skygod.awesome.model.Student;
import br.com.skygod.awesome.repository.StudentRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository dao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createShouldPersistData() {
        Student student = new Student("Robson", "robsonsato@homail.com");
        this.dao.save(student);
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("Robson");
        assertThat(student.getEmail()).isEqualTo("robsonsato@homail.com");
    }

    @Test
    public void deleteShouldRemoveData() {
        Student student = new Student("Robson", "robsonsato@homail.com");
        this.dao.save(student);
        this.dao.delete(student);

        assertThat(dao.findOne(student.getId())).isNull();
    }

    @Test
    public void updateShouldChangeAndPersist() {
        Student student = new Student("Robson", "robsonsato@homail.com");
        this.dao.save(student);
        student.setName("Robson2");
        student.setEmail("robsonsato2@homail.com");

        student = dao.findOne(student.getId());

        assertThat(student.getName()).isEqualTo("Robson2");
        assertThat(student.getEmail()).isEqualTo("robsonsato2@homail.com");
    }

    @Test
    public void findByNameShouldIgnoreCase() {
        Student student1 = new Student("Robson", "robsonsato@homail.com");
        Student student2 = new Student("robson", "robsonsato@homail.com");
        this.dao.save(student1);
        this.dao.save(student2);

        List<Student> studentList = this.dao.findByNameIgnoreCaseContaining("robson");
        assertThat(studentList.size()).isEqualTo(2);
    }

    @Test
    public void createWhenNameIsNullShouldThrowConstraintViolationException() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("The name field is required!");

        dao.save(new Student());
    }

    @Test
    public void createWhenEmailIsNullShouldThrowConstraintViolationException() {
        thrown.expect(ConstraintViolationException.class);
        Student student = new Student();
        student.setName("Sky");
        this.dao.save(student);
    }

    @Test
    public void createWhenEmailIsNotValidThrowConstraintViolationException() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("Type it an valid email!");
        this.dao.save(new Student("Sky", "Sky"));
    }

}
