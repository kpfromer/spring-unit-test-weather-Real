package com.teamtreehouse.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
//builds requests, acts like a fake user/headless browser
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//used to get results
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class WeatherControllerTest {
    //Creates a fake mvc to not have to not include all bean functionality and setup info
    private MockMvc mockMvc;
    private WeatherController controller;

    @Before
    public void setup(){
        controller = new WeatherController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    //This method checks if the "/" page really maps to the "weather/detail" page, not some other page!
    @Test
    public void home_ShowRenderDetailView() throws Exception {
        mockMvc.perform(get("/"))//preforms a get request
                .andExpect(view().name("weather/detail"));//checks if the view we got is the weather/detail page
    }

    //This method checks if the search function with zip code 60657 really redirects to the page "/search/60657"
    @Test
    public void search_ShouldRedirectWithPathParam() throws Exception {
        mockMvc.perform(get("/search").param("q","60657"))//get the page /search with parameter q with value of 60657 (a zip code in this case)
                .andExpect(redirectedUrl("/search/60657"));
    }
}
