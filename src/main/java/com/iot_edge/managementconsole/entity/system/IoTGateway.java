package com.iot_edge.managementconsole.entity.system;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "iot_gateway")
public class IoTGateway extends Auditable<String> implements Serializable {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "uuid", nullable = false, unique = true)
   private UUID uuid;

   private String brokerUrl;
   private Integer serverPort;
   private String clientId;
   private String userName;
   private String password;
   private Boolean status;

   @PrePersist
   protected void onCreate() {
      this.uuid = UUID.randomUUID();
   }
}

