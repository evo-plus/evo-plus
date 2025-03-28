package ru.dargen.evoplus.extension;

import io.netty.buffer.ByteBuf;

public interface UnknownCustomPayloadExtension {

    void setPayload(ByteBuf buf);

    ByteBuf payload();

}
