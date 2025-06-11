package com.scanakispersonalprojects.dndapp.controller;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.service.CustomUserDetailsService;



@Controller
public class UserController {

    private CustomUserDetailsService detailsService;

    public UserController(CustomUserDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    @GetMapping("/characters/")
    @ResponseBody
    public List<CharacterBasicInfoView> home(Authentication authentication) {
        CustomUserPrincipal user = (CustomUserPrincipal) detailsService.loadUserByUsername(authentication.getName());
        return user.getCharacters();
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "Hello Admin";
    }
    
        

}
