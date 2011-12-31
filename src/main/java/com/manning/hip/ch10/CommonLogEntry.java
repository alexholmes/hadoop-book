package com.manning.hip.ch10;

/**
 */
public class CommonLogEntry {
  private String remoteAddress;
  private String remoteLogname;
  private String userId;
  private String time;
  private String requestLine;
  private Long statusCode;
  private Long objSize;
  private String method;
  private String resource;
  private String protocol;
  private Long epoch;

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public String getRemoteLogname() {
    return remoteLogname;
  }

  public void setRemoteLogname(String remoteLogname) {
    this.remoteLogname = remoteLogname;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getRequestLine() {
    return requestLine;
  }

  public void setRequestLine(String requestLine) {
    this.requestLine = requestLine;
  }

  public Long getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Long statusCode) {
    this.statusCode = statusCode;
  }

  public Long getObjSize() {
    return objSize;
  }

  public void setObjSize(Long objSize) {
    this.objSize = objSize;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public Long getEpoch() {
    return epoch;
  }

  public void setEpoch(Long epoch) {
    this.epoch = epoch;
  }
}
