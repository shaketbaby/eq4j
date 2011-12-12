package org.github.eq4j;

import static org.github.eq4j.Registry.retrievePath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.sf.cglib.proxy.Enhancer;

import org.junit.Test;

public class ProxyTest
{
	@Test
	public void shouldBeAbleToCreateProxyForEntityClass()
	{
		TestEntity testEntity = Entities.entityOf(TestEntity.class);
		assertTrue(Enhancer.isEnhanced(testEntity.getClass()));
		assertThat(testEntity, is(instanceOf(org.github.eq4j.Entity.class)));
	}

	@Test
	public void shouldBeAbleToCallMethodOfEntityInterfaceAndNotGetRecorded()
	{
		org.github.eq4j.Entity entity = asEntity(Entities.entityOf(TestEntity.class));
		Path path = entity.getPath_EQ4J();
		assertThat(retrievePath(path), is(nullValue()));
		assertThat(path, equalTo(new Path("", null)));

		entity.setAlias_EQ4J("te");
		assertThat(entity.getPath_EQ4J(), equalTo(new Path("te", null)));
	}

	@Test
	public void shouldBeAbleToRecordPathToSimpleProperty()
	{
		TestEntity testEntity = entityWithAlias("te");

		Path path = retrievePath(testEntity.getId());
		assertThat(path.getFullPath(), equalTo("te.id"));

		path = asEntity(testEntity.getLastAccess()).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("te.lastAccess"));

		path = asEntity(testEntity.lastModified()).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("te.lastModified"));

		path = retrievePath(testEntity.getName());
		assertThat(path.getFullPath(), equalTo("te.name"));

		path = asEntity(testEntity.getOrders()).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("te.orders"));

		path = retrievePath(testEntity.getBlob());
		assertThat(path.getFullPath(), equalTo("te.blob"));
	}

	@Test
	public void shouldBeAbleToRecordPathForEmbeddedProperty()
	{
		TestEntity testEntity = entityWithAlias("alias");

		Path path = asEntity(testEntity.getAddress()).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("alias.address"));

		path = retrievePath(testEntity.getAddress().getStreet());
		assertThat(path.getFullPath(), equalTo("alias.address.street"));

		path = retrievePath(testEntity.getAddress().getCountry());
		assertThat(path.getFullPath(), equalTo("alias.address.country"));
	}

	@Test
	public void shouldBeAbleToReuseTheProxyAndProducedValue()
	{
		TestEntity testEntity = entityWithAlias("alias");
		Customer customer = testEntity.getCustomer();

		Path path = asEntity(customer).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("alias.customer"));

		Order order = customer.getOrder();
		path = asEntity(order).getPath_EQ4J();
		assertThat(path.getFullPath(), equalTo("alias.customer.order"));

		path = retrievePath(order.getSubmittedTime());
		assertThat(path.getFullPath(), equalTo("alias.customer.order.submittedTime"));

		path = retrievePath(order.getAddress().getCity());
		assertThat(path.getFullPath(), equalTo("alias.customer.order.address.city"));

		path = retrievePath(customer.isMarried());
		assertThat(path.getFullPath(), equalTo("alias.customer.married"));
	}

	@Test
	public void shouldRecordPathSeparatelyForEachThread()
	{
		class ValueHolder
		{
			Object value;
		}

		final TestEntity testEntity = entityWithAlias("alias");
		final ValueHolder v1 = new ValueHolder();
		final ValueHolder v2 = new ValueHolder();
		final CountDownLatch latch = new CountDownLatch(2);
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(new Runnable() {
			@Override
			public void run()
			{
				v1.value = testEntity.lastModified();
				latch.countDown();
				waitForAllToFinish(latch);
				assertThat(retrievePath(v1.value), is(notNullValue()));
				assertThat(retrievePath(v2.value), is(nullValue()));
			}
		});
		executor.submit(new Runnable() {
			@Override
			public void run()
			{
				v2.value = testEntity.getLastAccess();
				latch.countDown();
				waitForAllToFinish(latch);
				assertThat(retrievePath(v1.value), is(nullValue()));
				assertThat(retrievePath(v2.value), is(notNullValue()));
			}
		});
		executor.shutdown();
	}

	private void waitForAllToFinish(final CountDownLatch latch)
	{
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private TestEntity entityWithAlias(final String alias)
	{
		TestEntity testEntity = Entities.entityOf(TestEntity.class);
		asEntity(testEntity).setAlias_EQ4J(alias);
		return testEntity;
	}

	private org.github.eq4j.Entity asEntity(final Object proxy)
	{
		return org.github.eq4j.Entity.class.cast(proxy);
	}

	@Entity
	public static class TestEntity
	{
		// simple types
		@Id
		private int id;
		private String name;
		private Calendar lastAccess;
		private java.util.Date lastModified;
		@Embedded
		private Address address;
		// plural types
		private byte[] blob;
		@OneToMany
		private List<Order> orders;
		@ManyToOne
		private Customer customer;

		public int getId()
		{
			return id;
		}

		public void setId(final int id)
		{
			this.id = id;
		}

		public String getName()
		{
			return name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public Calendar getLastAccess()
		{
			return lastAccess;
		}

		public void setLastAccess(final Calendar lastAccess)
		{
			this.lastAccess = lastAccess;
		}

		public java.util.Date lastModified()
		{
			return lastModified;
		}

		public void setLastModified(final java.util.Date lastModified)
		{
			this.lastModified = lastModified;
		}

		public Address getAddress()
		{
			return address;
		}

		public void setAddress(final Address address)
		{
			this.address = address;
		}

		public byte[] getBlob()
		{
			return blob;
		}

		public void setBlob(final byte[] blob)
		{
			this.blob = blob;
		}

		public List<Order> getOrders()
		{
			return orders;
		}

		public void setOrders(final List<Order> orders)
		{
			this.orders = orders;
		}

		public Customer getCustomer()
		{
			return customer;
		}

		public void setCustomer(final Customer customer)
		{
			this.customer = customer;
		}
	}

	@Entity
	public static class Order
	{
		private String id;
		private Timestamp submittedTime;
		private Address address;

		public String getId()
		{
			return id;
		}

		public void setId(final String id)
		{
			this.id = id;
		}

		public Timestamp getSubmittedTime()
		{
			return submittedTime;
		}

		public void setSubmittedTime(final Timestamp submittedTime)
		{
			this.submittedTime = submittedTime;
		}

		public Address getAddress()
		{
			return address;
		}

		public void setAddress(final Address address)
		{
			this.address = address;
		}
	}

	@Embeddable
	public static class Customer
	{
		private boolean married;
		private Order order;

		public Order getOrder()
		{
			return order;
		}

		public void setOrder(final Order order)
		{
			this.order = order;
		}

		public boolean isMarried()
		{
			return married;
		}

		public void setMarried(final boolean married)
		{
			this.married = married;
		}

	}

	@Embeddable
	public static class Address
	{
		private String street;
		private String city;
		private Country country;

		public String getStreet()
		{
			return street;
		}

		public void setStreet(final String street)
		{
			this.street = street;
		}

		public String getCity()
		{
			return city;
		}

		public void setCity(final String city)
		{
			this.city = city;
		}

		public Country getCountry()
		{
			return country;
		}

		public void setCountry(final Country country)
		{
			this.country = country;
		}
	}

	public enum Country
	{
		China, Australia, USA
	}
}
