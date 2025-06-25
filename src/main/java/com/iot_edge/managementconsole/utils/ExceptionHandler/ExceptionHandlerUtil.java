package com.iot_edge.managementconsole.utils.ExceptionHandler;

import org.springframework.stereotype.Component;

@Component
public class ExceptionHandlerUtil {

    public String sanitizeErrorMessage(String errorMessage) {
        int dbDetailsIndex = errorMessage.indexOf("Detail");
        if (dbDetailsIndex != -1) {
            return errorMessage.substring(dbDetailsIndex);
        }

        return errorMessage;
    }
}
