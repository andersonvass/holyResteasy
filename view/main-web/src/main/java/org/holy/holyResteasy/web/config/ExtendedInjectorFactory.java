package org.holy.holyResteasy.web.config;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ExtendedInjectorFactory extends InjectorFactoryImpl {

	public ExtendedInjectorFactory() {
		super(ResteasyProviderFactory.getInstance());
	}

	public ExtendedInjectorFactory(ResteasyProviderFactory factory) {
		super(factory);
	}

	@Override
	public PropertyInjector createPropertyInjector(
			@SuppressWarnings("rawtypes") Class resourceClass) {
		return new EJBInjector();
	}
}