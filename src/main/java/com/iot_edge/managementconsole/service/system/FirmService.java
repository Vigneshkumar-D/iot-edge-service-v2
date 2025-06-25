package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.dto.system.FirmDto;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.FirmRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FirmService {

    private final FirmRepository firmRepository;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public FirmService(FirmRepository firmRepository, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.firmRepository = firmRepository;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(FirmDto firmDto){
        try{
            Firm existingAsset = firmRepository.findByName(firmDto.getName());
            if(existingAsset ==null){
                Firm firm = new Firm();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                    return conditions.getSource() != null;
                });
                modelMapper.map(firmDto, firm);

                return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmRepository.save(firm)));
            }
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", existingAsset));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding Firm: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> list(){
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error fetching data: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(Integer firmId, FirmDto firmDto){
        try{
            Optional<Firm> firmOptional = firmRepository.findById(firmId);
            if (firmOptional.isEmpty()) {
                throw new RuntimeException("Firm Details not found with id: " + firmId);
            }
            Firm firm = firmOptional.get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(firmDto, firm);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmRepository.save(firm)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Integer firmId) {
        try {
            if (!firmRepository.existsById(firmId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "Firm not found", 404));
            }
            firmRepository.deleteById(firmId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting firm: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }
}
