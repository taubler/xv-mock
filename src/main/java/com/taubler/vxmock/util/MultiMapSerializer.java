package com.taubler.vxmock.util;

import java.io.IOException;

import io.vertx.core.MultiMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MultiMapSerializer extends JsonSerializer<MultiMap> {

	@Override
	public void serialize(MultiMap mm, JsonGenerator jgen, SerializerProvider provuder) 
			throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        mm.entries().forEach(e -> {
        	try {
				jgen.writeObjectField(e.getKey(), e.getValue());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
//        jgen.writeNumberField("id", value.id);
//        jgen.writeStringField("itemName", value.itemName);
//        jgen.writeNumberField("owner", value.owner.id);
        jgen.writeEndObject();
	}

}
