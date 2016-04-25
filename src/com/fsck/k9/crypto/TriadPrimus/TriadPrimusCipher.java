/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fsck.k9.crypto.TriadPrimus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Michael
 */
public class TriadPrimusCipher {
    int rounds;
    
    public TriadPrimusCipher(int rounds) {
        this.rounds = rounds;
    }
    
    public long encrypt(long plainBlock, long currentKey) {
        long ret;
        byte leftBits, rightBits, keyBits;
        ret = plainBlock;
        int i,j,n;
        ArrayList<Long> subkeys = generateSubKeys(currentKey);
        for(i=0 ; i < rounds; i++) {
            long currentSubkey = subkeys.get(i);
            j = 0;
            for(n=0; n < 32; n++) {
                keyBits = getTwoBits(currentSubkey,j);
                
                rightBits = Block.GetLastTwoBits(ret);
                
                rightBits ^= keyBits;
                
                leftBits = Block.GetFirstTwoBits(ret);
                leftBits = SwitchTwoBitPos(leftBits);
                
                leftBits ^= rightBits;
                leftBits = SwitchTwoBitPos(leftBits);
                ret = Block.ModifyFirstTwoBits(ret, leftBits);
                
                ret = Block.CircularShiftLeft(ret);
                j+=2;
            }
        }
        return ret;
    }
    
    public long decrypt(long cipherBlock, long currentKey) {
        long ret;
        byte leftBits, rightBits, keyBits;
        ret = cipherBlock;
        int i,j,n;
        ArrayList<Long> subkeys = generateSubKeys(currentKey);
        Collections.reverse(subkeys);
        for(i=0 ; i < rounds; i++) {
            long currentSubkey = subkeys.get(i);
            j = 62;
            for(n=0;n<32;n++) {
                keyBits = getTwoBits(currentSubkey,j);
                
                ret = Block.CircularShiftRight(ret);
                
                leftBits = Block.GetFirstTwoBits(ret);
                leftBits = SwitchTwoBitPos(leftBits);
                
                rightBits = Block.GetLastTwoBits(ret);
                rightBits ^= keyBits;
                
                leftBits ^= rightBits;
                leftBits = SwitchTwoBitPos(leftBits);
                ret = Block.ModifyFirstTwoBits(ret, leftBits);
                
                j-=2;
            }
        }
        return ret;
    }
    
    private ArrayList<Long> generateSubKeys(long currentKey) {
        long seed = currentKey;
        int i;
        ArrayList<Long> subkeys = new ArrayList();
        Random rng = new Random(seed);
        for(i=0;i<rounds;i++) {
            subkeys.add(rng.nextLong());
        }
     return subkeys;   
    }
    
    private byte getTwoBits(long subkey, int bitPos) {
        byte ret = 0;
        byte mask = 0x3;
        long tmp = subkey;
        tmp = tmp & ((long)mask << (62-bitPos));
        tmp = (tmp >> (62-bitPos)) & mask;
        return (byte) tmp;
    }
    
    private byte SwitchTwoBitPos(byte bits) {
        byte tmp = bits;
        byte tmp2 = bits;
        tmp = (byte) (tmp & 0x2);
        tmp = (byte) (tmp >> 1);
        return (byte) (((tmp2 << 1) & 0x3) | tmp);
    }
}
