package com.teamtreehouse.web.controller;

import com.teamtreehouse.domain.Favorite;
import com.teamtreehouse.service.FavoriteNotFoundException;
import com.teamtreehouse.service.FavoriteService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static com.teamtreehouse.domain.Favorite.FavoriteBuilder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
//builds requests, acts like a fake user/headless browser
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//used to get results
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by kpfromer on 3/22/17.
 */
//Use mockito
@RunWith(MockitoJUnitRunner.class)
public class FavoriteControllerTest {

    private MockMvc mockMvc;

    // Creates an instance of the FavoriteController using the default constructor of the FavoriteController,
    // then inject any fields with the mock annotation with the mock annotation into the controller
    @InjectMocks
    private FavoriteController controller;

    @Mock
    private FavoriteService service;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void index_ShouldIncludeFavoritesInModel() throws Exception {
        // Arrange the mock behavior

        // Create a list of favorites to send back to the controller
        List<Favorite> favorites = Arrays.asList(
                new FavoriteBuilder(1L).withAddress("Chicago").withPlaceId("chicago1").build(),
                new FavoriteBuilder(2L).withAddress("Omaha").withPlaceId("omaha1").build()
        );

        /*
        * When the controller calls the favoriteService.findAll()
        * we return the values of the favorites list; "Mocking the functionality"
        *
        * The when function is part the import static org.mockito.Mockito.*;
        */
        when(service.findAll()).thenReturn(favorites);

        // Act (perform the MVC request) and Assert results
        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())// 200 status code, no errors
                .andExpect(view().name("favorite/index"))//went to the right place
                .andExpect(model().attribute("favorites", favorites)); // has the correct favorites

        //We want to make sure that the method really called the service.findAll(), and not did something else!
        verify(service).findAll();
    }

    @Test
    public void add_ShouldRedirectToNewFavorite() throws  Exception {
        // Arrange the mock behavior

        //Mocking the save form the Favorite Service
        doAnswer(invocation -> {
            //gets the Favorite object passed to the favoriteService.save(favorite)
            Favorite f = (Favorite)invocation.getArguments()[0];
            //We have to set a id, since hibernate usually does that
            f.setId(2L);
            //We have to return null because mockito requires it, but the function we are mocking doesn't
            return null;
        }).when(service).save(any(Favorite.class)); // do the code above when the favoriteService.save(favorite) is run in the FavoriteController

        // Act (perform the MVC request) and Assert results

        mockMvc.perform(
                post("/favorites")
                        .param("formattedAddress", "chicago, il")//fakes the form information
                        .param("placeId", "windycity")
        ).andExpect(redirectedUrl("/favorites/2"));
        verify(service).save(any(Favorite.class));
    }

    @Test
    public void detail_ShouldErrorOnNotFound() throws Exception {
        // Arrange the mock behavior
        when(service.findById(1L)).thenThrow(FavoriteNotFoundException.class);

        // Act (perform the MVC request) and Assert results
        mockMvc.perform(get("/favorites/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attribute("ex", Matchers.instanceOf(FavoriteNotFoundException.class)));
        verify(service).findById(1L);
    }

}