package com.iot_edge.managementconsole.service.system;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.IoTGatewayRequestDTO;
import com.iot_edge.managementconsole.dto.system.IoTGatewayDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.IoTGateway;
import com.iot_edge.managementconsole.mapper.IoTGatewayMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.IoTGatewayRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class IoTGatewayService {

    private final IoTGatewayRepository ioTGatewayRepository;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public IoTGatewayService(IoTGatewayRepository ioTGatewayRepository, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.ioTGatewayRepository = ioTGatewayRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(IoTGatewayRequestDTO ioTGatewayRequestDTO){
        try{
            IoTGateway ioTGateway =  IoTGateway.builder()
                    .brokerUrl(ioTGatewayRequestDTO.getBrokerUrl())
                    .clientId(ioTGatewayRequestDTO.getClientId())
                    .password(ioTGatewayRequestDTO.getPassword())
                    .userName(ioTGatewayRequestDTO.getUserName())
                    .status(ioTGatewayRequestDTO.getStatus())
                    .serverPort(ioTGatewayRequestDTO.getServerPort())
                    .build();
            ioTGateway = ioTGatewayRepository.save(ioTGateway);
            IoTGatewayDTO  ioTGatewayDto = IoTGatewayMapper.INSTANCE.toIoTGatewayDTO(ioTGateway);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", ioTGatewayDto));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding IoTGateway: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> list(){
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", ioTGatewayRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error fetching data: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(String iotGatewayUuid, IoTGatewayDTO ioTGatewayDto){
        try{
            Optional<IoTGateway> optionalIoTGateway = ioTGatewayRepository.findByUuid(UUID.fromString(iotGatewayUuid));
            if (optionalIoTGateway.isEmpty()) {
                throw new RuntimeException("IoTGateway Details not found with id: " + iotGatewayUuid);
            }
            IoTGateway ioTGateway = optionalIoTGateway.get();
            ioTGateway.setBrokerUrl(ioTGatewayDto.getBrokerUrl());
            ioTGateway.setClientId(ioTGatewayDto.getClientId());
            ioTGateway.setPassword(ioTGatewayDto.getPassword());
            ioTGateway.setUserName(ioTGatewayDto.getUserName());
            ioTGateway.setStatus(ioTGatewayDto.getStatus());
            ioTGateway.setServerPort(ioTGatewayDto.getServerPort());
            ioTGateway = ioTGatewayRepository.save(ioTGateway);
            IoTGatewayDTO ioTGatewayDTO = IoTGatewayMapper.INSTANCE.toIoTGatewayDTO(ioTGateway);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", ioTGatewayDTO));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(String iotGatewayUuid) {

        try {
            Optional<IoTGateway> optionalIoTGateway = ioTGatewayRepository.findByUuid(UUID.fromString(iotGatewayUuid));
            if (optionalIoTGateway.isEmpty()) {
                throw new ExpectationFailedException("Firm Not Found!");
            } else {
                IoTGateway ioTGateway = optionalIoTGateway.get();
                ioTGatewayRepository.delete(ioTGateway);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting IoTGateway: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<IoTGatewayDTO> getAllIoTGatewaysForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<IoTGateway> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("contactNo")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("website")), searchPattern),
                    cb.like(cb.lower(root.get("location")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllAssetsSorted(pageable, sort, specification);
    }

    private Page<IoTGatewayDTO> returnAllAssetsSorted(Pageable pageable, List<String> sort, Specification<IoTGateway> specification) throws BadRequestException {
        Page<IoTGateway> ioTGatewaysPage = ioTGatewayRepository.findAll(specification, sanitizeFirmPageable(pageable));

        if (sort.isEmpty()) {
            return ioTGatewaysPage.map(IoTGatewayMapper.INSTANCE::toIoTGatewayDTO);
        }

        Page<IoTGatewayDTO> ioTGatewayDTOs = ioTGatewaysPage.map(IoTGatewayMapper.INSTANCE::toIoTGatewayDTO);

        Comparator<IoTGatewayDTO> comparator = (a, b) -> 0;

        if (sort.contains("brokerUrl")) {
            comparator = Comparator.comparing(IoTGatewayDTO::getBrokerUrl, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("serverPort")) {
            comparator = Comparator.comparing(IoTGatewayDTO::getServerPort);
        } else if (sort.contains("clientId")) {
            comparator = Comparator.comparing(IoTGatewayDTO::getClientId, String.CASE_INSENSITIVE_ORDER);
        }else if (sort.contains("status")) {
            comparator = Comparator.comparing(IoTGatewayDTO::getStatus);
        }else if (sort.contains("createdDate")) {
            comparator = Comparator.comparing(IoTGatewayDTO::getCreatedDate);
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<IoTGatewayDTO> sortedIotGateways = ioTGatewayDTOs.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedIotGateways, pageable, ioTGatewayDTOs.getTotalElements());
    }

    private Pageable sanitizeFirmPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "brokerUrl", "brokerUrl",
                "serverPort", "serverPort",
                "clientId", "clientId",
                "userName", "userName",
                "status", "status",
                "createdDate", "createdDate"
        );

        List<Sort.Order> sanitizedOrders = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = propertyMapping.get(order.getProperty());
                    if (mappedProperty != null && !mappedProperty.isEmpty()) {
                        return new Sort.Order(order.getDirection(), mappedProperty);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        Sort sanitizedSort = sanitizedOrders.isEmpty() ? Sort.unsorted() : Sort.by(sanitizedOrders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sanitizedSort);
    }
}
