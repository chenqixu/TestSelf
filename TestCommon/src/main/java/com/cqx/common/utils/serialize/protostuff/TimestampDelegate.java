package com.cqx.common.utils.serialize.protostuff;

import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Pipe;
import com.dyuproject.protostuff.WireFormat.FieldType;
import com.dyuproject.protostuff.runtime.Delegate;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * TimestampDelegate
 *
 * @author chenqixu
 */
public class TimestampDelegate implements Delegate<Timestamp> {

    public FieldType getFieldType() {
        return FieldType.FIXED64;
    }

    public Class<?> typeClass() {
        return Timestamp.class;
    }

    public Timestamp readFrom(Input input) throws IOException {
        return new Timestamp(input.readFixed64());
    }

    public void writeTo(Output output, int number, Timestamp value,
                        boolean repeated) throws IOException {
        output.writeFixed64(number, value.getTime(), repeated);
    }

    @Override
    public void transfer(Pipe pipe, Input input, Output output, int number,
                         boolean repeated) throws IOException {
        output.writeFixed64(number, input.readFixed64(), repeated);
    }
}
