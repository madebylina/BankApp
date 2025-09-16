package com.controller;

import com.service.BlockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/validate")
public class BlockerController {

    private final BlockerService blockerService;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_BLOCKER')")
    public Boolean validate() throws AccountNotFoundException {
        return blockerService.validate();
    }
}
