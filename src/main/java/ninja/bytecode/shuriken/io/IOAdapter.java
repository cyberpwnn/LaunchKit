package ninja.bytecode.shuriken.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IOAdapter<T>
{
    public void write(T t, DataOutputStream dos) throws IOException;

    public T read(DataInputStream din) throws IOException;
}
