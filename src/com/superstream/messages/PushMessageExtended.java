package com.superstream.messages;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PushMessageExtended implements Serializable{   
    public enum SEVERITY {
         LOW(1), MEDIUM(2), HIGH(3), EMERG(4);
         private final int value;

        private SEVERITY(int value) {
            this.value = value;
        }
         
        public int getValue() {
            return value;
        }
    }   
    private String appID = "";
    private String title = " ";
    private String body = " ";
    private int poolID;
    private SEVERITY severity = SEVERITY.MEDIUM;
    private String additionalAppID = "";

    private Date date = new Date();

    public PushMessageExtended(String appID, String title, String body,SEVERITY severity, Date date) {
        this.appID = appID;
        this.title = title;
        this.body = body;
        this.date = date;
        this.severity = severity;
    }

    public PushMessageExtended(String appID, String title, String body,SEVERITY severity, int poolID, Date date) {
        this.appID = appID;
        this.title = title;
        this.body = body;
        this.poolID = poolID;
        this.date = date;
        this.severity = severity;
    }

    public PushMessageExtended(String body, int poolID, Date date) {
        this.body = body;
        this.poolID = poolID;
        this.date = date;
    }
    
    
    public PushMessageExtended(String body, int poolID, Date date,SEVERITY severity) {
        this.body = body;
        this.poolID = poolID;
        this.date = date;
        this.severity = severity;
    }
    
    
    public PushMessageExtended(String title, String body) {
        this.title = title;
        this.body = body;
    }   
  
    
     public PushMessageExtended(String title, String body,SEVERITY severity) {
        this.title = title;
        this.body = body;
        this.severity = severity;
    }   

    public PushMessageExtended(String appID, String title, String body, int poolID) {
        this.appID = appID;
        this.title = title;
        this.body = body;
        this.poolID = poolID;
    }

    public PushMessageExtended(String appID, String title, String body) {
        this.appID = appID;
        this.title = title;
        this.body = body;
    }
    
    public PushMessageExtended(String appID, String title, String body,SEVERITY severity) {
        this.appID = appID;
        this.title = title;
        this.body = body;
        this.severity = severity;
    }

    public PushMessageExtended(String title, String body, int poolID) {
        this.title = title;
        this.body = body;
        this.poolID = poolID;
    }
    
    public PushMessageExtended(String title, String body, int poolID,SEVERITY severity) {
        this.title = title;
        this.body = body;
        this.poolID = poolID;
        this.severity = severity;
    }

    public PushMessageExtended() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }    

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public int getPoolID() {
        return poolID;
    }

    public void setPoolID(int poolID) {
        this.poolID = poolID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getDateAsString(){
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS");
        
        
        return sdf.format(date);
    }

    public SEVERITY getSeverity() {
        return severity;
    }

    public void setSeverity(SEVERITY severity) {
        this.severity = severity;
    }

    public String getAdditionalAppID() {
        return additionalAppID;
    }

    public void setAdditionalAppID(String additionalAppID) {
        this.additionalAppID = additionalAppID;
    }
}
