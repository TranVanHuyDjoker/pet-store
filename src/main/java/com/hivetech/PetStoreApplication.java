package com.hivetech;

import com.hivetech.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

@SpringBootApplication(scanBasePackages = "com.hivetech")
@ConfigurationPropertiesScan()
@EntityScan(basePackages = "com.hivetech.model.entity")
@EnableJpaRepositories(basePackages = "com.hivetech.repository")
@EnableScheduling
public class PetStoreApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(PetStoreApplication.class, args);
	}

	@Autowired
	private DataSource dataSource;
	private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreApplication.class);

	@Override
	public void run(String... args) throws Exception {
		DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
		LOGGER.info("[------------------------ Spring Boot Application ( RUNNING )---------------------");
		LOGGER.info(" 3/ Connect Posgresql : {} | ({})", !dataSource.getConnection().isClosed(), metaData.getURL());
		LOGGER.info("------------------------ ( FINISHED )----------------------------------------------]");
	}

}

