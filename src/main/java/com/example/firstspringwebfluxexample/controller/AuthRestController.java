package com.example.firstspringwebfluxexample.controller;

import com.example.firstspringwebfluxexample.dto.AuthRequestDto;
import com.example.firstspringwebfluxexample.dto.AuthResponseDto;
import com.example.firstspringwebfluxexample.service.UserService;
import com.example.firstspringwebfluxexample.security.JwtUtil;
import com.example.firstspringwebfluxexample.security.MyPasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AuthRestController {

    private final JwtUtil jwtUtil;
    private final MyPasswordEncoder passwordEncoder;
    private final UserService userService;


    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDto>> login(@RequestBody AuthRequestDto ar) {
        return userService.findByUsername(ar.getUsername())
                .filter(userDetails -> passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword()))
                .map(userDetails -> ResponseEntity.ok(new AuthResponseDto(jwtUtil.generateToken(userDetails))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
