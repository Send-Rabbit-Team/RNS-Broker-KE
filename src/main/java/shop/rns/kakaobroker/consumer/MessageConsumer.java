package shop.rns.kakaobroker.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import shop.rns.kakaobroker.config.status.MessageStatus;
import shop.rns.kakaobroker.dto.KakaoMessageDTO;
import shop.rns.kakaobroker.dto.MessageResultDTO;
import shop.rns.kakaobroker.dto.ReceiveMessageDTO;

import java.io.IOException;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

@Component
@Log4j2
@RequiredArgsConstructor
public class MessageConsumer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues="q.kakao.ke.work", concurrency = "3", ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel){
        try{
            ReceiveMessageDTO receiveMessageDTO = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDTO.class);

            KakaoMessageDTO kakaoMessageDTO = receiveMessageDTO.getKakaoMessageDTO();
            System.out.println("메시지 내용 = " + kakaoMessageDTO.getContent());

            MessageResultDTO messageResultDTO = receiveMessageDTO.getMessageResultDTO();

            sendResponseToSendServer(messageResultDTO);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e){
            // DLX
            log.warn("Error processing message:" + message.getBody().toString() + ":" + e.getMessage());
        }
    }

    public void sendResponseToSendServer(final MessageResultDTO messageResultDTO){
        changeMessageStatusSuccess(messageResultDTO);

        rabbitTemplate.convertAndSend(RECEIVE_EXCHANGE_NAME, KE_RECEIVE_ROUTING_KEY, messageResultDTO);
    }

    public MessageResultDTO changeMessageStatusSuccess(MessageResultDTO messageResultDTO){
        messageResultDTO.setMessageStatus(MessageStatus.SUCCESS);
        return messageResultDTO;
    }
}
