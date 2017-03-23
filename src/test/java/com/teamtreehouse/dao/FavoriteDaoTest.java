package com.teamtreehouse.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.teamtreehouse.Application;
import com.teamtreehouse.domain.Favorite;
import com.teamtreehouse.domain.User;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.Matchers.*;
//import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.*;
import static com.teamtreehouse.domain.Favorite.FavoriteBuilder;

/**
 * Created by kpfromer on 3/23/17.
 */

/*
* When testing the data (DAO) layer of our application,
* it's helpful to have some assistance in starting our tests with some test data,
* and even verifying our resulting database after a test executes.
* Our assistance comes from a library called DBUnit
*/


//We need to create a application context (running the hole app) in order to make the test work
@SpringApplicationConfiguration(Application.class)//Application.class is the class we need to run to start the application context
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:favorites.xml")//allows for getting the test data (Database entries)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})//defines a listener api for reacts to test execution events
public class FavoriteDaoTest {
    @Autowired
    private FavoriteDao dao;

    @Before
    public void setup(){
        User user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    public void findAll_ShouldReturnTwo() throws Exception {
        assertThat(dao.findAll(), hasSize(2));
    }

    @Test
    public void save_ShouldPersistEntity() throws Exception {
        String placeId = "kpfromerDasdaaASFMBCLP";
        Favorite fave = new FavoriteBuilder()
                .withPlaceId(placeId)
                .build();
        dao.saveForCurrentUser(fave);
        assertThat(dao.findByPlaceId(placeId), notNullValue(Favorite.class));
    }

}