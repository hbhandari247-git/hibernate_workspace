package net.javaguides.hibernate.util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import net.javaguides.hibernate.entity.StudentEntity;
import java.io.*;

public class HibernateUtil {
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				Configuration configuration = new Configuration();

				// Hibernate settings equivalent to hibernate.cfg.xml's properties
				// Via Hardcode Java
				/*
				 * Properties settings = new Properties(); settings.put(Environment.DRIVER,
				 * "com.mysql.cj.jdbc.Driver"); settings.put(Environment.URL,
				 * "jdbc:mysql://localhost:3306/java_dmeo?useSSL=false");
				 * settings.put(Environment.USER, "root"); settings.put(Environment.PASS,
				 * "##Seba04278"); settings.put(Environment.DIALECT,
				 * "org.hibernate.dialect.MySQL5Dialect");
				 * 
				 * settings.put(Environment.SHOW_SQL, "true");
				 * 
				 * settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
				 * 
				 * settings.put(Environment.HBM2DDL_AUTO, "update");
				 * 
				 * configuration.setProperties(settings);
				 */

	            // Load properties from file
	            Properties properties = new Properties();
	            InputStream input = new FileInputStream("C:\\Users\\Srinivas\\git\\hibernate_workspace\\maven-demo-project\\src\\main\\resources\\config.properties");
	            properties.load(input);

	            // Apply properties to Hibernate configuration
	            configuration.setProperty(Environment.DRIVER, properties.getProperty("database.env"));
	            configuration.setProperty(Environment.URL, properties.getProperty("database.url"));
	            configuration.setProperty(Environment.USER, properties.getProperty("database.username"));
	            configuration.setProperty(Environment.PASS, properties.getProperty("database.password"));
	            configuration.setProperty(Environment.DIALECT, properties.getProperty("hibernate.dialect"));
	            configuration.setProperty(Environment.SHOW_SQL, properties.getProperty("hibernate.show_sql"));
	            configuration.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, properties.getProperty("hibernate.current_session_context_class"));
	            configuration.setProperty(Environment.HBM2DDL_AUTO, properties.getProperty("hibernate.hbm2ddl.auto"));
	            configuration.setProperty(Environment.C3P0_MIN_SIZE, properties.getProperty("hibernate.c3p0.min_size"));
	            configuration.setProperty(Environment.C3P0_MAX_SIZE, properties.getProperty("hibernate.c3p0.max_size"));
	            configuration.setProperty(Environment.C3P0_TIMEOUT, properties.getProperty("hibernate.c3p0.timeout"));
	            configuration.setProperty(Environment.C3P0_MAX_STATEMENTS, properties.getProperty("hibernate.c3p0.max_statements"));
	            configuration.setProperty(Environment.C3P0_IDLE_TEST_PERIOD, properties.getProperty("hibernate.c3p0.idle_test_period"));

	            // Add annotated classes
	            configuration.addAnnotatedClass(StudentEntity.class);

	            // Build session factory
	            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	                    .applySettings(configuration.getProperties()).build();
	            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}
}