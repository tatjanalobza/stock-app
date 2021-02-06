package stockapp.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("nl.tatjana.stockapp")
@PropertySource("classpath:application.yml")
public class StockConfig {
}
