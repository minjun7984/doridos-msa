package kr.doridos.reservationservice.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedMessage {

    private Long reservationId;

}
