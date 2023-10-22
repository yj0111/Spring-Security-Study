package io.security.oauth2.springsecurityoauth2;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomSecurityConfiguer extends AbstractHttpConfigurer<CustomSecurityConfiguer, HttpSecurity> {
    private boolean isSecure;

    @Override
    public void init(HttpSecurity builder) throws Exception{
        super.init(builder);
        System.out.println("init method started");
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception{
        super.init(builder);
        System.out.println("init method started");
        if(isSecure){
            System.out.println("http is required");
        }else{
            System.out.println("http is optional");
        }
    }

    public CustomSecurityConfiguer setFlag(boolean isSecure){
        this.isSecure = isSecure;
        return this;
    }
}
