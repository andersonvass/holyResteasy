package org.holy.holyResteasy.web.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJBInjector implements PropertyInjector {

	private static final Logger logger = LoggerFactory
			.getLogger(EJBInjector.class);

	private InitialContext itx;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void inject(final Object obj) {

		List<Field> fields = getFieldsFromClassAndSuperclasses(obj.getClass());

		for (final Field field : fields) {
			final Annotation[] annotations = field.getDeclaredAnnotations();
			for (final Annotation annotation : annotations) {
				AccessController.doPrivileged(new PrivilegedAction() {

					public Object run() {
						try {
							final Object objectToInject = lookup(field,
									annotation);
							if (objectToInject != null) {
								field.setAccessible(true);
								field.set(obj, objectToInject);
							}
						} catch (IllegalArgumentException e) {
							logger.error("Erro while trying to inject an EJB.",
									e);
						} catch (IllegalAccessException e) {
							logger.error("Erro while trying to inject an EJB.",
									e);
						}
						return null;
					}
				});
			}
		}
	}

	public List<Field> getFieldsFromClassAndSuperclasses(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		Class<?> superClass = clazz.getSuperclass();

		while (superClass != null) {
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}

		return fields;
	}

	private Object lookup(Field field, Annotation annotation) {
		if (annotation.annotationType().equals(EJB.class)) {
			String name = ((EJB) annotation).name();
			if (name == null || name.isEmpty()) {
				logger.error("EJB annotation without name attribute is not implemented because the EJB spec does not defines a common way for binding JNDI names.");
			}

			logger.debug("EJB Lookup {}.", name);

			return lookup(name);
		} else {
			return null;
		}
	}

	protected Object lookup(String name) {
		try {
			return getInitialContext().lookup(name);
		} catch (NamingException e) {
			logger.error("Erro while trying to inject an EJB.", e);
			return null;
		}
	}

	protected InitialContext getInitialContext() throws NamingException {
		if (this.itx == null) {
			this.itx = new InitialContext();
		}
		return this.itx;
	}

	@Override
	public void inject(HttpRequest request, HttpResponse response, Object obj)
			throws Failure, WebApplicationException, ApplicationException {
		inject(obj);
	}
}