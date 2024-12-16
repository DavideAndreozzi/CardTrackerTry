package com.andreozzi.config;

import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.andreozzi.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity  // Abilita la sicurezza web
@EnableScheduling    // Abilita la pianificazione
public class DefaultSecurityConfig {
	
	private final CustomUserDetailsService customUserDetailsService;

	// Costruttore per l'iniezione del servizio personalizzato per i dettagli utente
	public DefaultSecurityConfig(CustomUserDetailsService customUserDetailsService) {
	    this.customUserDetailsService = customUserDetailsService;
	}

    // Configurazione della catena di filtri di sicurezza
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Definizione delle regole di accesso per le diverse URL
                .requestMatchers("/css/**", "/js/**", "/assets/**", "/assets/ico", "/assets/img").permitAll()  
                .requestMatchers("/login", "/register").permitAll()  
                .anyRequest().authenticated()  
            )
            .formLogin(form -> form
                .loginPage("/login")  
                .defaultSuccessUrl("/index", true) 
                .permitAll()  
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  
                .logoutSuccessUrl("/login?logout=true") 
                .permitAll() 
            )
            .csrf(csrf -> csrf.disable()); // Disabilita la protezione CSRF
        return http.build();  // Costruisce e restituisce la catena di filtri di sicurezza
    }

    // Bean per l'encoding della password usando BCrypt
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Crea un encoder per password
    }

    // Configurazione dell'AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)  // Imposta il servizio di dettagli utente
                .passwordEncoder(passwordEncoder) // Imposta l'encoder di password
                .and()
                .build(); 
    }

    // Configurazione della strategia di SecurityContextHolder
    @Bean
    MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName"); // Imposta la strategia di contesto
        methodInvokingFactoryBean.setArguments(new String[]{SecurityContextHolder.MODE_INHERITABLETHREADLOCAL}); // Usa thread-local ereditabile
        return methodInvokingFactoryBean; // Restituisce il bean configurato
    }
}
