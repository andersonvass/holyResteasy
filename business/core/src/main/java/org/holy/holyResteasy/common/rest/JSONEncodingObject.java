package org.holy.holyResteasy.common.rest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JSONEncodingObject extends JsonObject {

	private String content;
	private Object object;

	public JSONEncodingObject(@SuppressWarnings("rawtypes") Map map) {
		super(map);
	}

	public JSONEncodingObject(Object pObject, String dateFormatPattern) {
		super(pObject);
		object = pObject;
		init(dateFormatPattern);
	}

	public JSONEncodingObject(Object pObject) {
		this(pObject, null);
	}

	private static final JsonDeserializer<String> STRING_UTF8_DESERIALIZER = new JsonDeserializer<String>() {

		@Override
		public String deserialize(JsonElement json, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {
			try {
				// Escapando o caracter %
				String string = json.getAsString();
				string = string.replaceAll("\\%", "%25");
				string = string.replaceAll("\\+", "%2B");

				return URLDecoder.decode(string, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"Erro ao converter o objeto, encoding invalido.", e);

			}
		}
	};

	private static final JsonSerializer<String> STRING_UTF8_SERIALIZER = new JsonSerializer<String>() {
		@Override
		public JsonElement serialize(String src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src);
		}
	};

	private static final JsonSerializer<Float> FLOAT_SERIALIZER = new JsonSerializer<Float>() {

		@Override
		public JsonElement serialize(Float value, Type typeOfValue,
				JsonSerializationContext context) {
			NumberFormat numberFormat = NumberFormat.getInstance(new Locale(
					"pt", "BR"));
			numberFormat.setMaximumFractionDigits(2);
			numberFormat.setMinimumFractionDigits(2);

			if (value != null) {
				return new JsonPrimitive(numberFormat.format(value));
			} else {
				return new JsonPrimitive(numberFormat.format(0.00));
			}
		}

	};

	private final GsonBuilder GSON_BUILDER_INSTANCE = new GsonBuilder();

	// private static Gson GSON;

	public JSONEncodingObject setNewDateFormat(String pDateFormat) {
		GSON_BUILDER_INSTANCE.setDateFormat(pDateFormat);
		return this;
	}

	public JSONEncodingObject(final String pContent) {
		super(pContent);
		content = pContent;
		init(null);
	}

	private void init(String dateFormatPattern) {

		GSON_BUILDER_INSTANCE.registerTypeAdapter(String.class,
				STRING_UTF8_SERIALIZER);
		GSON_BUILDER_INSTANCE.registerTypeAdapter(String.class,
				STRING_UTF8_DESERIALIZER);
		GSON_BUILDER_INSTANCE
				.registerTypeAdapter(float.class, FLOAT_SERIALIZER);

		if (dateFormatPattern == null) {
			GSON_BUILDER_INSTANCE.setDateFormat("dd/MM/yyyy");
		} else {
			GSON_BUILDER_INSTANCE.setDateFormat(dateFormatPattern);
		}
	}

	@Override
	public String toJson() {
		if (object == null) {
			throw new RuntimeException("O objeto nao pode ser nulo");
		}
		Gson gSon = GSON_BUILDER_INSTANCE.create();
		return gSon.toJson(this.object);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T toObject(Class clazz) {
		if (this.content == null) {
			this.content = toJson();
		}

		Gson gSon = GSON_BUILDER_INSTANCE.create();
		return (T) gSon.fromJson(this.content, clazz);
	}

	/**
	 * Exemplo de uso:
	 * 
	 * json.toList(new TypeToken<List<ProjetoPma>>() {});
	 */
	public <T> List<T> toList(TypeToken<?> typeToken) {
		if (this.content == null) {
			this.content = toJson();
		}

		if (this.content.trim().equals("{}")) {
			return new ArrayList<T>();
		}

		Gson gSon = GSON_BUILDER_INSTANCE.create();
		Type listType = typeToken.getType();
		return gSon.fromJson(this.content, listType);
	}
}
