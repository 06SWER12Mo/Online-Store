package com.example.demo.dto.response;

/**
 * Ultra-lightweight DTO for town search dropdown.
 * No entity associations — completely safe for lazy-loading environments.
 */
public class TownSearchResult {

    private Long id;
    private String name;
    private String bigAreaName;
    private String zipCode;

    public TownSearchResult() {}

    public TownSearchResult(Long id, String name, String bigAreaName, String zipCode) {
        this.id = id;
        this.name = name;
        this.bigAreaName = bigAreaName != null ? bigAreaName : "";
        this.zipCode = zipCode != null ? zipCode : "";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBigAreaName() { return bigAreaName; }
    public void setBigAreaName(String bigAreaName) { this.bigAreaName = bigAreaName; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
