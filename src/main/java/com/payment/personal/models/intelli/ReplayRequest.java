package com.payment.personal.models.intelli;

import java.util.List;

public record ReplayRequest(
        List<String> steps
) {
}
