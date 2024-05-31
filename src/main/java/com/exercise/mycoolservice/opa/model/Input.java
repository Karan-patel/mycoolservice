package com.exercise.mycoolservice.opa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Input {

    private String method;
    private User user;
}
