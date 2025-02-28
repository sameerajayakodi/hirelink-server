package com.samee.server.controller;


import com.samee.server.dto.UserDto;
import com.samee.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userServiceImpl) {
        this.userService = userServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return new ResponseEntity<>(userDto.getUsername() + " user registered..!!", HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.login(userDto), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or #username == authentication.name")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        // Log the username from path variable
        System.out.println("Requested username to delete: " + username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated User Role: " + authentication.getAuthorities());

        try {
            UserDto deletedUser = userService.deleteUser(username);

            if (deletedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Return success message with deleted user details
            return ResponseEntity.ok(deletedUser.getUsername() + " User successfully deleted");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }

}