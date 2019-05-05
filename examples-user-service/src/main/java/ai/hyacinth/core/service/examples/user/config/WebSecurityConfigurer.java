package ai.hyacinth.core.service.examples.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //    http.authorizeRequests()
    //        .antMatchers("/hello*").access("hasRole('ROLE_ADMIN')").and().formLogin();;
    http.authorizeRequests()
        .anyRequest()
        .authenticated()
        .anyRequest()
        .anonymous()
        .and()
        .headers()
        .httpStrictTransportSecurity()
        .disable()
        .and()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .logout()
        .disable()
        .csrf()
        .disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    super.configure(auth);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  // refer to SecurityContextPersistenceFilter for session persistent
}
