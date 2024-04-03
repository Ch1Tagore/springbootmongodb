package testApplication.springbootmongodb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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


    @Autowired
    private Userrepo userrepo;





    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDTO> uss = userrepo.findAll();

        InMemoryUserDetailsManager x = new InMemoryUserDetailsManager();
        for (UserDTO u : uss) {
            UserDetails user1 = User.builder().username(u.getUsername()).password(passwordEncoder().encode(u.getPassword())).authorities(u.getRole()).build();
            System.out.println(user1);
            x.createUser(user1);
        }
        System.out.println(x);
        return x;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((auth) -> {
             auth.requestMatchers(HttpMethod.POST, "/postnewtransaction").hasAuthority("admin");
             auth.requestMatchers(HttpMethod.GET, "/**").hasAnyAuthority("admin", "user");
             auth.anyRequest().authenticated();
         }).httpBasic(Customizer.withDefaults());
         return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
