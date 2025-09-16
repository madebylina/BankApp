package com.blocker.service;

import org.springframework.stereotype.Service;

@Service
public class BlockerService {

    private Integer requestCount = 0;

    public Boolean validate() {
        requestCount++;
        return requestCount % 4 != 0;
    }
}
