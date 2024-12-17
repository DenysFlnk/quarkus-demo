package com.entity.converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import io.quarkus.logging.Log;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class GeometryConverter implements AttributeConverter<Geometry, String> {

    @Override
    public String convertToDatabaseColumn(Geometry geom) {
        if (geom == null) {
            return null;
        }
        String wkbString = WKBWriter.toHex(writer().write(geom));
        Log.debug("Convert " + geom + " to " + wkbString);
        return wkbString;
    }

    @Override
    public Geometry convertToEntityAttribute(String wkbString) {
        if (wkbString == null) {
            return null;
        }
        Geometry geom;
        try {
            geom = reader().read(WKBReader.hexToBytes(wkbString));
            Log.debug("Convert " + wkbString + " to " + geom);
            return geom;
        } catch (ParseException e) {
            Log.error("Failed to convert to entity, failed WKB value: '%s'".formatted(wkbString), e);
            return null;
        }
    }

    private WKBReader reader() {
        return new WKBReader();
    }

    private WKBWriter writer() {
        return new WKBWriter();
    }
}
