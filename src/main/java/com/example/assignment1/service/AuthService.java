package com.example.assignment1.service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import com.example.assignment1.exeception.DataNotFoundExeception;
import com.example.assignment1.exeception.UserAuthrizationExeception;
import com.example.assignment1.model.User;
import com.example.assignment1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {

    @Autowired
    UserRepository userrepo;

    public BCryptPasswordEncoder PassEncoder() {
        return new BCryptPasswordEncoder();
    }
    public User getUserDetailsAuth(UUID userId) throws DataNotFoundExeception {
        Optional<User> user = userrepo.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new DataNotFoundExeception("User Not Found");
    }

    public boolean isAuthorised(UUID userId,String tokenEnc) throws DataNotFoundExeception, UserAuthrizationExeception {

        User user=getUserDetailsAuth(userId);
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);
        if(!((user.getUsername().equals(userName)) && (PassEncoder().matches(passWord,user.getPassword())))){
            throw new UserAuthrizationExeception("Forbidden to access");
        }
        return true;
    }

}
