package ru.bmstu.labs.customdb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TerminateResponse implements LabDTO {
    private String message;
}
