package com.teamtreehouse.service;

import com.teamtreehouse.config.AppConfig;
import com.teamtreehouse.service.dto.geocoding.Location;
import com.teamtreehouse.service.dto.weather.Weather;
import com.teamtreehouse.service.resttemplate.weather.WeatherServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

import static com.teamtreehouse.config.AppConfig.defaultRestTemplate;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * Created by kpfromer on 3/22/17.
 */

/*
* Wherever possible, we want to leave Spring out of our tests and focus completely on the "unit" under test.
* However, sometimes it's necessary to access Spring components, such as services, repositories, or other beans.
* For this reason, we can setup a minimal test ApplicationContext to save us the overhead of starting an entire Spring ApplicationContext.
* This video demonstrates one approach for setting up a test context.
*
* Basically we create the stuff we need just for the test
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class WeatherServiceTest {

    //this is that service from below
    @Autowired
    private WeatherService service;

    private Location loc;
    private Weather weather;

    //When matching if the lat, and long are the same as the return, the api might give us a slightly different lat and long
    //this is for the error margin that we will accept.
    private static final double ERROR_GEO = 0.0000001;
    //This is in milliseconds
    private static final double ERROR_TIME = 5000;//5 seconds

    @Before
    public void setup(){
        loc = new Location(41.9403795, -87.65318049999999);
        weather = service.findByLocation(loc);
    }

    @Test
    public void findByLocation_ShouldReturnSameCoords() throws Exception {
        assertThat(weather.getLatitude(), closeTo(loc.getLatitude(), ERROR_GEO));
        assertThat(weather.getLongitude(), closeTo(loc.getLongitude(), ERROR_GEO));
    }

    @Test
    public void findByLocation_ShouldReturn8DaysForecastData() throws Exception {
        assertThat(weather.getDaily().getData(), hasSize(8));
    }

    @Test
    public void findByLocation_ShouldReturnCurrentConditions() throws Exception {
        Instant now = Instant.now();
        double duration = Duration.between(now, weather.getCurrently().getTime()).toMillis();
        assertThat(duration, closeTo(0, ERROR_TIME));
    }




    //this for loading in the weather api from darksky
    //also it loads the properties file that we need to use for the correct settings
    @Configuration
    @PropertySource("api.properties")
    public static class TestConfig {
        @Autowired
        private Environment env;

        @Bean
        public RestTemplate RestTemplate() {
            return defaultRestTemplate();
        }

        @Bean
        public WeatherService weatherService(){
            WeatherService service = new WeatherServiceImpl(
                    env.getProperty("weather.api.name"),
                    env.getProperty("weather.api.key"),
                    env.getProperty("weather.api.host")
            );
            return service;
        }
    }

}