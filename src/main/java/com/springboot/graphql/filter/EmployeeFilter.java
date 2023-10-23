package com.springboot.graphql.filter;

import lombok.Data;

@Data
public class EmployeeFilter {

    private FilterField salary;
    private FilterField age;
    private FilterField position;
    private FilterField birthday;

}
