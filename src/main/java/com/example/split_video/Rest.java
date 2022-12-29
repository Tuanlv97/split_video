package com.example.split_video;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/splitVideo")
public class Rest {

    private final Service service;

    public Rest(Service service) {
        this.service = service;
    }

    @PostMapping
    public void splitVideo(@RequestBody VideoRequest videoRequest){
        service.split(videoRequest.getVideoList());
    }
}
