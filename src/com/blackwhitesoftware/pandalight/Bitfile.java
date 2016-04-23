package com.blackwhitesoftware.pandalight;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by hudini on 15.04.2016.
 */
public class Bitfile {
    private String designName;
    private String partName;
    private Date creationDate;
    private byte[] data;

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static DateFormat timeFormat = new SimpleDateFormat("ss:mm:HH");

    public Bitfile() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public Bitfile(Path path) throws IOException {
        this();
        byte[] fileContent = Files.readAllBytes(path);
        parseBitfile(fileContent);
    }

    public Bitfile(String path) throws IOException {
        this(Paths.get(path));
    }

    public Bitfile(File file) throws IOException {
        this(file.toPath());
    }

    public String getDesignName() {
        return designName;
    }

    public String getPartName() {
        return partName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getLength() {
        return data.length;
    }

    public byte[] getData() {
        return data;
    }

    private void parseBitfile(byte[] fileContent) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(fileContent);

        // Skip header
        int headerLength = buffer.getShort();
        buffer.position(buffer.position() + headerLength + 2);

        while (buffer.hasRemaining()) {
            char fieldIdentifier = (char) buffer.get();
            BitfileField field = BitfileField.fromIdentifier(fieldIdentifier);
            try {
                parseField(field, buffer);
            } catch (ParseException ignored) { }
        }
    }

    private void extractData(ByteBuffer buffer) {
        int length = buffer.getInt();
        data = new byte[length];
        buffer.get(data);

        data = Arrays.copyOfRange(data, 0, 2048);
    }

    private void parseField(BitfileField field, ByteBuffer buffer) throws IOException, ParseException {
        if (field == BitfileField.DATA) {
            extractData(buffer);
            return;
        }

        int length = buffer.getShort();
        String string = new String(buffer.array(), buffer.position(), length - 1, UTF_8);
        buffer.position(buffer.position() + length);

        switch (field) {
            case DESIGN_NAME:
                designName = string;
                break;
            case PART_NAME:
                partName = string;
                break;
            case CREATION_DATE:
                creationDate = dateFormat.parse(string);
                break;
            case CREATION_TIME:
                Date creationTime = timeFormat.parse(string);
                creationDate = setTime(creationDate, creationTime);
                break;
        }
    }

    private Date setTime(Date date, Date time) {
        Calendar dateCal = Calendar.getInstance();
        Calendar timeCal = Calendar.getInstance();
        dateCal.setTime(date);
        timeCal.setTime(time);
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        return new Date(dateCal.getTimeInMillis());
    }
}
