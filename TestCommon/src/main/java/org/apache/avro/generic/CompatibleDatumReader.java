package org.apache.avro.generic;

import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;

import java.io.IOException;

/**
 * CompatibleDatumReader<br>
 * <pre>
 *  如果没有更多的数据可读,说明read schema和write schema并不一致
 *  这种情况直接忽略不一致字段
 * </pre>
 *
 * @author chenqixu
 */
public class CompatibleDatumReader<D> extends GenericDatumReader<D> {

    public CompatibleDatumReader(Schema schema) {
        super(schema);
    }

    protected Object readRecord(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Object r = getData().newRecord(old, expected);
        Object state = getData().getRecordState(r, expected);
        for (Schema.Field f : in.readFieldOrder()) {
            int pos = f.pos();
            String name = f.name();
            Object oldDatum = (old != null) ? getData().getField(r, name, pos, state) : null;
            try {
                getData().setField(r, name, pos, read(oldDatum, f.schema(), in), state);
            } catch (IOException e) {
                continue;
//            } catch (EOFException e) {
//                //如果没有更多的数据可读,说明read schema和write schema并不一致
//                //这种情况直接忽略不一致字段
//                continue;
            }
        }
        return r;
    }
}
