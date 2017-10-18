package io.pivotal.pal.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.TimeZone;

@SpringBootApplication
public class PalTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }

    @Bean
    TimeEntryRepository timeEntryRepository(DataSource dataSoure) {
//        return new InMemoryTimeEntryRepository();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new JdbcTimeEntryRepository(dataSoure);
    }

//    @Bean
//    public DataSource getDataSource() {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//
//        MysqlDataSource dataSource = new MysqlDataSource();
//
//        if (System.getenv("SPRING_DATASOURCE_URL") != null) {
//            dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
//        } else if (System.getenv("VCAP_SERVICES") != null) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                JsonNode jsonNode = objectMapper.readTree(System.getenv("VCAP_SERVICES"));
//                JsonNode mysql = jsonNode.get("p-mysql");
//                if (mysql.isArray()) {
//                    for (final JsonNode objNode : mysql) {
//                        if (objNode.isObject() && objNode.get("credentials") != null && objNode.get("credentials").get("jdbcUrl") != null) {
//                            dataSource.setUrl(objNode.get("credentials").get("jdbcUrl").asText());
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        return dataSource;
//    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JavaTimeModule())
                .build();
    }
}