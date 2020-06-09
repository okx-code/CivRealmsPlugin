package com.civrealms.plugin.common.packets.stream;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.UUID;

public class DataOutputStream implements ByteArrayDataOutput {
  private final ByteArrayDataOutput delegate;

  public DataOutputStream() {
    this.delegate = ByteStreams.newDataOutput();
  }

  @Override
  public void write(int b) {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b) {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) {
    delegate.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) {
    delegate.writeBoolean(v);
  }

  @Override
  public void writeByte(int v) {
    delegate.writeByte(v);
  }

  @Override
  public void writeShort(int v) {
    delegate.writeShort(v);
  }

  @Override
  public void writeChar(int v) {
    delegate.writeChar(v);
  }

  @Override
  public void writeInt(int v) {
    delegate.writeInt(v);
  }

  @Override
  public void writeLong(long v) {
    delegate.writeLong(v);
  }

  @Override
  public void writeFloat(float v) {
    delegate.writeFloat(v);
  }

  @Override
  public void writeDouble(double v) {
    delegate.writeDouble(v);
  }

  @Override
  public void writeChars(String s) {
    delegate.writeChars(s);
  }

  @Override
  public void writeUTF(String s) {
    delegate.writeUTF(s);
  }

  @Override
  @Deprecated
  public void writeBytes(String s) {
    delegate.writeBytes(s);
  }

  public void writeByteArray(byte[] b) {
    writeInt(b.length);
    write(b);
  }

  @Override
  public byte[] toByteArray() {
    return delegate.toByteArray();
  }

  public void writeUUID(UUID u) {
    writeLong(u.getMostSignificantBits());
    writeLong(u.getLeastSignificantBits());
  }
}
