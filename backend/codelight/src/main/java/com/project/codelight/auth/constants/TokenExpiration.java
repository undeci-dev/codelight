package com.project.codelight.auth.constants;

import java.util.Calendar;
import lombok.Getter;

@Getter
public enum TokenExpiration {
    ACCESS_TOKEN(10, Calendar.MINUTE),
    REFRESH_TOKEN(14, Calendar.DATE);

    private final int value;
    private final int calendarField;

    TokenExpiration(int value, int calendarField) {
        this.value = value;
        this.calendarField = calendarField;
    }

    public long getExpirationInSeconds() {
        if (calendarField == Calendar.MINUTE) {
            return value * 60L;
        } else if (calendarField == Calendar.HOUR) {
            return value * 60L * 60L;
        } else if (calendarField == Calendar.DATE) {
            return value * 24L * 60L * 60L;
        }
        return 0;
    }

    public long getExpirationInMillis() {
        return getExpirationInSeconds() * 1000L;
    }
}
