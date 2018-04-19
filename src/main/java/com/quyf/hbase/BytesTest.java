package com.quyf.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by Administrator on 2018/4/10.
 */
public class BytesTest {
    public static void main(String[] args) {
        byte[] a = Bytes.toBytes("a");
        byte[] b = Bytes.toBytes("b");

        byte[] c = Bytes.add(a,b);
        System.out.println( new String(c));
        System.out.println( Bytes.toString(c));
    }
}
