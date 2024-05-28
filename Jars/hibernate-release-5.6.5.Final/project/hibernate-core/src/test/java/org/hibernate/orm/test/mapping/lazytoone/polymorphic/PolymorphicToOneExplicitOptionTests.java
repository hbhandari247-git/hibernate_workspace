/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.mapping.lazytoone.polymorphic;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.proxy.HibernateProxy;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.EnhancementOptions;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static javax.persistence.FetchType.LAZY;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Steve Ebersole
 */
@RunWith( BytecodeEnhancerRunner.class)
@EnhancementOptions( lazyLoading = true )
public class PolymorphicToOneExplicitOptionTests extends BaseNonConfigCoreFunctionalTestCase {
	@Test
	public void testInheritedToOneLaziness() {
		inTransaction(
				(session) -> {
					sqlStatementInterceptor.clear();

					// NOTE : this test shows an edge case that does not work the way it
					// should.  Because we have a polymorphic to-one, we will have to
					// generate a HibernateProxy for the laziness.  However, the explicit
					// NO_PROXY should force the proxy to be immediately initialized
					// and the target returned.
					//
					// this is the old behavior as well - these HHH-13658 changes did not cause this
					//
					// its an odd edge case however and maybe not that critical.  it essentially
					// asks for the association to be lazy and to also be eager
					//
					// The assertions here are based on what *does* happen.  Whether that is right/wrong
					// is a different discussion

					final Order order = session.byId( Order.class ).getReference( 1 );
					assertThat( sqlStatementInterceptor.getQueryCount(), is( 0 ) );

					System.out.println( "Order # " + order.getId() );
					assertThat( sqlStatementInterceptor.getQueryCount(), is( 0 ) );

					System.out.println( "  - amount : " + order.getAmount() );
					// triggers load of base fetch state
					assertThat( sqlStatementInterceptor.getQueryCount(), is( 1 ) );

					final Customer customer = order.getCustomer();
					// this *should* be 2 - the customer should get loaded
					//int expectedCount = 2;
					// but it is 1 because we get back a HibernateProxy
					int expectedCount = 1;
					assertThat( sqlStatementInterceptor.getQueryCount(), is( expectedCount ) );
					// should be true...
					//assertTrue( Hibernate.isInitialized( customer ) );
					// but is false
					assertFalse( Hibernate.isInitialized( customer ) );
					// should not be a HibernateProxy
					//assertThat( customer, not( instanceOf( HibernateProxy.class ) ) );
					// but is
					assertThat( customer, instanceOf( HibernateProxy.class ) );

					System.out.println( "  - customer : " + customer.getId() );
					assertThat( sqlStatementInterceptor.getQueryCount(), is( expectedCount ) );

					customer.getName();
					// this should not trigger SQL because the customer ought to already be initialized
					// but again that is not the case
					expectedCount++;
					assertThat( sqlStatementInterceptor.getQueryCount(), is( expectedCount ) );
				}
		);
	}

	@Test
	@TestForIssue(jiraKey = "HHH-14659")
	public void testQueryJoinFetch() {
		inTransaction(
				(session) -> {
					final Order order = session.createQuery( "select o from Order o join fetch o.customer", Order.class )
							.uniqueResult();

					assertTrue( Hibernate.isPropertyInitialized( order, "customer" ) );
					Customer customer = order.getCustomer();
					assertTrue( Hibernate.isInitialized( customer ) );
				}
		);
	}

	@Before
	public void createTestData() {
		inTransaction(
				(session) -> {
					final DomesticCustomer customer = new DomesticCustomer( 1, "them", "123" );
					session.persist( customer );
					final Order order = new Order( 1, BigDecimal.ONE, customer );
					session.persist( order );
				}
		);
	}

	@After
	public void dropTestData() {
		inTransaction(
				(session) -> {
					session.createQuery( "delete Order" ).executeUpdate();
					session.createQuery( "delete Customer" ).executeUpdate();
				}
		);
	}

	private SQLStatementInterceptor sqlStatementInterceptor;

	@Override
	protected void applyMetadataSources(MetadataSources sources) {
		super.applyMetadataSources( sources );
		sources.addAnnotatedClass( Order.class );
		sources.addAnnotatedClass( Customer.class );
		sources.addAnnotatedClass( ForeignCustomer.class );
		sources.addAnnotatedClass( DomesticCustomer.class );
	}

	@Override
	protected void configureStandardServiceRegistryBuilder(StandardServiceRegistryBuilder ssrb) {
		super.configureStandardServiceRegistryBuilder( ssrb );
		ssrb.applySetting( AvailableSettings.ALLOW_ENHANCEMENT_AS_PROXY, true );
		sqlStatementInterceptor = new SQLStatementInterceptor( ssrb );
	}

	@Entity( name = "Order" )
	@Table( name = "`order`" )
	public static class Order {
		@Id
		private Integer id;
		private BigDecimal amount;
		@ManyToOne( fetch = LAZY, optional = false )
		@LazyToOne( LazyToOneOption.NO_PROXY )
		private Customer customer;

		public Order() {
		}

		public Order(Integer id, BigDecimal amount, Customer customer) {
			this.id = id;
			this.amount = amount;
			this.customer = customer;
		}

		public Integer getId() {
			return id;
		}

		private void setId(Integer id) {
			this.id = id;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
	}

	@Entity( name = "Customer" )
	@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
	public static abstract class Customer {
		@Id
		private Integer id;
		private String name;

		public Customer() {
		}

		public Customer(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		private void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Entity(name = "ForeignCustomer")
	@Table(name = "foreign_cust")
	public static class ForeignCustomer extends Customer {
		private String vat;

		public ForeignCustomer() {
			super();
		}

		public ForeignCustomer(Integer id, String name, String vat) {
			super( id, name );
			this.vat = vat;
		}

		public String getVat() {
			return vat;
		}

		public void setVat(String vat) {
			this.vat = vat;
		}
	}

	@Entity(name = "DomesticCustomer")
	@Table(name = "domestic_cust")
	public static class DomesticCustomer extends Customer {
		private String taxId;

		public DomesticCustomer() {
			super();
		}

		public DomesticCustomer(Integer id, String name, String taxId) {
			super( id, name );
			this.taxId = taxId;
		}

		public String getTaxId() {
			return taxId;
		}

		public void setTaxId(String taxId) {
			this.taxId = taxId;
		}
	}
}
