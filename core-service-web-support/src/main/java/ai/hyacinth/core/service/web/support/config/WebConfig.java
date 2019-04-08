package ai.hyacinth.core.service.web.support.config;

import ai.hyacinth.core.service.web.support.errorhandler.ServiceControllerExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ComponentScan(basePackageClasses = {ServiceControllerExceptionHandler.class})
public class WebConfig {
  //  @EnableWebSecurity
  //  public class WebSecurity extends WebSecurityConfigurerAdapter {
  //    private final UserService userDetailsService;
  //    private final BCryptPasswordEncoder bCryptPasswordEncoder;
  //    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder
  // bCryptPasswordEncoder) {
  //      this.userDetailsService = userDetailsService;
  //      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  //    }
  //    @Override
  //    protected void configure(HttpSecurity http) throws Exception {
  //      http.csrf().disable().
  //              authorizeRequests()
  //              .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
  //              .permitAll()
  //              .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
  //              .permitAll()
  //              .anyRequest().authenticated().and()
  //              .addFilter( new AuthenticationFilter(authenticationManager()) )
  //              .addFilter( new AuthorizationFilter( authenticationManager() ))
  //              .sessionManagement()
  //              .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  //
  //      http.headers().frameOptions().disable();
  //    }
  //    @Override
  //    public void configure(AuthenticationManagerBuilder auth) throws Exception {
  //      auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  //    }
  //  }
}
