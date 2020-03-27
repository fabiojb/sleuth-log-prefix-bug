package com.example.sleuthlogprefixbug;

public class LogPrefixContentException extends RuntimeException {

    private String logPrefix;

    public LogPrefixContentException(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public String getLogPrefix() {
        return logPrefix;
    }
}
