package com.iot_edge.managementconsole.service.user;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.RoleRequestDTO;
import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.entity.system.Location;
import com.iot_edge.managementconsole.entity.user.Role;
import com.iot_edge.managementconsole.mapper.FirmMapper;
import com.iot_edge.managementconsole.mapper.LocationMapper;
import com.iot_edge.managementconsole.mapper.RoleMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;


@Service
public class RoleService {


    private final RoleRepository roleRepository;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public RoleService(RoleRepository roleRepository, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.roleRepository = roleRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> getAllRoles() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> createRole(RoleRequestDTO roleRequestDTO) {
        try{
            Role role = Role.builder()
                    .roleName(roleRequestDTO.getRoleName())
                    .active(roleRequestDTO.getActive())
                    .firm(FirmMapper.INSTANCE.toFirm(roleRequestDTO.getFirm()))
                    .build();
            role = roleRepository.save(role);
            RoleDTO roleDTO = RoleMapper.INSTANCE.toRoleDTO(role);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleDTO));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> getRoleByUuid(String roleUuid) {
        try{
            Optional<Role> roleOptional = roleRepository.findByUuid(UUID.fromString(roleUuid));
            return roleOptional.<ResponseEntity<ResponseModel<?>>>map(role -> ResponseEntity.ok(new ResponseModel<>(true, "Success", RoleMapper.INSTANCE.toRoleDTO(role)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(false, "Role details not found")));

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> updateRole(String roleUuid, RoleDTO roleDTO) {
        try{
            Role role = roleRepository.findByUuid(UUID.fromString(roleUuid)).orElse(null);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel<>(false, "Role details not found!"));
            }
            role.setRoleName(roleDTO.getRoleName());
            role.setFirm(FirmMapper.INSTANCE.toFirm(roleDTO.getFirm()));
            role.setActive(roleDTO.getActive());
            role = roleRepository.save(role);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", RoleMapper.INSTANCE.toRoleDTO(role)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> deleteRole(String roleUuid) {
        try {
            Optional<Role> optionalRole = roleRepository.findByUuid(UUID.fromString(roleUuid));
            if (optionalRole.isEmpty()) {
                throw new ExpectationFailedException("Role Not Found!");
            } else {
                Role role = optionalRole.get();
                roleRepository.delete(role);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting role: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public Role getSystem(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("System");
        return optionalRole.orElse(null);
    }

    public Role getSuperUser(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("Super User");
        return optionalRole.orElse(null);
    }

    public Role getGuest(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("Guest");
        return optionalRole.orElse(null);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<RoleDTO> getAllRolesForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<Role> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("locationName")), searchPattern),
                    cb.like(cb.lower(root.get("category")), searchPattern),
                    cb.like(cb.lower(root.get("latitude")), searchPattern),
                    cb.like(cb.lower(root.get("longitude")), searchPattern),
                    cb.like(cb.lower(root.get("createdDate")), searchPattern),
                    cb.like(cb.lower(root.get("parent")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllRolesSorted(pageable, sort, specification);
    }

    private Page<RoleDTO> returnAllRolesSorted(Pageable pageable, List<String> sort, Specification<Role> specification) throws BadRequestException {
        Page<Role> rolePage = roleRepository.findAll(specification, sanitizeFirmPageable(pageable));

        if (sort.isEmpty()) {
            return rolePage.map(RoleMapper.INSTANCE::toRoleDTO);
        }

        Page<RoleDTO> roleDTOS = rolePage.map(RoleMapper.INSTANCE::toRoleDTO);

        Comparator<RoleDTO> comparator = (a, b) -> 0;

        if (sort.contains("rolename")) {
            comparator = Comparator.comparing(RoleDTO::getRoleName, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("firm")) {
            comparator = Comparator.comparing(
                    RoleDTO::getFirm,
                    (loc1, loc2) -> {
                        if (loc1 == null && loc2 == null) return 0;
                        if (loc1 == null) return -1;
                        if (loc2 == null) return 1;
                        return loc1.getName().compareTo(loc2.getName());
                    }
            );
        } else if (sort.contains("category")) {
            comparator = Comparator.comparing(RoleDTO::getActive);
        }else if (sort.contains("createdDate")) {
            comparator = Comparator.comparing(RoleDTO::getCreatedDate);
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<RoleDTO> sortedRoles = roleDTOS.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedRoles, pageable, roleDTOS.getTotalElements());
    }

    private Pageable sanitizeFirmPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "rolename", "rolename",
                "firm", "firm",
                "category", "category",
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
