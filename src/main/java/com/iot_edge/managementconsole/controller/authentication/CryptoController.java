package com.iot_edge.managementconsole.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.managementconsole.dto.request.CryptoRequestDTO;
import com.iot_edge.managementconsole.service.authentication.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crypto")
@Slf4j
public class CryptoController {
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public CryptoController(CryptoService cryptoService, @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.cryptoService = cryptoService;
        this.objectMapper = objectMapper;
    }
    @PostMapping(value = "/encrypt", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> encrypt(@RequestBody CryptoRequestDTO cryptoRequestDTO) throws Exception {
        log.info("Encrypting data {}", cryptoRequestDTO);
        String output = cryptoService.encrypt(cryptoRequestDTO.getValue());
        return ResponseEntity.ok(objectMapper.writeValueAsString(output));
    }

    @PostMapping(value = "/decrypt", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> decrypt(@RequestBody CryptoRequestDTO cryptoRequestDTO) throws Exception {
        log.info("Decrypting data {}", cryptoRequestDTO);
        String output = cryptoService.decrypt(cryptoRequestDTO.getValue());
        return ResponseEntity.ok(objectMapper.writeValueAsString(output));
    }
}

