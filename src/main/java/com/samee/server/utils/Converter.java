package com.samee.server.utils;

import com.samee.server.dto.UserDto;
import com.samee.server.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class Converter {

    private final ModelMapper modelMapper;

    @Autowired
    public Converter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User userDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto entityToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> userListToDtoList(List<User> users) {
        return users.stream()
                .map(this::entityToUserDto)
                .collect(Collectors.toList());
    }

}
