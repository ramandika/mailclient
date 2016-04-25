/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fsck.k9.crypto.ECCElgamal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author Michael
 * Only used in Input/Output tools (Read file, write file, etc.)
 */
public class IOUtils {

    
    public static BigInteger getData(String message) throws IOException {
        byte[] data = message.getBytes();
        return new BigInteger(data);
    }
    
    public static ArrayList<BigInteger> getDataArray(String message) throws IOException {
        ArrayList<BigInteger> ret = new ArrayList();
        byte[] data = message.getBytes();
        byte[][] matData = transformToTwoD(data, 16);
        for (byte[] matData1 : matData) {
            ret.add(new BigInteger(matData1));
        }
        return ret;
    }
    
    private static byte[][] transformToTwoD(byte[] oneD, int width) {
        int len = oneD.length;
        int height = (len/width) + 1;
        byte[][] ret = new byte[height][width];
        int count=0;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                if(count==oneD.length) break;
                ret[i][j]=oneD[count];
                count++;
            }
        }
        return ret;
    }
    
/*    public static void writeData(String address, BigInteger toWrite) throws IOException {
        Path path = Paths.get(address);
        byte[] data = toWrite.toByteArray();
        Files.write(path, data);
    }*/
    
/*    public static void writeDataFromList(String address, ArrayList<BigInteger> toWrite) throws IOException {
        ArrayList<Byte[]> data = new ArrayList();
        for(BigInteger bi : toWrite) {
            byte[] byteArr = bi.toByteArray();
            Byte[] ByteArr = new Byte[byteArr.length];
            for(int i = 0; i < byteArr.length; i++) ByteArr[i] = byteArr[i];
            data.add(ByteArr);
        }
        int lastElLen = 0;
        for(lastElLen = 0; lastElLen < data.get(data.size()-1).length; lastElLen++) {
            if (data.get(data.size()-1)[lastElLen] == 0) {
                break;
            }
        }
        byte[] combinedArr = new byte[((data.size()-1) * data.get(0).length) + lastElLen];
        int j = 0;
        for(Byte[] arr : data) {
            for (Byte arrEl : arr) {
                combinedArr[j] = arrEl;
                j++;
                if (j == combinedArr.length) break;
            }
        }
        BigInteger combined = new BigInteger(combinedArr);
        writeData(address, combined);
    }*/
    
    public static String formattedOutput(ArrayList<ECCEG.CipherPair> data) {
        StringBuilder sb = new StringBuilder();
        for(ECCEG.CipherPair pairpoint:data){
            EllipticalCurve.Point p1=pairpoint.getP1();
            EllipticalCurve.Point p2=pairpoint.getP2();
            sb.append("[(");
            sb.append(p1.getX().toString(16));
            sb.append(",");
            sb.append(p1.getY().toString(16));
            sb.append("), (");
            sb.append(p2.getX().toString(16));
            sb.append(",");
            sb.append(p2.getY().toString(16));
            sb.append(")]");
            sb.append("\n");
        }
        return sb.toString();
    }
    
/*    public static void writeCipherFile(String address, ArrayList<ECCEG.CipherPair> toWrite) throws IOException {
        FileOutputStream fos = new FileOutputStream(address);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(toWrite);
        fos.close();
    }*/
    
    public static ArrayList<ECCEG.CipherPair> readCipherFile(String address) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(address);
        ObjectInputStream in = new ObjectInputStream(fis);
        ArrayList<ECCEG.CipherPair> ret = (ArrayList<ECCEG.CipherPair>) in.readObject();
        fis.close();
        return ret;
    }
    
/*    public static void writePublicKey(String address, EllipticalCurve.Point publicKey) throws IOException {
        FileOutputStream fos = new FileOutputStream(address);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(publicKey);
        fos.close();
    }*/
    
    public static EllipticalCurve.Point readPublicKey(String address) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(address);
        ObjectInputStream in = new ObjectInputStream(fis);
        EllipticalCurve.Point ret = (EllipticalCurve.Point) in.readObject();
        fis.close();
        return ret;
    }
}
