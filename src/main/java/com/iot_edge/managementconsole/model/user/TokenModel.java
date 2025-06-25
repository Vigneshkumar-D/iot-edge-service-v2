package com.iot_edge.managementconsole.model.user;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenModel {
    private String username;
    private String password;
}
