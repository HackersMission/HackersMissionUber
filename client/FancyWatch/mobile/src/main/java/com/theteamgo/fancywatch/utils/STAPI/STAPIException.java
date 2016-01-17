package com.theteamgo.fancywatch.utils.STAPI;

/**
 * Created by houfang on 16/1/17.
 */


public class STAPIException extends Exception {

    private static final long serialVersionUID = -1L;

    private Integer mHttpResponseCode;
    private String mStatus;

    public STAPIException(Integer httpResponseCode,String status) {
        this.mHttpResponseCode = httpResponseCode;
        this.mStatus = status;
    }


    public Integer getHttpResponseCode() {
        return mHttpResponseCode;
    }

    public String getStatus() {
        return mStatus;
    }


    @Override
    public String toString() {
        return "STAPIException [mHttpResponseCode=" + mHttpResponseCode
                + ", mStatus=" + mStatus + "]";
    }
}
