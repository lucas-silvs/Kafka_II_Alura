package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<String,T>(properties());
    }


    private static Properties properties(){
        var properties =new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,GsonSerializer.class.getName());
        //só é confirmado um ok caso o leader receba a mensagem, replica ela para todas as replicas, e as replicas confirmarem o recebimento da mensagem
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");

        return properties ;
    }

    public void send(String topico, String key, T value) throws ExecutionException, InterruptedException {
        try {
            var record = new ProducerRecord<>(topico, key, value);
            Callback callback = (data, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                System.out.println("Sucesso ao enviar no tópico: " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/ time " + data.timestamp());
            };
            producer.send(record, callback).get();
        }catch (Exception e){
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void close() {
        producer.close();

    }
}
