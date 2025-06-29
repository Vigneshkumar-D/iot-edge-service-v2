package com.iot_edge.managementconsole.dto.user;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private UUID uuid;
    private Integer id;
    private String menuName;
    private Integer parentId;
    private UUID pageId;
    private String icon;
    private String path;
    private Integer orderNumber;
    private Boolean status;
    private String moduleName;
    private List<Integer>userAccessFeatureMappings;
}
