package com.myorg.bugTrackerPoc.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * i18n message configuration - reading from src/main/resources/messages.properties
 */
@Configuration
public class MessageConfiguration {

    @Bean
    public MessageSource getMessageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("resources/messages");
        return messageSource;
    }

}
