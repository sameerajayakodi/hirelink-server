package com.samee.server.service;


import com.samee.server.dto.UserDto;

import java.util.List;

public interface UserService {
    void registerUser(UserDto userDto);
    String login(UserDto userDto);
    List<UserDto> getAllUsers();
    UserDto deleteUser(String username);
}
