package com.manning.hip.ch10;

import com.maxmind.geoip.LookupService;
import org.apache.pig.EvalFunc;
import org.apache.pig.FuncSpec;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * a = load '1.txt';
 * b = foreach a generate .com.manning.hip.ch7.PigGeolocationUDF(*);
 * dump b;
 */
public class PigGeolocationUDF extends EvalFunc<String> {
  private LookupService geoloc;
  private static final String COUNTRY = "country";

  public String exec(Tuple input) throws IOException {

    if (input == null || input.size() == 0) {
      return null;
    }

    Object object = input.get(0);
    if (object == null) {
      return null;
    }

    String ip = (String) object;

    return lookup(ip);
  }

  protected String lookup(String ip) throws IOException {
    if (geoloc == null) {
      geoloc =
        new LookupService("./geoip", LookupService.GEOIP_MEMORY_CACHE);
    }

    String country = geoloc.getCountry(ip).getName();

    if ("N/A".equals(country)) {
      return null;
    }

    return country;
  }

  public List<String> getCacheFiles() {
    List<String> list = new ArrayList<String>(1);
    list.add("/app/GeoIP.dat#geoip");
    return list;
  }

  @Override
  public List<FuncSpec> getArgToFuncMapping()
    throws FrontendException {
    List<FuncSpec> funcList = new ArrayList<FuncSpec>();
    funcList.add(new FuncSpec(this.getClass().getName(),
      new Schema(new Schema.FieldSchema(null, DataType.CHARARRAY))));
    funcList.add(new FuncSpec(PigLongGeolocationUDF.class.getName(),
      new Schema(new Schema.FieldSchema(null, DataType.LONG))));
    return funcList;
  }

  @Override
  public Schema outputSchema(Schema input) {
    return new Schema(
      new Schema.FieldSchema(COUNTRY, DataType.CHARARRAY));
  }

}
