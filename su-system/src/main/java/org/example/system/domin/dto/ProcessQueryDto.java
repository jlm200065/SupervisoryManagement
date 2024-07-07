package org.example.system.domain.dto;

public class ProcessQueryDto {
    private String name;
    private String engineCategory;
    private int pageNum;
    private int pageSize;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEngineCategory() {
        return engineCategory;
    }

    public void setEngineCategory(String engineCategory) {
        this.engineCategory = engineCategory;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
