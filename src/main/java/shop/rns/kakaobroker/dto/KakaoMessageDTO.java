package shop.rns.kakaobroker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.rns.kakaobroker.config.status.MessageStatus;
import shop.rns.kakaobroker.config.type.ButtonType;

import javax.persistence.Embedded;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoMessageDTO {
    private long messageId;

    private String from;

    @JsonIgnore
    private String to;

    private String title;

    private String subtitle;

    private String content;

    private String description;

    private String image;

    private MessageStatus messageStatus;

    @JsonIgnore
    private String reserveTime;

    @JsonIgnore
    private String scheduleCode;

    // kakao button
    private List<ButtonDTO> buttonDTOList;
}
