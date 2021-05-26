package com.cqx.common.utils.serialize.kryo.registrar;

import com.esotericsoftware.kryo.Kryo;

import java.io.Serializable;

/**
 * A Registrar adds registrations to a given Kryo instance.
 * Examples would be a registrar that registers serializers
 * for all objects in a given package.
 * comes from Storm, which took it from cascading.kryo
 */
public interface IKryoRegistrar extends Serializable {
    void apply(Kryo k);
}
