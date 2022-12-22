package br.com.eboli.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "yaml")
@PropertySource(value = "classpath:exception-messages.yml", factory = PropSourceFactoryYaml.class)
public class ExceptionMessageConfig {

    private String customerControllerSaveIllegal;
    private String customerControllerSaveConflict;

}
