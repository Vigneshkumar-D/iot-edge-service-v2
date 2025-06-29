package com.iot_edge.managementconsole.dto.user;

import com.iot_edge.managementconsole.entity.user.User;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserTokenDTO {

    private UUID uuid;

    private String token;

    private Integer userId;

    private Instant expireOn;

    private Integer amount;

    private String unit;

    private User user;

    private Instant createdOn;

    private boolean active;

}
