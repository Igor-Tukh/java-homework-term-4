package ru.spbau.mit.tukh.serverArchitectures;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to serialize and deserialize int arrays using ArrayProtos.
 */
public class Serialize {
    public static byte[] serializeArray(int[] array) {
        List<Integer> list = intListFromArray(array);
        return ArrayProtos.Array.newBuilder().addAllData(list).build().toByteArray();
    }

    private static int[] deserializeArray(byte[] data) throws InvalidProtocolBufferException {
        return intArrayFromList(ArrayProtos.Array.parseFrom(data).getDataList());
    }

    public static int[] deserializeArrayFromByteArray(byte[] data) throws IOException {
        return intArrayFromList(ArrayProtos.Array.parseFrom(data).getDataList());
    }

    public static int[] deserializeArrayFromDataInputStream(DataInputStream dataInputStream) throws IOException {
        int messageLength = dataInputStream.readInt();
        byte[] data = new byte[messageLength];
        dataInputStream.read(data, 0, messageLength);
        return deserializeArray(data);
    }

    public static void writeArrayToDataOutputStream(DataOutputStream dataOutputStream, int[] array) throws IOException {
        byte[] serializedArray = serializeArray(array);
        dataOutputStream.writeInt(serializedArray.length);
        dataOutputStream.write(serializedArray);
    }

    private static List<Integer> intListFromArray(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    private static int[] intArrayFromList(List<Integer> list) {
        int[] array = new int[list.size()];
        int index = 0;
        for (int element: list) {
            array[index++] = element;
        }
        return array;
    }
}
