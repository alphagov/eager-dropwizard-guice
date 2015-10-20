package uk.gov.verify.guice;

import com.google.inject.Module;
import io.dropwizard.Configuration;

import java.util.Arrays;
import java.util.Optional;

public class GuiceBundleBuilder<T extends Configuration> {
    private Iterable<Module> modules;
    private Optional<Class<T>> configClass = Optional.empty();

    public GuiceBundleBuilder<T> setModules(Iterable<Module> modules) {
        this.modules = modules;
        return this;
    }

    public GuiceBundleBuilder<T> setModules(Module... modules) {
        this.modules = Arrays.asList(modules);
        return this;
    }

    public GuiceBundleBuilder<T> setConfigClass(Class<T> configClass) {
        this.configClass = Optional.of(configClass);
        return this;
    }

    public GuiceBundle<T> build() {
        return new GuiceBundle<T>(configClass, modules);
    }
}