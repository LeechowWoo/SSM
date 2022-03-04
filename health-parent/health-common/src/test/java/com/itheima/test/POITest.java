package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class POITest {
    //测试使用POI来读取excel文件中的数据
    @Test
    public void test01() throws IOException {
        //加载指定文件，来创建一个Excel对象(工作簿)
        XSSFWorkbook excel=new XSSFWorkbook(new FileInputStream(new File("D:\\poi.xlsx")));
        //读取excel文件中的第一个sheet标签页
        XSSFSheet sheet = excel.getSheetAt(0);
        //遍历拿到的sheet页，获取每一行的数据
        for (Row row : sheet) {
            //遍历行，获得每个单元格对象
            for (Cell cell : row) {
                //cell代表一个单元格
                System.out.println(cell.getStringCellValue());
            }
        }
        //关闭资源
        excel.close();
    }

//    @Test
//    public void test02() throws IOException {
//        //加载指定文件，来创建一个Excel对象(工作簿)
//        XSSFWorkbook excel=new XSSFWorkbook(new FileInputStream(new File("D:\\poi.xlsx")));
//        //读取excel文件中的第一个sheet标签页
//        XSSFSheet sheet = excel.getSheetAt(0);
//        int lastRowNum = sheet.getLastRowNum();//获取当前sheet的最后一个行号
//        for(int i=0;i<=lastRowNum;i++){
//            XSSFRow row = sheet.getRow(i);//获取每一行
//            short lastCellNum = row.getLastCellNum();//获取当前行的最后一个单元格的索引
//            for(short j=0;j<lastCellNum;j++){
//                XSSFCell cell = row.getCell(j);//根据单元格索引获取单元格对象
//                System.out.println(cell.getStringCellValue());
//            }
//        }
//        //关闭资源
//        excel.close();
//    }

    //使用POI想excel文件写入数据，并且通过输出流将文件保存在本地磁盘中
    @Test
    public void test03() throws IOException {
        //在内存中先创建一个excel文件
        XSSFWorkbook excel=new XSSFWorkbook();
        //在excel中创建sheet
        XSSFSheet sheet = excel.createSheet("信息");
        //在工作表中创建对象
        XSSFRow title = sheet.createRow(0);
        //在行中创建单元格对象
        title.createCell(0).setCellValue("姓名");
        title.createCell(1).setCellValue("地址");
        title.createCell(2).setCellValue("年龄");

        XSSFRow datarow = sheet.createRow(1);
        datarow.createCell(0).setCellValue("张三");
        datarow.createCell(1).setCellValue("北京");
        datarow.createCell(2).setCellValue("23");

        //创建一个输出流，通过输出流将内存中的excel文件写到磁盘中
        FileOutputStream out = new FileOutputStream(new File("D:\\hello.xlsx"));
        excel.write(out);
        out.flush();
        excel.close();
    }
}
