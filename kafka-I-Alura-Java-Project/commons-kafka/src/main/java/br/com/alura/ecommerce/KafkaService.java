package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

     KafkaService(String groupId, String topico, ConsumerFunction parse, Class<T> typeClass, Map<String,String> mapProperties) {
         this(groupId,parse,typeClass, mapProperties);
        this.consumer.subscribe(Collections.singletonList(topico));
    }

    public KafkaService(String groupId, Pattern topico, ConsumerFunction parse, Class<T> typeClass, Map<String,String> mapProperties) {
        this(groupId,parse,typeClass, mapProperties);
        this.consumer.subscribe(topico);

    }

    private KafkaService(String groupId, ConsumerFunction parse, Class<T> typeClass, Map<String,String> mapProperties) {
        this.consumer = new KafkaConsumer<String, T>(properties(groupId,typeClass,mapProperties));
        this.parse = parse;

    }

    void run() {
         try {


             while (true) {
                 var records = consumer.poll(Duration.ofSeconds(1));
                 if (!records.isEmpty()) {
                     for (ConsumerRecord<String, T> record : records) {
                         parse.consume(record);
                     }
                 }
                 System.out.println("Registro vazio");
             }
         }catch (Exception e){
             e.printStackTrace();
             close();
         }



    }
    private Properties properties(String groupId, Class<T> typeClass, Map<String, String> overrideMapProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, typeClass.getName());
        properties.putAll(overrideMapProperties);


        return properties;
    }

    @Override
    public void close() {
         consumer.close();
    }
}
