package com.jjinterna.pbxevents.action.http.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.DefaultRootObjectLocator;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public class HttpQueryToStringStrategy implements ToStringStrategy {

	private String fieldSeparator = "&";
	private String fieldNameValueSeparator = "=";

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, boolean value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, byte value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, char value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, double value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, float value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, int value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, long value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, short value) {
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, Object value) {
		return stringBuilder;
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, boolean[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, char[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, double[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, float[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, int[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, long[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, short[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder append(ObjectLocator locator,
			StringBuilder stringBuilder, Object[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendStart(ObjectLocator parentLocator,
			Object parent, StringBuilder stringBuilder) {
		if (parent != null) {
			stringBuilder.append("Event");
			stringBuilder.append(fieldNameValueSeparator);
			stringBuilder.append(parent.getClass().getSimpleName());
		}
		return stringBuilder;
	}

	@Override
	public StringBuilder appendEnd(ObjectLocator parentLocator, Object parent,
			StringBuilder stringBuilder) {
		return stringBuilder;
	}

	protected void appendFieldStart(ObjectLocator parentLocator, Object parent,
			String fieldName, StringBuilder buffer) {
		if (fieldName != null) {
			buffer.append(fieldSeparator);
			if (parentLocator != null) {
				buffer.append(parent.getClass().getSimpleName());
				buffer.append('.');
			}
			buffer.append(fieldName);
			buffer.append(fieldNameValueSeparator);
		}
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			boolean value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			byte value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			char value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			double value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			float value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			int value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			long value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			short value) {
		appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
		stringBuilder.append(value);
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			Object value) {
		if (value == null) {
			return stringBuilder;
		}
		if (value instanceof String) {
			appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
			try {
				stringBuilder
						.append(URLEncoder.encode((String) value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				stringBuilder.append("java.io.UnsupportedEncodingException");
			}
		} else if (value instanceof ToString) {
			((ToString) value).appendFields(
					new DefaultRootObjectLocator(parent), stringBuilder, this);
		} else {
			appendFieldStart(parentLocator, parent, fieldName, stringBuilder);
			stringBuilder.append(value.toString());
		}
		return stringBuilder;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			boolean[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			char[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			double[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			float[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			int[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			long[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			short[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator,
			Object parent, String fieldName, StringBuilder stringBuilder,
			Object[] value) {
		throw new UnsupportedOperationException();
	}

	public static final ToStringStrategy INSTANCE = new HttpQueryToStringStrategy();
}
