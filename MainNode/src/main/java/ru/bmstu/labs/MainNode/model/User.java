package ru.bmstu.labs.MainNode.model;

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
