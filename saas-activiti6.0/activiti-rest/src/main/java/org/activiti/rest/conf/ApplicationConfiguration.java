package org.activiti.rest.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({@org.springframework.context.annotation.PropertySource(value={"classpath:/property/db.properties"}, ignoreResourceNotFound=true), @org.springframework.context.annotation.PropertySource(value={"classpath:/property/engine.properties"}, ignoreResourceNotFound=true)})
@ComponentScan(basePackages={"org.activiti.rest.conf"})
@ImportResource({"classpath:/property/activiti-custom-context.xml"})
public class ApplicationConfiguration
{
}