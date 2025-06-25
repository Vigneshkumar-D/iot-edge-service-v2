package com.iot_edge.managementconsole.service;//package com.iotedge.service;
//
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RedisSessionService {
//
//    private final StringRedisTemplate redisTemplate;
//
//    public RedisSessionService(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public void saveUserSession(String userId, String sessionId) {
//        redisTemplate.opsForValue().set("wa_session:" + userId, sessionId);
//    }
//
//    public void removeUserSession(String userId, String sessionId) {
//        redisTemplate.opsForSet().remove("ws_session:" + userId, sessionId);
//    }
//
//    public String getSessionId(String userId) {
//        return redisTemplate.opsForValue().get("session:" + userId);
//    }
//}
