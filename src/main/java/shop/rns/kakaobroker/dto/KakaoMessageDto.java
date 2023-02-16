package shop.rns.kakaobroker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.rns.kakaobroker.config.status.MessageStatus;
import shop.rns.kakaobroker.config.type.ButtonType;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoMessageDto {
    private String from;

    @JsonIgnore
    private String to;

    private String title;

    private String subtitle;

    private String content;

    private String description;

    private String image;

    private MessageStatus messageStatus;

    private String buttonTitle;

    private String buttonUrl;

    private ButtonType buttonType;

    private String cronExpression;

    private String cronText;
}
