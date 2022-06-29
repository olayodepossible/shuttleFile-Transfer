package com.possible.shutterflyfiletransfer.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProducerResponse {
    private String status;
    private String message;
}
