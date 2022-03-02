package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.regex.Pattern;

public class LogService {
    public static void main(String[] args) {
        var logService = new LogService();
        try (var consumer = new KafkaService<String>(LogService.class.getSimpleName(), Pattern.compile("ECOMMERCE.*"), logService::parse,String.class, Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName()))){
            consumer.run();

        }



    }

    public void parse(ConsumerRecord<String,String> record) {
        System.out.println("\n");
        System.out.println("LOG: ");
        System.out.println("Tópico: " + record.topic());
        System.out.println("Chave: " + record.key());
        System.out.println("Valor: " + record.value());
        System.out.println("offset: " + record.offset());

    }
}
