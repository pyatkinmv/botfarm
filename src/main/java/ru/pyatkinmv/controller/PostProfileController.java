package ru.pyatkinmv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pyatkinmv.service.PostProfileService;
import ru.pyatkinmv.service.PostProfileService.ProfileDto;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PostProfileController {
    private final PostProfileService postProfileService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void post(@RequestBody ProfileDto profile) {
        postProfileService.post(profile);
    }
}
