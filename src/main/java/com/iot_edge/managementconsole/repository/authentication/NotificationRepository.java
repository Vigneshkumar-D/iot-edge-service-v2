package com.iot_edge.managementconsole.repository.authentication;


import com.iot_edge.managementconsole.constants.NotificationChannel;
import com.iot_edge.managementconsole.constants.NotificationStatus;
import com.iot_edge.managementconsole.constants.NotificationType;
import com.iot_edge.managementconsole.entity.authentication.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Optional<List<Notification>> findAllByChannelAndStatusAndType(NotificationChannel channel, NotificationStatus status, NotificationType type);
    Optional<List<Notification>> findAllByChannelAndStatus(NotificationChannel channel, NotificationStatus status);

}
