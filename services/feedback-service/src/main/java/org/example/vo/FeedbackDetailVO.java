package org.example.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackDetailVO extends FeedbackVO {

    private List<FeedbackMessageVO> messages;
}
