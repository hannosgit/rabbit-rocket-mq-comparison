package com.firstexample.consumer;

import com.firstexample.common.TestObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

public class ResultWriter {

    private static final String[] CSV_HEADER = {"name", "number", "send[ms]", "send[DATETIME]", "receive[ms]", "receive[DATETIME]", "difference[ms]"};

    public static void writeResult(String fileName, Iterable<TestObject> result) {
        try {
            FileWriter out = new FileWriter(fileName);
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(CSV_HEADER))) {
                result.forEach(testObject -> {
                    try {
                        printer.printRecord(
                                Arrays.asList(
                                        testObject.getName(),
                                        testObject.getNumber(),
                                        testObject.getSendTimestamp(),
                                        LocalDateTime.ofInstant(Instant.ofEpochMilli(testObject.getSendTimestamp()), ZoneId.systemDefault()),
                                        testObject.getReceiveTimestamp(),
                                        LocalDateTime.ofInstant(Instant.ofEpochMilli(testObject.getReceiveTimestamp()), ZoneId.systemDefault()),
                                        testObject.getReceiveTimestamp() - testObject.getSendTimestamp()
                                ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
