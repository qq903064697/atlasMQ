package cn.atlas.atlasmq.broker.utils;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class ByteConvertUtil {
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        //32位-24位=8位
        //00000000001 0xFF 16
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToInt(byte[] ary) {
        int value;
        value = (ary[0] & 0xFF)
                | ((ary[1] << 8) & 0xFF00)
                | ((ary[2] << 16) & 0xFF0000)
                | ((ary[3] << 24) & 0xFF000000);
        return value;
    }

    public static void main(String[] args) {
        int j = 100;
        //4个字节，1byte=8bit, byte[4]
        byte[] byteContent = ByteConvertUtil.intToBytes(j);
        System.out.println(byteContent.length);
        int result = ByteConvertUtil.bytesToInt(byteContent);
        System.out.println(result);
    }
}
