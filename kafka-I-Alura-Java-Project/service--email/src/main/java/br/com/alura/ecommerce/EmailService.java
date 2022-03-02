package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;


public class EmailService  {
    public static void main(String[] args) {
        var emailService = new EmailService();
        var kafkaService = new KafkaService<>(EmailService.class.getSimpleName(),"ECOMMERCE_SEND_EMAIL",emailService::parse, Email.class,  Map.of());
        kafkaService.run();

    }

    private void  parse(ConsumerRecord<String,Email> record){
        System.out.println("\n");
        System.out.println("Log");
        System.out.println("Chave: " + record.key());
        System.out.println("Valor: " + record.value());
        System.out.println("Tópico: " + record.topic());
        System.out.println("offset: " + record.offset());
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }


}
