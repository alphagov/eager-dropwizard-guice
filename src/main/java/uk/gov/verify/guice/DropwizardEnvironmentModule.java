package uk.gov.verify.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

import java.util.Optional;

public class DropwizardEnvironmentModule<T extends Configuration> extends AbstractModule {
    private final Optional<Class<T>> configClassOptional;
    private final T configuration;
    private final Environment environment;

    public DropwizardEnvironmentModule(Optional<Class<T>> configClassOptional, T configuration, Environment environment) {
        this.configClassOptional = configClassOptional;
        this.configuration = configuration;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        configClassOptional.ifPresent((configClass) -> bind(configClass).toInstance(configuration));
        bind(Configuration.class).toInstance(configuration);
        bind(Environment.class).toInstance(environment);
        bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
    }
}
