package org.github.eq4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class ValueFactory
{
	private static final List<Producer> producers = Arrays.<Producer> asList(
		new IntegerProducer(), new ByteProducer()
	);

	public static Object uniqueValueFor(final Class<?> type)
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
		private static final AtomicLong sequence = new AtomicLong(Long.MIN_VALUE);

		abstract boolean canProduce(final Class<?> type);
		abstract Object produce(final Class<?> type);

		protected Long nextSequence()
		{
			return Long.valueOf(sequence.incrementAndGet());
		}
	}

	private static class ByteProducer extends Producer
	{
		@Override
		public boolean canProduce(final Class<?> type)
		{
			return Byte.class.equals(type) || byte.class.equals(type);
		}

		@Override
		public Object produce(final Class<?> type)
		{
			return Byte.valueOf(nextSequence().byteValue());
		}
	}

	private static class IntegerProducer extends Producer
	{
		@Override
		public boolean canProduce(final Class<?> type)
		{
			return Integer.class.equals(type) || int.class.equals(type);
		}

		@Override
		public Object produce(final Class<?> type)
		{
			return Integer.valueOf(nextSequence().intValue());
		}
	}
}
