package com.example.assignment1.service;

import java.util.Optional;
import java.util.UUID;

import com.example.assignment1.exeception.DataNotFoundExeception;
import com.example.assignment1.exeception.UserAuthrizationExeception;
import com.example.assignment1.exeception.UserExistException;
import com.example.assignment1.model.User;
import com.example.assignment1.model.UserDto;
import com.example.assignment1.model.UserUpdateRequestModel;
import com.example.assignment1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    @Autowired
    UserRepository userrepo;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public String createUser(User user) throws UserExistException {
        User userDto = userrepo.findByUsername(user.getUsername());
        if (userDto == null) {
            user.setPassword(encoder().encode(user.getPassword()));
            userrepo.save(user);
            return "Created User";
        }
        throw new UserExistException("User Exists Already");
    }

    public UserDto getUserDetails(UUID userId) throws DataNotFoundExeception {
        Optional<User> user = userrepo.findById(userId);
        if (user.isPresent()) {
            UserDto dto = UserDto.getUserDto(user.get());
            return dto;
        }
        throw new DataNotFoundExeception("User Not Found");
    }

    public String updateUserDetails(UUID userId, UserUpdateRequestModel user) throws DataNotFoundExeception, UserAuthrizationExeception {
        Optional<User> userObj = userrepo.findById(userId);
        if (userObj.isPresent()) {
            if(!userObj.get().getUsername().equals(user.getUsername()))
                throw new UserAuthrizationExeception("Forbidden to Update Data");
            User dto = userObj.get();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPassword(encoder().encode(user.getPassword()));
            dto.setUsername(user.getUsername());
            userrepo.save(dto);
            return "Updated User Details Successfully";

        }
        throw new DataNotFoundExeception("User Not Found");
    }

    public User loadUserByUsername(String username) {
        // TODO Auto-generated method stub
        User user = userrepo.findByUsername(username);
        if (user == null) {
            return null;
        }
        return user;
    }

}

