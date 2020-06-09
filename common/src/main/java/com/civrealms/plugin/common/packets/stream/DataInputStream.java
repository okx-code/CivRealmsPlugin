package com.civrealms.plugin.common.packets.stream;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.util.UUID;

public class DataInputStream implements ByteArrayDataInput {
  private final ByteArrayDataInput delegate;

  public DataInputStream(byte[] data) {
    this.delegate = ByteStreams.newDataInput(data);
  }

  @Override
  public void readFully(byte[] b) {
    delegate.readFully(b);
  }

  @Override
  public void readFully(byte[] b, int off, int len) {
    delegate.readFully(b, off, len);
  }

  @Override
  public int skipBytes(int n) {
    return delegate.skipBytes(n);
  }

  @Override
  public boolean readBoolean() {
    return delegate.readBoolean();
  }

  @Override
  public byte readByte() {
    return delegate.readByte();
  }

  @Override
  public int readUnsignedByte() {
    return delegate.readUnsignedByte();
  }

  @Override
  public short readShort() {
    return delegate.readShort();
  }

  @Override
  public int readUnsignedShort() {
    return delegate.readUnsignedShort();
  }

  @Override
  public char readChar() {
    return delegate.readChar();
  }

  @Override
  public int readInt() {
    return delegate.readInt();
  }

  @Override
  public long readLong() {
    return delegate.readLong();
  }

  @Override
  public float readFloat() {
    return delegate.readFloat();
  }

  @Override
  public double readDouble() {
    return delegate.readDouble();
  }

  @Override
  public String readLine() {
    return delegate.readLine();
  }

  @Override
  public String readUTF() {
    return delegate.readUTF();
  }

  public byte[] readByteArray() {
    int len = readInt();
    byte[] b = new byte[len];
    readFully(b);
    return b;
  }

  public UUID readUUID() {
    long mostSig = delegate.readLong();
    long leastSig = delegate.readLong();
    return new UUID(mostSig, leastSig);
  }
}
