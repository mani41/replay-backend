package com.payment.personal.models.replay.response;

import java.util.List;

public record GeneratedReplayEvents(String title,
                                    String summary,
                                    List<String> tags,
                                    List<GeneratedEventSteps> steps) {
}
