package com.arn.gab;

/**
 * Created by arn on 10/10/2017.
 */

public class Messages {

    private String message,type;
    private Long time;

    public String getsenttime() {
        return senttime;
    }

    public void setsenttime(String senttime) {
        this.senttime = senttime;
    }

    private String senttime;

    public Messages(String message) {
        this.message = message;
    }

    private String from;
    private Boolean seen;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public Messages(String message, Boolean seen, Long time, String type,String senttime) {

        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.senttime = senttime;

    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Messages(){

    }

}
