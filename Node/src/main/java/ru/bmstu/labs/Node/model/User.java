package ru.bmstu.labs.Node.model;

import lombok.*;

@Getter
@Setter
@ToString
public class User extends IsolationEntity {
    private Long id;
    private String name;
    private String lastName;
    private String email;
}
