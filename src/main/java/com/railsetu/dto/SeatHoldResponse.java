package com.railsetu.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SeatHoldResponse {
    private boolean success;
    private String holdToken;
    private List<Long> lockedSeatIds;
    private LocalDateTime expiresAt;
    private long remainingSeconds;
    private String message;

    public SeatHoldResponse() {}

    public SeatHoldResponse(boolean success, String holdToken, List<Long> lockedSeatIds, LocalDateTime expiresAt, long remainingSeconds, String message) {
        this.success = success;
        this.holdToken = holdToken;
        this.lockedSeatIds = lockedSeatIds;
        this.expiresAt = expiresAt;
        this.remainingSeconds = remainingSeconds;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getHoldToken() { return holdToken; }
    public void setHoldToken(String holdToken) { this.holdToken = holdToken; }
    public List<Long> getLockedSeatIds() { return lockedSeatIds; }
    public void setLockedSeatIds(List<Long> lockedSeatIds) { this.lockedSeatIds = lockedSeatIds; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
