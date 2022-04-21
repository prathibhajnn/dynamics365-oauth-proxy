/**
 * 
 */
package group.youwinn.config;

/**
 * @author ADMIN
 *
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Value( "${security_username}" )
	private String userName;
	
	@Value( "${security_password}" )
	private String password;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http.cors().and()
         .csrf().disable()
         .authorizeRequests().anyRequest().authenticated()
         .and()
         .httpBasic();
    }
  
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) 
            throws Exception 
    {
        auth.inMemoryAuthentication()
            .withUser(userName)
            .password("{noop}"+password)
            .roles("ADMIN");
    }
    
  }
