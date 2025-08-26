package com.naveen.journalApp.Controller;

import com.naveen.journalApp.Entity.User;
import com.naveen.journalApp.Repository.UserRepository;
import com.naveen.journalApp.api.response.WeatherResponse;
import com.naveen.journalApp.service.UserService;
import com.naveen.journalApp.service.WeatherServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherServices weatherServices;

//    @GetMapping
//    public List<User> getAll(){
//        return userService.getAll();
//    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUsername(userName);
        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());
        userService.saveNewUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> greetings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherServices.getWeather("Hyderabad");
        String greeting = "";
        if(weatherResponse != null){
            greeting = " Weather Feels Like " + weatherResponse.getCurrent().getFeelslike() + "\n Temperature is: "
            + weatherResponse.getCurrent().getTemperature() + "\n description of climate is: "
            + weatherResponse.getCurrent().getWeatherDescriptions() + " \n is it day Now?: "
            +weatherResponse.getCurrent().getIsDay();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting, HttpStatus.OK);
    }
}
