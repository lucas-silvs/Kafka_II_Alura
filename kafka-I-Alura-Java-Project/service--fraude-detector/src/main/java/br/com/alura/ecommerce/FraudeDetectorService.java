package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudeDetectorService {

    private  final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var fraudeDetectorService = new FraudeDetectorService();
        var kafkaService = new KafkaService<Order>(FraudeDetectorService.class.getSimpleName(), "ECOMMERCE_NOVO_PEDIDO", fraudeDetectorService::parse, Order.class, Map.of());
        kafkaService.run();
    }


    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("\n");
        System.out.println("processando pedidos, necessário criar validação de fraude");
        System.out.println("Chave: " + record.key());
        System.out.println("Valor: " + record.value());
        System.out.println("Tópico: " + record.topic());
        System.out.println("offset: " + record.offset());


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        var order = record.value();
        if (isFraud(order)) {
            //simulando que uma fraude aconteceu com um amount >= 4500
            System.out.println("Pedido é uma fraude !!!!"+ order);
            orderDispatcher.send("ECOMMERCE_PEDIDO_REJEITADO",order.getEmail(),order);
        } else {
            System.out.println("Pedido aprovado"+ order);
            orderDispatcher.send("ECOMMERCE_PEDIDO_APROVADO",order.getEmail(),order);
        }


    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }


}
