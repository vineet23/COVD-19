package com.paper.squeeze.covd_19;

public class Detail {

    private String header;
    private String details;
    private int raw;
    private String why;

    public Detail(String header,String details,int raw,String why) {
        this.header = header;
        this.details = details;
        this.raw = raw;
        this.why = why;
    }

    public String getHeader(){
        return this.header;
    }

    public String getDetails(){
        return  this.details;
    }

    public int getRaw(){
        return this.raw;
    }

    public String getWhy(){
        return this.why;
    }

}
