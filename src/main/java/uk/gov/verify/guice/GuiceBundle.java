package uk.gov.verify.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.JerseyModule;
import com.hubspot.dropwizard.guice.JerseyUtil;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Optional;

import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final Optional<Class<T>> configClass;
    private final Iterable<Module> modules;

    @SuppressWarnings("unused")
    public Injector getInjector() {
        return injector;
    }

    private Injector injector;

    GuiceBundle(Optional<Class<T>> configClass, Iterable<Module> modules) {
        this.configClass = configClass;
        this.modules = modules;
    }

    @Override
    public void run(T configuration, Environment environment) {
        injector = Guice.createInjector(Stage.PRODUCTION, getModules(configuration, environment));
        JerseyUtil.registerGuiceBound(injector, environment.jersey());
        JerseyUtil.registerGuiceFilter(environment);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {}

    private Iterable<? extends Module> getModules(T configuration, Environment environment) {
        DropwizardEnvironmentModule dropwizardEnvironmentModule = new DropwizardEnvironmentModule<T>(configClass, configuration, environment);
        JerseyModule jerseyModule = new JerseyModule();
        return concat(asList(dropwizardEnvironmentModule, jerseyModule), modules);
    }

}
