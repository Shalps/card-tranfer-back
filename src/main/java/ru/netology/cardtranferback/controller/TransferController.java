package ru.netology.cardtranferback.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cardtranferback.model.DTO.ConfirmRequest;
import ru.netology.cardtranferback.model.DTO.ErrorResponse;
import ru.netology.cardtranferback.model.DTO.OperationResponse;
import ru.netology.cardtranferback.model.DTO.TransferRequest;
import ru.netology.cardtranferback.service.TransferService;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@AllArgsConstructor
public class TransferController {

    private TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<?> initTransfer(@RequestBody @Validated TransferRequest req) {
        log.info("Transfer initiated: from={}, to={}, amount={}",
                req.getCardFromNumber(),
                req.getCardToNumber(),
                req.getAmount().getValue());

        try {
            String operationId = transferService.initTransfer(req);
            return ResponseEntity.ok(new OperationResponse(operationId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }

    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<?> confirmMoney(@RequestBody @Validated ConfirmRequest request) {
        try {
            transferService.confirmTransfer(request.getOperationId(), request.getCode());
            return ResponseEntity.ok(new OperationResponse(request.getOperationId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }

    }
}

