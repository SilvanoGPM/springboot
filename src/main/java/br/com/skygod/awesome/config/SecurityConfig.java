package br.com.skygod.awesome.config;

import br.com.skygod.awesome.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static br.com.skygod.awesome.config.SecurityConstants.SING_UP_URL;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailService customUserDetailService;

//    @Override
//    protected void configure(HttpSecurity http) throws
//            Exception {
//        http.authorizeRequests()
//                .antMatchers("/*/protected/**").hasAnyRole("USER")
//                .antMatchers("/*/admin/**").hasAnyRole("ADMIN")
//                .and()
//                .httpBasic()
//                .and()
//                .csrf()
//                .disable();
//    }

    @Override
    protected void configure(HttpSecurity http) throws
            Exception {

        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, SING_UP_URL).permitAll()
                .antMatchers("/*/protected/**").hasAnyRole("USER")
                .antMatchers("/*/admin/**").hasAnyRole("ADMIN")
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), customUserDetailService));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws
            Exception {

        auth.userDetailsService(customUserDetailService).passwordEncoder(new BCryptPasswordEncoder());

    }

    //    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws
//            Exception {
//        auth.inMemoryAuthentication()
//                .withUser("skygod").password("skypass")
//                .roles("USER").and()
//                .withUser("admin").password("admin")
//                .roles("USER", "ADMIN");
//    }

}
