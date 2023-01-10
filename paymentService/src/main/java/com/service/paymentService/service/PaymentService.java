package com.service.paymentService.service;

import com.service.domainPersistence.enumerate.PaymentStatusEnum;
import com.service.domainPersistence.payload.payment.PaymentHistoricalRequest;
import com.service.domainPersistence.payload.payment.PaymentRequest;
import com.service.domainPersistence.persistence.PaymentEntity;
import com.service.domainPersistence.persistence.PaymentHistoricalEntity;
import com.service.paymentService.repository.PaymentHistoricalRepository;
import com.service.paymentService.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentService implements PaymentServiceGateway{

    private final PaymentRepository paymentRepository;
    private final PaymentHistoricalRepository paymentHistoricalRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentHistoricalRepository paymentHistoricalRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentHistoricalRepository = paymentHistoricalRepository;
    }

    @Override
    public Mono<PaymentEntity> createCreditUser(PaymentRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime dateTime = LocalDateTime.now();
        return paymentRepository.save(
                PaymentEntity.builder()
                        .userId(request.getUserId())
                        .creditAmount(request.getCreditAmount())
                        .dateIndex(Integer.valueOf(formatter.format(dateTime)))
                        .creditStatus(String.valueOf(PaymentStatusEnum.CREDIT_ACTIVATE))
                        .build()
        );
    }

    @Override
    public Mono<PaymentEntity> updateCreditUser(PaymentRequest request) {
        return paymentRepository.findById(request.getPaymentId())
                .flatMap(data -> {
                    Double currentAmount = data.getCreditAmount();
                    double updatedAmount = currentAmount + request.getCreditAmount();
                    if (updatedAmount < 0) data.setCreditStatus(String.valueOf(PaymentStatusEnum.CREDIT_DEACTIVATE));
                    else data.setCreditStatus(String.valueOf(PaymentStatusEnum.CREDIT_ACTIVATE));
                    data.setCreditAmount(updatedAmount);
                    return paymentRepository.save(data);
                });
    }

    @Override
    public Mono<PaymentHistoricalEntity> createUpdateCreditFromTransaction(PaymentHistoricalRequest request) {

        return null;
    }
}