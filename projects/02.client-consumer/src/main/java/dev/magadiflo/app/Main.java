package dev.magadiflo.app;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class Main {

    private final Logger LOG = LoggerFactory.getLogger(Main.class);
    private volatile boolean keepConsuming = true;

    public static void main(String[] args) {
        Properties kaProperties = new Properties();
        kaProperties.put("bootstrap.servers", "localhost:9092");
        kaProperties.put("group.id", "kinaction-helloworld");
        kaProperties.put("enable.auto.commit", "true");
        kaProperties.put("auto.commit.interval.ms", "1000");
        kaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        Main main = new Main();
        main.consume(kaProperties);
        Runtime.getRuntime().addShutdownHook(new Thread(main::shutdown));
    }

    public void consume(Properties kaProperties) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kaProperties)) {
            consumer.subscribe(List.of("kinaction-helloworld")); //El consumidor le dice a Kafka qué temas le interesan.
            while (this.keepConsuming) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(250));
                for (ConsumerRecord<String, String> record : records) {
                    LOG.info("kinaction-info offset: {}, kinaction-value: {}", record.offset(), record.value());
                }
            }
        }
    }

    private void shutdown() {
        this.keepConsuming = false;
    }
}
