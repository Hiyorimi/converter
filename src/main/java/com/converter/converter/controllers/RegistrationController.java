package com.converter.converter.controllers;


import com.converter.converter.model.Role;
import com.converter.converter.model.User;
import com.converter.converter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String getRegistration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String postRegistration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordConfirm, Model model) {
        User userFromDb = userRepository.findByUsername(username);
        if (userFromDb != null) {
            model.addAttribute("message", "Пользователь с таким именем уже существует");
            model.addAttribute("username", username);
            return "registration";
        } else {
            if (!password.equals(passwordConfirm)) {
                model.addAttribute("username", username);
                model.addAttribute("doesnotmatch", "Пароли не совпадают!");
                return "registration";
            }
            User n = new User(username, password);
            n.setActive(true);
            n.setRoles(Collections.singleton(Role.USER));
            userRepository.save(n);


        }
        return "redirect:/login";
    }
}
