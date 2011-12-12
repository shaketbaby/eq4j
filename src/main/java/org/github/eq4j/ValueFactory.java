package org.github.eq4j;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.MAX_RADIX;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

class ValueFactory
{
	private static final Sequence sequence = new Sequence();
	private static final List<Producer> producers = producers();

	public static Object valueFor(final Class<?> type) throws Exception
	{
		return findProducer(type).produce(type);
	}

	private static Producer findProducer(final Class<?> type)
	{
		for (Producer producer : producers) {
			if (producer.canProduce(type)) {
				return producer;
			}
		}
		throw new IllegalArgumentException("Can not produce value for type[" + type + "].");
	}

	private static abstract class Producer
	{
		protected List<Class<?>> classes;

		Producer(final Class<?>... classes)
		{
			this.classes = Arrays.asList(classes);
		}

		boolean canProduce(final Class<?> type)
		{
			return classes.contains(type);
		}

		abstract Object produce(final Class<?> type) throws Exception;
	}

	private static List<Producer> producers()
	{
		return Arrays.<Producer>asList(
			new Producer(String.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return String.valueOf(sequence.nextInt());
				}
			},
			new Producer(int.class, Integer.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return sequence.nextInt();//Integer.valueOf();
				}
			},
			new Producer(long.class, Long.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Long.valueOf(sequence.nextLong());
				}
			},
			new Producer()
			{
				@Override
				boolean canProduce(final Class<?> type)
				{
					return type.isEnum();
				}

				@Override
				public Object produce(final Class<?> type)
				{
					final Object[] enumConstants = type.getEnumConstants();
					return enumConstants[Math.abs(sequence.nextInt()) % enumConstants.length];
				}
			},
			new Producer(BigDecimal.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return new BigDecimal(sequence.nextInt());
				}
			},
			new Producer(java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class)
			{
				@Override
				public Object produce(final Class<?> type) throws Exception
				{
					return type.getConstructor(long.class).newInstance(sequence.nextLong());
				}
			},
			new Producer(boolean.class, Boolean.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return sequence.nextInt() % 2 == 0 ? TRUE : FALSE;
				}
			},
			new Producer(double.class, Double.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Double.valueOf(sequence.nextInt());
				}
			},
			new Producer()
			{
				@Override
				boolean canProduce(final Class<?> type)
				{
					return type.isArray();
				}

				@Override
				public Object produce(final Class<?> type)
				{
					return Array.newInstance(type.getComponentType(), 0);
				}
			},
			new Producer(float.class, Float.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Float.valueOf(sequence.nextInt());
				}
			},
			new Producer(short.class, Short.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Short.valueOf((short)sequence.nextInt());
				}
			},
			new Producer(byte.class, Byte.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Byte.valueOf((byte)sequence.nextInt());
				}
			},
			new Producer(BigInteger.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return BigInteger.valueOf(sequence.nextInt());
				}
			},
			new Producer(char.class, Character.class)
			{
				@Override
				public Object produce(final Class<?> type)
				{
					return Character.forDigit(sequence.nextInt() % MAX_RADIX, MAX_RADIX);
				}
			}
		);
	}

	private static class Sequence extends ThreadLocal<Sequence.Value>
	{
		@Override
		protected Value initialValue()
		{
			return new Value();
		}

		int nextInt()
		{
			return get().next();
		}

		long nextLong()
		{
			return nextInt();
		}

		private class Value
		{
			private int value = MIN_VALUE;

			private int next()
			{
				if (MAX_VALUE == value) {
					value = MIN_VALUE;
				}
				return ++value;
			}
		}
	}

	// method only to be used by test class
	static void setSequenceValue(final int value) {
		sequence.get().value = value;
	}
}
