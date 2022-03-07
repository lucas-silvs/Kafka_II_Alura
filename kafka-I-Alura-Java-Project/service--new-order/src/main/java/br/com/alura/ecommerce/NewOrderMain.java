package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var dispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<Email>()){



                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(7000);
                var email = Math.random()+"@email.com";

                var order = new Order(orderId, amount, email);
                dispatcher.send("ECOMMERCE_NOVO_PEDIDO", email, order);


                var emailRequest = new Email("Teste@email.com","Obrigado, estamos processando seu pedido");
                emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailRequest);
            }
        }
    }


}
