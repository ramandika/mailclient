/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fsck.k9.crypto.TriadPrimus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Block {
    ArrayList<Long> dataBlock;
    public Block(String data) throws UnsupportedEncodingException {
        if (data.length() % 8 != 0) {
            //do padding
            int padby = 8- (data.length() % 8);
            for(int i = 0; i < padby; i++) data += "\0";
        }
        dataBlock = new ArrayList();
        byte[] dataByte = data.getBytes("ISO-8859-1");
        int i,j;
        for (i = 0; i < dataByte.length; i+=8) {
            long lng = 0L;
            for(j=0; j < 8; j++) {
                lng |= ((long)dataByte[i+j] & 0xFF) << (64-8*(j+1)) ;
            }
            dataBlock.add(lng);
        }
        
    }
    
    public ArrayList<Long> getDataBlock() {
        return dataBlock;
    }
    
    public String OutputToString() throws UnsupportedEncodingException {
        byte[] databyte = new byte[dataBlock.size()*8];
        int i = 0;
        for(Long lng : dataBlock) {
            int j = 0;
            for(j=0; j < 8; j++) {
                databyte[i] = (byte) ((lng >> (64-8*(j+1))) & 0xFF);
                i++;
            }
        }
        String retstr = new String(databyte, "ISO-8859-1");
        return retstr;
    }
    
    public static byte GetLastTwoBits (long blk) {
        return (byte) (blk & 0x3);
    }
    
    public static byte GetFirstTwoBits (long blk) {
        return (byte) (blk >> 62 & 0x3);
    }
    
    public static long ModifyLastTwoBits (long blk, byte val) {
        return blk & ~(0x3) | val;
    }
    
    public static long ModifyFirstTwoBits (long blk, byte val) {
        return blk & ~(0x3L << 62) | ((long)val << 62);
    }
    
    public static long CircularShiftLeft (long blk) {
        byte msb =(byte) (blk >> 62 & 0x3);
        return ModifyLastTwoBits(blk << 2, msb);
    }
    
    public static long CircularShiftRight (long blk) {
        byte lsb = (byte) (blk & 0x3);
        return ModifyFirstTwoBits(blk >> 2, lsb);
    }

}
