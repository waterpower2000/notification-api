package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDataTempDTO {
    public String requestId;
    private List<String> warning;
    private List<String> error;
    private List<Map<String, String>> newEntry;
    private List<Map<String, String>> updatedEntry;
}
