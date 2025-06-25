package com.iot_edge.managementconsole.model.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Parameters implements Serializable {
    private String parameterName;
    private String displayName;
    private Object parameterType;
    private String value;
    private Instant updatedOn;
}
