package testApplication.springbootmongodb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import testApplication.springbootmongodb.model.UserDTO;
import testApplication.springbootmongodb.repository.Userrepo;

import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private Userrepo userrepo;

    SecurityConfig(Userrepo userrepo){
        this.userrepo=userrepo;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        List<UserDTO> uss=userrepo.findAll();

        InMemoryUserDetailsManager x= new InMemoryUserDetailsManager();
        for(UserDTO u:uss){
            UserDetails user1= User.builder().username(u.getUsername()).password(passwordEncoder().encode(u.getPassword())).roles(u.getRole()).build();
            System.out.println(user1);
            x.createUser(user1);
        }
        System.out.println(x);
        return x;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    public void configure(HttpSecurity http) throws Exception {
         http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((auth)->{
            auth.requestMatchers("/admin/**").hasAuthority("admin");
            auth.requestMatchers("/user/**").hasRole("user");
            auth.anyRequest().authenticated();
        }).build();
    }
}
