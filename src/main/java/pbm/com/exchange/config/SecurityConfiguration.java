package pbm.com.exchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import pbm.com.exchange.security.*;
import pbm.com.exchange.security.jwt.*;
import tech.jhipster.config.JHipsterProperties;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JHipsterProperties jHipsterProperties;

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;
    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(
        TokenProvider tokenProvider,
        CorsFilter corsFilter,
        JHipsterProperties jHipsterProperties,
        SecurityProblemSupport problemSupport
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
            .ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/test/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
            .disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
        .and()
            .headers()
            .contentSecurityPolicy(jHipsterProperties.getSecurity().getContentSecurityPolicy())
        .and()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
            .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
        .and()
            .frameOptions()
            .deny()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("v3/api-docs/springdocDefault").permitAll()
            .antMatchers(HttpMethod.PUT,"/api/app/auth/change-password").authenticated()
            .antMatchers(HttpMethod.POST,"/api/app/auth/check-otp-change-password").authenticated()
            .antMatchers("/api/app/auth/**").permitAll()
            .antMatchers("/api/app/categories").permitAll()
            .antMatchers(HttpMethod.DELETE, "/api/app/products/{id}").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/products").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/products/my-items").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/products/{id}").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/products/{photo}").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/products/similar/{id}").permitAll()
            .antMatchers(HttpMethod.POST, "/api/app/products").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/app/profiles").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app//profiles/code").authenticated()
            .antMatchers(HttpMethod.POST, "/api/app/profiles/uploadAvatar").authenticated()
            .antMatchers(HttpMethod.POST, "/api/app/favorites").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/locations").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/states").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/exchanges/my-exchanges").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/exchanges/myExchangeByProductId").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/filter-products").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/web-filter-products").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/states/*").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/cities**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/app/send-mail").permitAll()
            .antMatchers(HttpMethod.POST, "/api/app/add-token").authenticated()
            .antMatchers(HttpMethod.POST, "/api/app/purchases").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/purchases-user").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/profiles/uid").permitAll()
            .antMatchers(HttpMethod.GET,"/api/app/categories").permitAll()
            .antMatchers(HttpMethod.GET, "/api/app/my-wishlist").authenticated()
            .antMatchers(HttpMethod.POST, "/api/app/favorite").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/auctions").permitAll()
            .antMatchers(HttpMethod.POST, "/api/app/auctions").authenticated()
            .antMatchers(HttpMethod.GET, "/api/app/image/download/*").permitAll()
            
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
            
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/health/**").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            
            //application api
            .antMatchers("/api/app/auth/*").permitAll()
            
            .antMatchers("/api/app**").permitAll()
            .antMatchers("/api/**").authenticated()
        .and()
            .httpBasic()
        .and()
            .apply(securityConfigurerAdapter());
        // @formatter:on
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
