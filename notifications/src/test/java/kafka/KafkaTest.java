package kafka;

import com.notifications.dto.NotificationDto;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = {"notification"})
public class KafkaTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testNotification() {
        NotificationDto notificationDto = new NotificationDto("login", "message");
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("notification-group", "true", embeddedKafkaBroker),
                new StringDeserializer(),
                new JsonDeserializer<>(NotificationDto.class, false)
        ).createConsumer()) {
            consumerForTest.subscribe(List.of("notification"));

            kafkaTemplate.send("notification", notificationDto.getLogin(), notificationDto);

            var inputMessage = KafkaTestUtils.getSingleRecord(consumerForTest, "notification", Duration.ofSeconds(5));
            assertThat(inputMessage.key()).isEqualTo(notificationDto.getLogin());
            assertThat(inputMessage.value()).isEqualTo(notificationDto);
        }
    }

    @TestConfiguration
    public static class KafkaTestConfig {
        @Bean
        public KafkaTemplate<String, NotificationDto> kafkaTemplate(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
            producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
            producerProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
        }
    }

}