package com.infsml.hachiman;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.digest("I am salting this weird lake XD".getBytes(StandardCharsets.UTF_8));
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
    public static String getHash(String input){
        try {
            return toHexString(getSHA(input));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return input;
    }
}