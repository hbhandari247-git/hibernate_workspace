package net.javaguides.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import net.javaguides.hibernate.entity.StudentEntity;
import net.javaguides.hibernate.model.Student;
import net.javaguides.hibernate.util.HibernateUtil;

public class StudentDAO {

	public int saveStudent(Student student) {

		Transaction transaction = null;
		int id = -1;
		if (null == student) {
			return id;
		}
		// auto close session object
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			/**
			 * Creating a Data access object before inserting record into DB.
			 */
			StudentEntity student2 = new StudentEntity();
			if (null != student) {
				student2.setEmail(student.getEmail());
				student2.setFirstName(student.getFirstName());
				student2.setLastName(student.getLastName());
			}
			// start the transaction
			transaction = session.beginTransaction();

			// save student object
			id = (int) session.save(student2);

			// commit transaction
			transaction.commit();
			session.close();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return id;
	}

	public String getStudentById(Integer id) {

		Transaction transaction = null;
		StudentEntity studentEntity = new StudentEntity();
		Student student = new Student();
		if (0 == id) {
			return "No Student Found";
		}
		// auto close session object
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			/**
			 * Creating a Data access object before inserting record into DB.
			 */
			// start the transaction
			transaction = session.beginTransaction();

			// save student object
			studentEntity = session.get(StudentEntity.class, id);

			if (null != studentEntity) {
				student.setEmail(studentEntity.getEmail());
				student.setFirstName(studentEntity.getFirstName());
				student.setLastName(studentEntity.getLastName());
			}

			// commit transaction
			transaction.commit();
			session.close();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return student.getFirstName();
	}

	public String updateStudentById(Integer id, String emailId) {

		Transaction transaction = null;
		StudentEntity studentEntity = new StudentEntity();
		if (0 == id) {
			return "No Student Found";
		}
		// auto close session object
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			/**
			 * Creating a Data access object before inserting record into DB.
			 */
			// start the transaction
			transaction = session.beginTransaction();

			// save student object
			studentEntity = session.get(StudentEntity.class, id);

			if (null != studentEntity) {
				System.out.println("FOUND STUDENT");
				studentEntity.setEmail(emailId);
				session.update(studentEntity);
			}

			// commit transaction
			transaction.commit();
			session.close();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return "Update Completed. Please check database";
	}

}