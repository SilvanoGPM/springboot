package br.com.skygod.awesome.javaclient;

import br.com.skygod.awesome.model.Student;

public class JavaClientDaoTest {
    public static void main(String[] args) {
        final JavaClientDao dao = new JavaClientDao();

        Student student = new Student();
        student.setId(51L);
        student.setName("Silvanao");
        student.setEmail("silvanindasilvalima@hotmail.com");

//        System.out.println(dao.listAll());
//        System.out.println(dao.findById(0));
//        System.out.println(dao.save(student));
//        dao.update(student);
        dao.delete(51L);

    }
}
