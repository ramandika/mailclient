/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fsck.k9.crypto.TriadPrimus;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class PrimusKey {
    String key;
    int blkCount;
    ArrayList<Long> keyList;
    public PrimusKey(String key, int blkCount) {
       this.key = key;
       this.blkCount = blkCount;
       keyList = new ArrayList();
       expandKey();
    }
    
    private void expandKey() {
        int targetLen = blkCount * 8;
        int currentKeyLen = key.length();
        String newKey = key;
        int i,j;
        j = 0;
        if(currentKeyLen < targetLen) {
            for(i=currentKeyLen; i < targetLen; i++) {
                if(j == currentKeyLen) {
                    j = 0;
                }
                newKey += newKey.charAt(j);
                j++;
            }
            for (i = 0; i < newKey.length(); i += 8) {
                long k = 0L;
                for(j = 0; j < 8; j++) {
                    k |= (long)newKey.charAt(i+j) << (64-8*(j+1));
                }
                keyList.add(k);
            }
        } else {
            //truncate and fit
            for(i = 0; i < targetLen; i+=8) {
                long k = 0L;
                for(j = 0; j < 8; j++) {
                    k |= (long)newKey.charAt(i+j) << (64-8*(j+1));
                }
                keyList.add(k);
            }
        }
    }
    
    public long getKey(int blkNum) {
        return keyList.get(blkNum);
    }
    
}
