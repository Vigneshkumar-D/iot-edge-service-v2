package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.dto.system.IoTGatewayDto;
import com.iot_edge.managementconsole.entity.system.IoTGateway;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.IoTGatewayRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IoTGatewayService {

    private final IoTGatewayRepository ioTGatewayRepository;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public IoTGatewayService(IoTGatewayRepository ioTGatewayRepository, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.ioTGatewayRepository = ioTGatewayRepository;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(IoTGatewayDto ioTGatewayDto){
        try{
            IoTGateway ioTGateway = new IoTGateway();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(ioTGatewayDto, ioTGateway);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", ioTGatewayRepository.save(ioTGateway)));
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

    public ResponseEntity<ResponseModel<?>> update(Integer iotGatewayId, IoTGatewayDto ioTGatewayDto){
        try{
            Optional<IoTGateway> optionalIoTGateway = ioTGatewayRepository.findById(iotGatewayId);
            if (optionalIoTGateway.isEmpty()) {
                throw new RuntimeException("IoTGateway Details not found with id: " + iotGatewayId);
            }
            IoTGateway ioTGateway = optionalIoTGateway.get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(ioTGatewayDto, ioTGateway);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", ioTGatewayRepository.save(ioTGateway)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Integer iotGatewayId) {
        try {
            if (!ioTGatewayRepository.existsById(iotGatewayId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "Firm not found", 404));
            }
            ioTGatewayRepository.deleteById(iotGatewayId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting IoTGateway: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }
}
