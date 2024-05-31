package com.exercise.mycoolservice.opa;


import com.exercise.mycoolservice.opa.model.Input;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class OpaRequest {

    private Input input;
}
