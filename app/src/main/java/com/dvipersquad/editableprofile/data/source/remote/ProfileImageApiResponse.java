package com.dvipersquad.editableprofile.data.source.remote;

public class ProfileImageApiResponse {

    private String fieldName;
    private String originalName;
    private String fileName;
    private long size;

    public ProfileImageApiResponse(String fieldName, String originalName, String fileName, long size) {
        this.fieldName = fieldName;
        this.originalName = originalName;
        this.fileName = fileName;
        this.size = size;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
