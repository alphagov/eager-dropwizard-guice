package uk.gov.verify.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.squarespace.jersey2.guice.BootstrapUtils;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceBundleTest {
    public static String SOME_VALUE = "SOME VALUE WE EXPECT";

    public static class GuiceBundledApplication extends Application<GuiceBundledConfiguration> {

        public static void main(String[] args) throws Exception {
            new GuiceBundledApplication().run(args);
        }

        @Override
        public void initialize(Bootstrap<GuiceBundledConfiguration> bootstrap) {
            AbstractModule abstractModule = new AbstractModule() {
                @Override
                protected void configure() {
                }

                @Provides
                @Named("a value from configuration")
                public String injectableValueFromConfiguration(GuiceBundledConfiguration configuration) {
                    return configuration.getValue();
                }
            };
            GuiceBundle<GuiceBundledConfiguration> guiceBundle = new GuiceBundleBuilder<GuiceBundledConfiguration>()
                    .setConfigClass(GuiceBundledConfiguration.class)
                    .setModules(abstractModule)
                    .build();
            bootstrap.addBundle(guiceBundle);
        }

        @Override
        public void run(GuiceBundledConfiguration configuration, Environment environment) throws Exception {
            environment.jersey().register(InjectableResource.class);
        }

        @Path("/injector")
        public static class InjectableResource {

            private final String value;

            @Inject
            public InjectableResource(@Named("a value from configuration") String value) {
                this.value = value;
            }

            @GET
            public String returnHubEntityIdFromMetadata() {
                return value;
            }
        }

    }
    private static class GuiceBundledConfiguration extends Configuration {

        public String getValue() {
            return SOME_VALUE;
        }
    }

    @BeforeClass
    public static void before(){
        BootstrapUtils.reset();
    }
    @ClassRule
    public static DropwizardAppRule<GuiceBundledConfiguration>  APP_RULE = new DropwizardAppRule<>(GuiceBundledApplication.class, ResourceHelpers.resourceFilePath("guice_bundled_application.yml"));

    @Test
    public void shouldReturnTheValueBoundUsingTheGuiceInjector() throws Exception {
        Client client = new JerseyClientBuilder(APP_RULE.getEnvironment()).build("client");
        String receivedValue = client.target("http://localhost:" + APP_RULE.getLocalPort() + "/injector").request().get(String.class);
        assertThat(receivedValue).isEqualTo(SOME_VALUE);
    }
}