package xeonex.xeonex.infra.security;

import xeonex.xeonex.domain.User.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;
import java.util.Set;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private PermissionService permissionService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET,"/static/**").permitAll();
                    Map<String, Map<HttpMethod, Set<UserRole>>> urlPermissions = permissionService.getUrlPermissions();

                    urlPermissions.forEach((url, methods) -> {
                        methods.forEach((method, roles) -> {
                            if (roles == null) {
                                authorize.requestMatchers(method, url).permitAll();
                            } else {
                                authorize.requestMatchers(method, url).hasAnyRole(roles.stream().map(UserRole::name).toArray(String[]::new));
                            }
                        });
                    });
                    /*
                    authorize.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
                    authorize.requestMatchers(HttpMethod.POST,"/auth/register").permitAll();// no futuro só permitir para ADMIN
                    authorize.requestMatchers(HttpMethod.POST ,"/product").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.GET ,"/product/{id}").hasRole("PSICO");
                    */
                    authorize.anyRequest().permitAll();
                }

                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
