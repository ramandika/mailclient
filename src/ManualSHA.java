/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mailclient;

/**
 *
 * @author ACER
 */
public class ManualSHA{
    /*
    SHA1CircularShift
    @TODO bitwise rotate 32-bit number ke kiri
    */
    private static int SHA1CircularShift(int bits,int word){ 
    return (bits << word) | (bits >> (32 - word));
    }
    /*
    SHACode
    @TODO convert string to 16-word blocks as array
    append padding bits and the length.
    */
    public static String SHACode(String input){
       byte[] temp = input.getBytes();
       int[] block = new int[(((temp.length + 8) >> 6 ) + 1) *16];
       int i,j,k;
       
       for (i = 0; i<temp.length; i++){
           block[i>>2] |= temp[i] << (24 - (i % 4) * 8);
       }
       block[i>>2] |= 0x80 << (24 - (i % 4) *8);
       block[block.length - 1] = temp.length * 8;
       
       int[] split = new int[80];
       
       int a =  1732584193;
       int b = -271733879;
       int c = -1732584194;
       int d =  271733878;
       int e = -1009589776;
       
       for(i = 0; i < block.length; i += 16) {
       int preva = a;
       int prevb = b;
       int prevc = c;
       int prevd = d;
       int preve = e;
        for (j = 0; j < block.length; j+= 16){
           if ((j < 16)){
               split[j] = block[i+j];
           } else {
               split[j] = ( SHA1CircularShift(split[j-3] ^ split[j-8] ^ split[j-14] ^ split[j-16], 1) );
           }
           
        if (j < 20){
           k = 1518500249 + ((b & c) | ((~b) & d));
        } else if (j < 40){
           k = 1859775393 + (b ^ c ^ d);
        } else if (j < 60){
           k = -1894007588 + ((b & c) | (b & d) | (c & d));
        } else{
           k = -899497514 + (b ^ c ^ d);
        }
       
           
        int t = SHA1CircularShift(a, 5) + e + split[j] +k;
                e = d;
                d = c;
                c = SHA1CircularShift(b, 30);
                b = a;
                a = t;
              }
        a = a + preva;
        b = b + prevb;
        c = c + prevc;
        d = d + prevd;
        e = e + preve; 
            }
       int[] words = {a,b,c,d,e,0};
       byte[] base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
       byte[] result = new byte[28];
       for (i=0; i<27; i++) {
         int start=i*6;
         int word=start>>5;
         int offset=start & 0x1f;

             if (offset <= 26) {
                 result[i] = base64[(words[word] >> (26 - offset)) & 0x3F];
             } else if (offset == 28) {
                 result[i] = base64[(((words[word] & 0x0F) << 2) |
                                    ((words[word+1] >> 30) & 0x03)) & 0x3F];
             } else {
                 result[i] = base64[(((words[word] & 0x03) << 4) |
                                    ((words[word+1] >> 28) & 0x0F)) & 0x3F];
             }
          }
          result[27]='=';

          return new String(result);
        }
    //testmain
           public static void main(String args[]) {
         System.out.println(SHACode("Hello"));
     }
    }




