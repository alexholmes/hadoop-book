package com.manning.hip.ch3.avro.gen;

@SuppressWarnings("all")
public class StockAvg extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"StockAvg\",\"namespace\":\"com.manning.hip.ch3.avro.gen\",\"fields\":[{\"name\":\"symbol\",\"type\":\"string\"},{\"name\":\"avg\",\"type\":\"double\"}]}");
  public org.apache.avro.util.Utf8 symbol;
  public double avg;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return symbol;
    case 1: return avg;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: symbol = (org.apache.avro.util.Utf8)value$; break;
    case 1: avg = (java.lang.Double)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
