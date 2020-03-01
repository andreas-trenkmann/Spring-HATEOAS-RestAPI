package org.trenkmann.restsample.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author andreas trenkmann
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {


  /**
   * never use this in productive apps!
   */
  @Profile(value = "insecure")
  @Configuration
  public static class NoSecConfiguration extends WebSecurityConfigurerAdapter {

    private final CommonSecurityConfiguration commonSecurityConfiguration;

    public NoSecConfiguration(
        CommonSecurityConfiguration commonSecurityConfiguration) {
      this.commonSecurityConfiguration = commonSecurityConfiguration;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

      http.authorizeRequests()
          .antMatchers("/**").permitAll()
          .and().apply(commonSecurityConfiguration);
    }
  }

  @Order(1)
  @Profile(value = "!insecure")
  @Configuration
  public static class DefaultSecConfiguration extends WebSecurityConfigurerAdapter {

    private final CommonSecurityConfiguration commonSecurityConfiguration;
    private PasswordEncoder passwortEncoder = new BCryptPasswordEncoder();

    public DefaultSecConfiguration(
        CommonSecurityConfiguration commonSecurityConfiguration) {
      this.commonSecurityConfiguration = commonSecurityConfiguration;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .antMatchers("/", "/actuator/info", "/oas/**", "/webjars/**", "/v3/api-docs/**")
          .permitAll()
          .anyRequest()
          .authenticated()
          .and()
          .httpBasic()
          .and().apply(commonSecurityConfiguration);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.inMemoryAuthentication().passwordEncoder(passwortEncoder)
          .withUser("user1")
          .password(passwortEncoder.encode("password"))
          .roles("USER")
          .and()
          .withUser("admin")
          .password(passwortEncoder.encode("password"))
          .roles("ADMIN");
    }
  }

  /**
   * This bean is necessary to autowire in static classes
   */
  @Bean
  public CommonSecurityConfiguration commonSecurityConfiguration() {
    return new CommonSecurityConfiguration();
  }


  public class CommonSecurityConfiguration extends
      AbstractHttpConfigurer<CommonSecurityConfiguration, HttpSecurity> {

    /**
     * This init is valid to any config which takes part at application start
     */
    @Override
    public void init(HttpSecurity http) throws Exception {
      // enable cors and csrf
      http.cors()
          .and().csrf().disable();
    }

    @Override
    public void configure(HttpSecurity http) {
      //for general configuration tasks
    }
  }

}
