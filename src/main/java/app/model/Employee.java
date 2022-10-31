package app.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class Employee {

    @EqualsAndHashCode.Exclude
    private int id;
    private String firstName;
    private String lastName;
    private double salary;

}
