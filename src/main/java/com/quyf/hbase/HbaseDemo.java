package com.quyf.hbase;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 * hbase概念http://www.cnblogs.com/zlslch/p/5956699.html
 * http://www.cnblogs.com/zlslch/category/922188.html
 */

public class HbaseDemo {

    private static Connection conn;
    private static Configuration configuration;
    private static Admin admin;
    public static void main(String[] args) throws Exception{
        //System.out.println("hello world");


    }

    @Before
    public void setup() throws Exception{
        configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum", "bg1:2181,bg2:2181,bg3:2181");
        // configuration.addResource(new Path("I:\\gitproject\\hbase_demo\\src\\main\\resources\\hbase-site.xml"));
        conn = ConnectionFactory.createConnection(configuration);
        admin = conn.getAdmin();
    }
    /**
     * 结束之后 关闭对象
     */
    @After
    public void End_up() throws Exception {
        if (conn != null) conn.close();
    }
    @Test
    public void create() throws IOException {
        String[] cols = {"phone","addr","age"};
        createTable("helloworld", cols);
    }

    /**
     * https://blog.csdn.net/qq_28048615/article/details/79838676
     * @param tableName
     * @param columns
     * @throws IOException
     */
    private void createTable(String tableName, String[] columns) throws IOException {

        TableName tb = TableName.valueOf( tableName );
        if( admin.tableExists( tb )){
            System.out.println("已经存在");
        }else{
            System.out.println("not存在");

            HTableDescriptor desc = new HTableDescriptor( tb );
            for (String column:columns) {
                desc.addFamily( new HColumnDescriptor(column));
            }
            admin.createTable(desc);
            System.out.println("create "+tableName+" success");
        }
    }

    @Test
    public void showTable() throws IOException {

        TableName[] tables = admin.listTableNames();
        for(TableName tab:tables){
            System.out.println("表名："+tab.getNameAsString());
            HTableDescriptor desc = admin.getTableDescriptor( tab );
            for(HColumnDescriptor column: desc.getColumnFamilies()){
                System.out.println(column.getNameAsString()+"==="+column.toString());
            }
        }
    }

    @Test
    public void getByRowKey() throws IOException {
        //admin.getTableDescriptor(TableName.valueOf("test"));
        Table table = conn.getTable(TableName.valueOf("test"));
        Get get = new Get("0004".getBytes());
        Result rt = table.get(get);
        System.out.println( rt.toString());
        for(Cell cell:rt.rawCells()){
            System.out.println(
                    Bytes.toString(CellUtil.cloneRow(cell))+"\t"+
                            Bytes.toString( CellUtil.cloneFamily(cell))+"\t"+
                            Bytes.toString(CellUtil.cloneQualifier(cell))+"\t"+
                            Bytes.toString( CellUtil.cloneValue(cell))
            );
        }
    }

    @Test
    public void put() throws IOException {
        Table tab = conn.getTable(TableName.valueOf("test"));
        Put put1 = new Put("0004".getBytes());
        put1.addColumn("info".getBytes(),"age".getBytes(),Bytes.toBytes("4"));
        //put1.addColumn("info".getBytes(),"username".getBytes(),Bytes.toBytes("hellooww"));

        Put put2 = new Put("0005".getBytes());
        put2.addColumn("info".getBytes(),"age".getBytes(),Bytes.toBytes("45"));
       // put2.addColumn("info".getBytes(),"username".getBytes(),Bytes.toBytes("alibaba"));

        List<Put> list = new ArrayList<Put>();
        list.add(put1);
        list.add(put2);
        tab.put(list);
    }

    @Test
    public void deletByRowKey() throws IOException {
        Table table = conn.getTable(TableName.valueOf("test"));
        Delete delete = new Delete(Bytes.toBytes("0004"));
        //如果age列有多个版本的话，这里只删除了最新的一个版本，其他版本的数据还在的，
        //　addColumn是删除某一个列簇里的最新时间戳版本。
        // delete.addColumns是删除某个列簇里的所有时间戳版本。
        //delete.addColumn("info".getBytes(),"age".getBytes());

        //测试下删除不存在的列
       // delete.addColumn("info".getBytes(),"hh".getBytes());
        //测试下删除不存在的列簇 会报错 column family info22 does not exist in region test
        delete.addColumn("info22".getBytes(),"hh".getBytes());
        table.delete(delete);
        table.close();
    }

    @Test
    public void scanTable() throws IOException {
        Table table = conn.getTable(TableName.valueOf("test"));
        Scan scan = new Scan();
        //scan可以加很多条件和范围
        scan.withStartRow("0002".getBytes());
        //scan.withStopRow("0004".getBytes());//不包含末尾行
        scan.withStopRow("0004".getBytes(),true);//包含末尾行
        ResultScanner rtScan = table.getScanner( scan );
//        Iterator itor = rtScan.iterator();
//        while(itor.hasNext()){
//            System.out.println( itor.next().toString());
//        }
        for (org.apache.hadoop.hbase.client.Result next = rtScan.next();next !=null;next = rtScan.next() ){
            System.out.println(next.toString());
        }
    }
}
