#eager-dropwizard-guice [![Build Status](https://travis-ci.org/alphagov/eager-dropwizard-guice.svg?branch=master)](https://travis-ci.org/alphagov/eager-dropwizard-guice)
This library is a wrapper around Hubspot' [`dropwizard-guice`](http://www.github.com/hubspot/dropwizard-guice) library. It provides a `GuiceBundle` for Dropwizard that initialises the dropwizard injector at runtime. It has a builder which providers customisation of the host application configuration class and the ability to set module. There is no support for auto-config by design.
