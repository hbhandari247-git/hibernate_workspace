package net.javaguides.hibernate;

import net.javaguides.hibernate.dao.StudentDAO;
import net.javaguides.hibernate.model.Student;

public class HibernateApplication {
	public static void main(String[] args) {

		StudentDAO studentDao = new StudentDAO();

		Student student = new Student("Himanshu", "Bhandari", "hb12@gmail.com");		
		System.out.println(studentDao.saveStudent(student));
		System.out.println(studentDao.getStudentById(2));
		System.out.println(studentDao.updateStudentById(2, "hb1203@gmail.com"));
	}
}