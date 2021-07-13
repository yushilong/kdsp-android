package com.qizhu.rili.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5Utils {
    private static String MD5 = "MD5";
    private static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    private static final int STREAM_BUFFER_LENGTH = 128 * 1024;
    private static final byte[] HEX_BYTES = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68,
            69, 70};

    public static synchronized String computeMd5forPkg(byte[] hex) {
        if (hex == null) {
            return null;
        }
        int i1;
        int i2;
        byte[] byteBuffers = new byte[2 * hex.length];
        for (int i = 0; i < hex.length; ++i) {
            i1 = (hex[i] & 0xf0) >> 4;
            byteBuffers[2 * i] = HEX_BYTES[i1];
            i2 = hex[i] & 0xf;
            byteBuffers[2 * i + 1] = HEX_BYTES[i2];
        }

        byte[] md5Bytes = messageDigest.digest(byteBuffers);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * Compute the input data's md5 value.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String md5Digest(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            messageDigest.update(buffer, 0, read);
            read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return getHexString(messageDigest.digest());
    }

    public static String md5Digest(InputStream inputStream, byte[] buffer) throws IOException {
        int read = inputStream.read(buffer, 0, buffer.length);
        while (read > -1) {
            messageDigest.update(buffer, 0, read);
            read = inputStream.read(buffer, 0, buffer.length);
        }
        return getHexString(messageDigest.digest());
    }

    /**
     * Compute the input string's md5 value.
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static String md5Digest(String input) throws IOException {
        messageDigest.update(input.getBytes());
        return getHexString(messageDigest.digest());
    }

    public static byte[] md5Digest(byte[] input) throws IOException {
        messageDigest.update(input);
        return messageDigest.digest();
    }

    public static String md5DigestStr(byte[] input) throws IOException {
        return getHexString(md5Digest(input));
    }

    private static String getHexString(byte[] digest) {
        BigInteger bi = new BigInteger(1, digest);
        return String.format("%032x", bi);
    }

    /**
     * 检测文件的MD5值
     */
    public static boolean checkMd5(String filePath, String targetMd5, StringBuilder md5Builder) {
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            String md5 = md5Digest(bufferedInputStream);
            if (md5 != null && md5.equalsIgnoreCase(targetMd5)) {
                if (md5Builder != null) {
                    md5Builder.append(md5);
                }
                return true;
            } else {
                if (md5Builder != null) {
                    if (TextUtils.isEmpty(md5)) {
                        md5Builder.append("empty");
                    } else {
                        md5Builder.append(md5);
                    }
                }
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取MD5值
     * @param str       源地址
     * @return          MD5之后的地址
     */
    public static String MD5(String str) {
        byte[] byteArray = str.getBytes();
        byte[] md5Bytes = messageDigest.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
