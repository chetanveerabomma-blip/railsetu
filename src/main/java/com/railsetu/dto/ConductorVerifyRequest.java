package com.railsetu.dto;

public class ConductorVerifyRequest {
    private String pnrOrToken;
    private String stationCode;
    private String remarks;

    public ConductorVerifyRequest() {}

    public ConductorVerifyRequest(String pnrOrToken, String stationCode) {
        this.pnrOrToken = pnrOrToken;
        this.stationCode = stationCode;
    }

    public String getPnrOrToken() { return pnrOrToken; }
    public void setPnrOrToken(String pnrOrToken) { this.pnrOrToken = pnrOrToken; }
    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
