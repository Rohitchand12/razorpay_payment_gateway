package com.rohit.razorpay.payment.simulator;

import com.rohit.razorpay.common.enums.ChaosMode;
import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import com.rohit.razorpay.payment.repository.PaymentRepository;
import com.rohit.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class BankSimulator {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

//    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallBacks(){
        LocalDateTime timeBefore = LocalDateTime.now().minusSeconds(1);
        List<PaymentEntity> candidatePayments = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,timeBefore);

        log.info("Scheduler processing {} payments",candidatePayments.size());

        for(PaymentEntity payment : candidatePayments){
            simulateCallback(payment);
        }
    }

    private void simulateCallback(PaymentEntity payment) {
        //get the configuration for the payment method
        SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig = simulatorConfig.getConfig(payment.getMethod());

        //dueTime is the time after which the payment can be processed, not before that.
        LocalDateTime dueTime = dueAt(payment,methodSimulatorConfig);

        //if the current time is before the dueTime, then we can't process the payment;
        if(LocalDateTime.now().isBefore(dueTime)){
            return;
        }
        switch(simulatorConfig.getChaosMode()){
            case SUCCESS -> resolve(payment,true);
            case FAILURE -> resolve(payment,false);
            case NORMAL,SLOW ->  resolve(payment,shouldApprove(payment,methodSimulatorConfig));
        }
    }
    private void resolve(PaymentEntity payment, boolean approve){
        if(approve){
            paymentService.resolveAuthorization(payment.getId()
                    ,true
                    ,"SIM_BANK_REF"+ RandomizerUtil.randomBase64(16)
                    ,null
                    ,null
                    );
        }else{
            paymentService.resolveAuthorization(payment.getId()
                    ,false
                    ,"SIM_BANK_REF"+ RandomizerUtil.randomBase64(16)
                    ,"SIM_BANK_REJECTED"
                    ,"Sim_bank rejected the payment"
            );
        }
    }
    private boolean shouldApprove(PaymentEntity payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int numberRange = payment.getId().hashCode()%100;
        return numberRange < methodSimulatorConfig.getSuccessRate();
    }
    private LocalDateTime dueAt(PaymentEntity payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int range = methodSimulatorConfig.getMaxDelaySeconds()-methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds()+ Math.abs(payment.getId().hashCode())%(range+1);
        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds *= 2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }
}
