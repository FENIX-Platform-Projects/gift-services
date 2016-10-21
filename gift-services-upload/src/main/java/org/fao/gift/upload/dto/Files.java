package org.fao.gift.upload.dto;

public enum Files {

    subject("subject.csv"), consumption("consumption.csv");
    private String fileName;

    Files(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public static Files get(String fileName) {
        for (Files f : Files.values())
            if (f.getFileName().equalsIgnoreCase(fileName))
                return f;
        return null;
    }
}
