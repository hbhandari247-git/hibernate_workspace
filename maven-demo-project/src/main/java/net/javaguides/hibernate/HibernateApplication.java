package net.javaguides.hibernate;

import net.javaguides.hibernate.dao.StudentDAO;
import net.javaguides.hibernate.model.Student;

public class HibernateApplication {
	public static void main(String[] args) {

		StudentDAO studentDao = new StudentDAO();

		Student student = new Student("Himanshu", "Bhandari", "hbhan238@gmail.com");		
		System.out.println(studentDao.saveStudent(student));
		System.out.println(studentDao.getStudentById(1));
		System.out.println(studentDao.updateStudentById(1, "hbha947@gmail.com"));
	}
}