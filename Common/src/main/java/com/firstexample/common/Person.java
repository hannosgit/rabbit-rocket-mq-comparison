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

    private String padding;
    private long messageNumber;
    private long sendTimestamp;
    private long receiveTimestamp;

    public Person(long personalId, String firstName, String lastName, String location, String padding, long messageNumber, long sendTimestamp, long receiveTimestamp) {
        this.personalId = personalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.padding = padding;
        this.messageNumber = messageNumber;
        this.sendTimestamp = sendTimestamp;
        this.receiveTimestamp = receiveTimestamp;
    }
}
