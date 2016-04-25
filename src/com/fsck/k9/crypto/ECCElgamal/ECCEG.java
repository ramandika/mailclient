/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fsck.k9.crypto.ECCElgamal;

import com.fsck.k9.crypto.SHA1;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class ECCEG {
    BigInteger[] equation;
    BigInteger privateKey,prime,order;
    EllipticalCurve.Point basePoint,publicKey;
    EllipticalCurve ecc;
    BigInteger kstatic=new BigInteger("1000");
    private long timeTaken;
    
    public String digitalSignature(String message) throws Exception{
        EllipticalCurve.Point p;
        BigInteger k,r;
        String res=null;
        try{
            do{
                k = Utils.generateK(order);
                System.out.println("k="+k);
                p = ecc.coefMultiply(k, basePoint);
                r = p.getX().mod(order);
                System.out.println("r="+r);
            }while(r.equals(BigInteger.ZERO));
            String hashResult = SHA1.encode(message);
            BigInteger e = IOUtils.getData(hashResult);
            BigInteger s = k.modInverse(order).multiply(e.add(r.multiply(privateKey))).
                    mod(order);
            res = "(["+p.getX()+","+p.getY()+"],"+s+")";   
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    
    public boolean verifyDS(String hashMessage, String ds) throws Exception{
        String s = ds.substring(ds.lastIndexOf(',')+1, ds.length()-1).trim();
        String x = ds.substring(ds.indexOf('[')+1, ds.indexOf(',')).trim();
        String y = ds.substring(ds.indexOf(',')+1, ds.indexOf(']')).trim();
        EllipticalCurve.Point point =new EllipticalCurve.Point(new BigInteger(x), new BigInteger(y));
        BigInteger bigS =new BigInteger(s);
        EllipticalCurve.Point V1 = ecc.coefMultiply(bigS, point);
        System.out.println("V1:"+V1.getX()+" "+V1.getY());
        
        BigInteger hashBig = IOUtils.getData(hashMessage);

        EllipticalCurve.Point temp = ecc.coefMultiply(hashBig,basePoint);
        System.out.println(temp.getX()+" "+temp.getY());
        EllipticalCurve.Point temp2 = ecc.coefMultiply(new BigInteger(x), publicKey);
        EllipticalCurve.Point V2 = ecc.add(temp, temp2);
        System.out.println(V1.getX()+"--------"+V1.getY());
        System.out.println(V2.getX()+"--------"+V2.getY());
        if (V1.getX().equals(V2.getX()) && V1.getY().equals(V2.getY())) return true;
        else return false;
    }
    
    public ECCEG(BigInteger[] equation,BigInteger prime, 
            EllipticalCurve.Point basePoint, BigInteger order){
        this.equation = equation;
        this.prime = prime;
        this.basePoint = basePoint;
        this.order = order;
        ecc = new EllipticalCurve(prime,equation);
    }
    
    public void setPrivateKey(BigInteger privateKey){
        this.privateKey=privateKey;
    }
    
    public void setPublicKey(EllipticalCurve.Point _publicKey){
        publicKey = new EllipticalCurve.Point(_publicKey.getX(), _publicKey.getY());
    }
    
    public long getTimeTakenInMs() {
        return timeTaken;
    }

    public void generatePublicPrivateKeys(){
        privateKey=Utils.generateK(order);
        publicKey= ecc.coefMultiply(privateKey,basePoint);
    }
    public BigInteger getPrivateKey() {
        return privateKey;
    }
    public EllipticalCurve.Point getPublicKey() {
        return publicKey;
    }

    private EllipticalCurve.Point encodeMessage(BigInteger input){
        boolean found=false;
        BigInteger satu=new BigInteger("1");
        BigInteger dua=new BigInteger("2");
        BigInteger empat=new BigInteger("4");
        BigInteger x=null,y=null,iterator;
        iterator=BigInteger.ZERO;
        while(!found){
            iterator=iterator.add(satu);
            x=input.multiply(kstatic).add(iterator).mod(prime);
            BigInteger a= x.pow(3).multiply(ecc.eccEquation[0]).
                    add(x.pow(2).multiply(ecc.eccEquation[1])).
                    add(x.multiply(ecc.eccEquation[2])).
                    add(ecc.eccEquation[3]).mod(prime);
            //System.out.println(ecc.eccEquation[0]+"x^3+"+ecc.eccEquation[1]+"x^2+"+ecc.eccEquation[2]+"x+"+ecc.eccEquation[3]);
            //Find Y
            if(a.modPow(prime.subtract(satu).divide(dua),prime).compareTo(satu)==0){//Ada solusi Y
                y=a.modPow(prime.add(satu).divide(empat),prime);
                found=true;
            }
        }
        return new EllipticalCurve.Point(x,y);
    }

    private BigInteger decodeMessage(EllipticalCurve.Point p, BigInteger k){
        return p.getX().subtract(new BigInteger("1")).divide(k);
    }

    public static class CipherPair implements Serializable{
        EllipticalCurve.Point p1,p2;
        public CipherPair(EllipticalCurve.Point p1,EllipticalCurve.Point p2){this.p1=p1;this.p2=p2;}
        public EllipticalCurve.Point getP1(){return p1;}
        public EllipticalCurve.Point getP2(){return p2;}
    }

    public ArrayList<CipherPair> encrypt(ArrayList<BigInteger> messages) {
        timeTaken = 0;
        long startTime = System.currentTimeMillis();
        ArrayList<CipherPair> result = new ArrayList();
        //Pilih suatu kb [0,P-1]
        //privateKey=Utils.generateK(prime);
        //publicKey= ecc.coefMultiply(privateKey,EllipticalCurve.P192.basePoint);
        //System.out.println("private-key:"+privateKey+"\n"+"public-key:("+publicKey.getX()+","+publicKey.getY()+")");
        //convert each message to point
        System.out.println("Encrypted");
        for(BigInteger m : messages){
            BigInteger k=Utils.generateK(order);
            EllipticalCurve.Point pm = encodeMessage(m);
            //System.out.println(m+"-->"+"("+pm.getX()+","+pm.getY()+")");
            result.add(new CipherPair(ecc.coefMultiply(k,basePoint),ecc.add(pm,ecc.coefMultiply(k,publicKey))));
        }
        long endTime = System.currentTimeMillis();
        timeTaken = endTime-startTime;
        return result;
    }
    
    public ArrayList<BigInteger> decrypt(ArrayList<CipherPair> cipher) {
//        BigInteger k=new BigInteger("20");
//        EllipticalCurve.Point p=new EllipticalCurve.Point(new BigInteger("201"),new BigInteger("228"));
//        BigInteger result=decodeMessage(p,k);
//        System.out.println(result);
        timeTaken = 0;
        long startTime = System.currentTimeMillis();
        ArrayList<BigInteger> result=new ArrayList();
        for(CipherPair c:cipher){
            EllipticalCurve.Point bi=ecc.coefMultiply(privateKey,c.getP1());
            EllipticalCurve.Point m=ecc.substract(c.getP2(),bi);
            //System.out.println("("+m.getX()+","+m.getY()+")");
            result.add(decodeMessage(m,kstatic).mod(prime));
        }
        long endTime = System.currentTimeMillis();
        timeTaken = endTime-startTime;
        return result;
    }

    
}
