package com.DBService.Entiry;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Result {
    private String Name;
    private List<SubClass> SubClasses;

}
