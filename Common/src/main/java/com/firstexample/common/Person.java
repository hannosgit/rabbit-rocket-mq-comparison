package com.firstexample.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Person {

    private long personalId;
    private String firstName;
    private String lastName;
    private String location;

    private long controlNumber;
    private long sendTimestamp;
    private long receiveTimestamp;

    public Person(long personalId, String firstName, String lastName, String location, long controlNumber, long sendTimestamp, long receiveTimestamp) {
        this.personalId = personalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;

        this.controlNumber = controlNumber;
        this.sendTimestamp = sendTimestamp;
        this.receiveTimestamp = receiveTimestamp;
    }
}
