package org.holy.holyResteasy.common.rest;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flexjson.JSONDeserializer;

public class JsonObject {

	protected static final String DATE_FORMAT0 = "yyyy-MM-dd";
	protected static final String DATE_FORMAT1 = "yyyy-MM-dd HH:mm";
	protected static final String DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
	protected static final String DATE_FORMAT3 = "yyyy-MM-dd HH:mm:ss:SSS";
	protected static final Gson GSON_BUILDER_INSTANCE = new GsonBuilder()
			.setDateFormat(DATE_FORMAT0).create();

	private static final Logger logger = LoggerFactory
			.getLogger(JsonObject.class);

	@SuppressWarnings("rawtypes")
	protected Map map;
	protected String content;
	protected Object object;

	public JsonObject(String content) {
		this.content = content;
	}

	public JsonObject(Object object) {
		this.object = object;
	}

	@SuppressWarnings("rawtypes")
	public JsonObject(Map map) {
		this.map = map;
		this.content = map.toString();
	}

	public String getAsString(String expression) {
		return getValue(expression, stringTransform);
	}

	public Integer getAsInt(String expression) {
		return getValue(expression, integerTransform);
	}

	public Long getAsLong(String expression) {
		return getValue(expression, longTransform);
	}

	public Double getAsDouble(String expression) {
		return getValue(expression, doubleTransform);
	}

	public BigDecimal getAsBigDecimal(String expression) {
		return new BigDecimal(getAsDouble(expression));
	}

	public Date getAsDate(String expression) {
		return getValue(expression, dateTransform);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<JsonObject> getAsList(String expression) {
		try {
			List<Map> list = (List<Map>) getValue(expression);
			if (list == null) {
				return null;
			}

			List<JsonObject> result = new ArrayList<JsonObject>(list.size());

			for (Map map : list) {
				result.add(new JsonObject(map));
			}

			return result;
		} catch (ClassCastException e) {
			logger.info("Invalid expression: {}", expression);
			return null;
		}
	}

	public String toJson() {
		if (this.object == null) {
			throw new RuntimeException("The object can not be null!");
		}
		return GSON_BUILDER_INSTANCE.toJson(object);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T toObject(Class clazz) {

		if (content == null) {
			content = this.toJson();
		}

		return (T) GSON_BUILDER_INSTANCE.fromJson(content, clazz);
	}

	protected <T> T getValue(String expression, Transform<T> transform) {
		Object value = getValue(expression);

		if (value != null) {
			T result = transform.transform(value.toString());
			return result;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected Object getValue(String expression) {
		if (this.map == null) {
			deserialize(content);
		}

		logger.debug("Processing: {}", expression);

		String[] entities = expression.split("\\.");

		Map map = this.map;
		Object value = null;
		for (int i = 0; i < entities.length; i++) {
			if (value != null) {
				try {
					map = (Map) value;
				} catch (ClassCastException e) {
					logger.info("Invalid expression: {}", expression);
					return null;
				}
			}
			value = map.get(entities[i]);
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	protected void deserialize(String content) {
		this.map = (Map) new JSONDeserializer().deserialize(content);
	}

	protected static abstract class Transform<T> {

		public abstract T transform(String value);
	}

	protected static final Transform<String> stringTransform = new Transform<String>() {

		@Override
		public String transform(String value) {
			return value;
		}
	};

	protected static final Transform<Integer> integerTransform = new Transform<Integer>() {

		@Override
		public Integer transform(String value) {
			return Integer.valueOf(value);
		}
	};

	protected static final Transform<Long> longTransform = new Transform<Long>() {

		@Override
		public Long transform(String value) {
			return Long.valueOf(value);
		}
	};

	protected static final Transform<Double> doubleTransform = new Transform<Double>() {

		@Override
		public Double transform(String value) {
			return Double.valueOf(value);
		}
	};

	protected static final Transform<Date> dateTransform = new Transform<Date>() {

		private DateFormat dateFormat1 = new SimpleDateFormat(DATE_FORMAT1);
		private DateFormat dateFormat2 = new SimpleDateFormat(DATE_FORMAT2);
		private DateFormat dateFormat3 = new SimpleDateFormat(DATE_FORMAT3);

		@Override
		public Date transform(String value) {
			if (value.contains("-")) {
				try {
					return dateFormat3.parse(value);
				} catch (ParseException e) {
					try {
						return dateFormat2.parse(value);
					} catch (ParseException e1) {
						try {
							return dateFormat1.parse(value);
						} catch (ParseException e2) {
							throw new RuntimeException(value, e);
						}
					}
				}
			} else {
				return new Date(Long.valueOf(value));
			}
		}
	};

	@Override
	public String toString() {
		return toJson();
	}
}
