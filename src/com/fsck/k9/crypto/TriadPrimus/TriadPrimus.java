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
public class TriadPrimus {
    private String key;
    private String data;
    private Block dataBlk;
    private ArrayList<Long> ptrToBlk;
    private static int PRIMUS_ROUNDS = 8;
    public TriadPrimus(String data, String key) throws UnsupportedEncodingException {
        this.key = key;
        this.data = data;
        dataBlk = new Block(data);
        ptrToBlk = dataBlk.getDataBlock();
    }
    
    public String Encrypt() throws UnsupportedEncodingException {
        int i;
        TriadPrimusCipher E = new TriadPrimusCipher(PRIMUS_ROUNDS);
        PrimusKey K = new PrimusKey(key, dataBlk.getDataBlock().size());
        for(i=0; i < ptrToBlk.size(); i++) {
            long plaintext = ptrToBlk.get(i);
            long ciphertext = E.encrypt(plaintext, K.getKey(i));
            ptrToBlk.set(i, ciphertext);
        }
        return dataBlk.OutputToString();
    }
    
    public String Decrypt() throws UnsupportedEncodingException {
        int i;
        TriadPrimusCipher D = new TriadPrimusCipher(PRIMUS_ROUNDS);
        PrimusKey K = new PrimusKey(key,dataBlk.getDataBlock().size());
        for(i=0; i < ptrToBlk.size(); i++) {
            long ciphertext = ptrToBlk.get(i);
            long plaintext = D.decrypt(ciphertext, K.getKey(i));
            ptrToBlk.set(i, plaintext);
        }
        return dataBlk.OutputToString();
    }
    
}
