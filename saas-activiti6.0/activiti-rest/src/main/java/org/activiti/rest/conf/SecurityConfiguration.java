package org.activiti.rest.conf;

import org.activiti.rest.security.BasicAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  
  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new BasicAuthenticationProvider();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  /*
	   * crossdomain 문제 해결 - https://forums.activiti.org/content/options-request-returning-401
	   * http
		.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
		.authenticationProvider(authenticationProvider())
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.csrf().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.OPTIONS, "**").permitAll()//allow CORS option calls
		.anyRequest().authenticated()
		.and()
		.httpBasic();
	   * */
	  http
		.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
		.authenticationProvider(authenticationProvider())
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.csrf().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.OPTIONS, "**").permitAll()//allow CORS option calls
		//.antMatchers("/resources/**").permitAll()
		.anyRequest().authenticated()
		.and()
		.httpBasic();
	 /* 
  	 http
     .authenticationProvider(authenticationProvider())
     .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
     .csrf().disable()
     .authorizeRequests()
       .anyRequest().authenticated()
       .and()
     .httpBasic();
     */
  }
}
