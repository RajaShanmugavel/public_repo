package com.abc.raja.usermgmt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// http.authorizeRequests().antMatchers("/",
		// "/home").permitAll().anyRequest().authenticated().and().formLogin()
		// .loginPage("/login").permitAll().and().logout().permitAll();
		// http.anonymous().authorities("ROLE_ANONYMOUS").and().exceptionHandling().and().servletApi().and()
		// .authorizeRequests().antMatchers("/").permitAll().anyRequest().authenticated();
		http.authorizeRequests().antMatchers("/", "/adduser", "/state", "/chars", "/sum").access("hasRole('USER')")
				.and().formLogin().defaultSuccessUrl("/adduser").and().exceptionHandling()
				.accessDeniedPage("/Access_Denied");

		http.csrf().disable();

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
	}
}