package com.manning.hip.ch3.avro.gen;

@SuppressWarnings("all")
public class Stock extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"Stock\",\"namespace\":\"com.manning.hip.ch3.avro.gen\",\"fields\":[{\"name\":\"symbol\",\"type\":\"string\"},{\"name\":\"date\",\"type\":\"string\"},{\"name\":\"open\",\"type\":\"double\"},{\"name\":\"high\",\"type\":\"double\"},{\"name\":\"low\",\"type\":\"double\"},{\"name\":\"close\",\"type\":\"double\"},{\"name\":\"volume\",\"type\":\"int\"},{\"name\":\"adjClose\",\"type\":\"double\"}]}");
  public org.apache.avro.util.Utf8 symbol;
  public org.apache.avro.util.Utf8 date;
  public double open;
  public double high;
  public double low;
  public double close;
  public int volume;
  public double adjClose;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return symbol;
    case 1: return date;
    case 2: return open;
    case 3: return high;
    case 4: return low;
    case 5: return close;
    case 6: return volume;
    case 7: return adjClose;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: symbol = (org.apache.avro.util.Utf8)value$; break;
    case 1: date = (org.apache.avro.util.Utf8)value$; break;
    case 2: open = (java.lang.Double)value$; break;
    case 3: high = (java.lang.Double)value$; break;
    case 4: low = (java.lang.Double)value$; break;
    case 5: close = (java.lang.Double)value$; break;
    case 6: volume = (java.lang.Integer)value$; break;
    case 7: adjClose = (java.lang.Double)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
