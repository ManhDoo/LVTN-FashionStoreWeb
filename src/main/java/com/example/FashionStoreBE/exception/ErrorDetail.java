package com.example.FashionStoreBE.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {

    private String message;
    private String error;
    private LocalDateTime timeStamp;
}
