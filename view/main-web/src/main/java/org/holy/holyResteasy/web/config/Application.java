package org.holy.holyResteasy.web.config;

import java.util.HashSet;
import java.util.Set;

import org.holy.holyResteasy.web.user.UserHB;

public class Application extends javax.ws.rs.core.Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(UserHB.class);
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<Object>();
		return singletons;
	}
}