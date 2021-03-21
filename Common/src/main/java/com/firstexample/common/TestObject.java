package com.firstexample.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TestObject {

    private String name;
    private int number;
    private long sendTimestamp;
    private long receiveTimestamp;

    public TestObject(String name, int number, long sendTimestamp, long receiveTimestamp) {
        this.name = name;
        this.number = number;
        this.sendTimestamp = sendTimestamp;
        this.receiveTimestamp = receiveTimestamp;
    }
}
