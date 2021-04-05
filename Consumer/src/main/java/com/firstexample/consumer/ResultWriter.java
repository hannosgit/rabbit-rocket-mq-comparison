package com.firstexample.consumer;

import com.firstexample.common.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

public class ResultWriter {

    private static final String[] CSV_HEADER = {"message number", "send[ms]", "send[DATETIME]", "receive[ms]", "receive[DATETIME]", "difference[ms]"};

    public static void writeResult(String fileName, Iterable<Person> result) {
        try {
            FileWriter out = new FileWriter(fileName);
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(CSV_HEADER))) {
                result.forEach(person -> {
                    try {
                        printer.printRecord(
                                Arrays.asList(
                                        person.getMessageNumber(),
                                        person.getSendTimestamp(),
                                        LocalDateTime.ofInstant(Instant.ofEpochMilli(person.getSendTimestamp()), ZoneId.systemDefault()),
                                        person.getReceiveTimestamp(),
                                        LocalDateTime.ofInstant(Instant.ofEpochMilli(person.getReceiveTimestamp()), ZoneId.systemDefault()),
                                        person.getReceiveTimestamp() - person.getSendTimestamp()
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
