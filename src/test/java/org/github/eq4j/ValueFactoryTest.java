package org.github.eq4j;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

public class ValueFactoryTest
{
	private enum TestEnum
	{
		VALUE_1, VALUE_2, VALUE_3
	}

	private int sequenceValue = 100;

	@Before
	public void resetSequenceValue()
	{
		ValueFactory.setSequenceValue(sequenceValue++);
	}

	@Test
	public void shouldBeAbleToProduceValuesForSupportedTypes() throws Exception
	{
		Class<?>[] types = {
			Byte.class, Short.class, Integer.class, Long.class,
			Float.class, Double.class, Boolean.class, Character.class,
			TestEnum.class, BigInteger.class, BigDecimal.class, String.class,
			java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
			int[].class, String[][].class, TestEnum[][][].class, Object[].class
		};

		for (Class<?> type : types)
		{
			Object value = ValueFactory.valueFor(type);
			assertThat(value, is(type));
			assertThat(value, is(notNullValue()));
		}
	}

	@Test
	public void shouldRestartFromMinIntWhenReachedMaxInt() throws Exception
	{
		assertThat(ValueFactory.valueFor(Integer.TYPE), is((Object)sequenceValue));

		ValueFactory.setSequenceValue(MAX_VALUE);

		assertThat(ValueFactory.valueFor(Integer.TYPE), is((Object)(Integer.MIN_VALUE + 1)));
	}

	@Test
	public void shouldProduceSequencialValueAcrossTypes() throws Exception
	{
		assertThat(ValueFactory.valueFor(Integer.class), is((Object)sequenceValue++));
		assertThat(ValueFactory.valueFor(byte.class), is((Object)valueOf(sequenceValue++).byteValue()));
		assertThat(ValueFactory.valueFor(Short.class), is((Object)valueOf(sequenceValue++).shortValue()));
		assertThat(ValueFactory.valueFor(long.class), is((Object)valueOf(sequenceValue++).longValue()));
	}

	@Test
	public void shouldProduceSequenceValuesIndependentlyBetweenThreads() throws Exception
	{
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<Integer> future1 = executor.submit(callable(2, sequenceValue));
		Future<Integer> future2 = executor.submit(callable(5, sequenceValue));
		assertThat(future1.get(), is(sequenceValue + 2));
		assertThat(future2.get(), is(sequenceValue + 5));
		executor.shutdown();
	}

	private Callable<Integer> callable(final int count, final int initValue) {
		return new Callable<Integer>()
		{
			@Override
			public Integer call() throws Exception
			{
				ValueFactory.setSequenceValue(initValue);
				Thread.sleep(300);

				Integer result = null;
				Class<Integer> type = Integer.class;
				for (int i = 0; i < count; i++)
				{
					result = type.cast(ValueFactory.valueFor(type));
				}
				return result;
			}
		};
	}
}
