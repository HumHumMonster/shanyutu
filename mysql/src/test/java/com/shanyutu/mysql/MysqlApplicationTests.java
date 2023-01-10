package com.shanyutu.mysql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import static com.shanyutu.utils.createData.*;


@SpringBootTest
class MysqlApplicationTests {

    //生成数量
    private int createDataRowsNum = 30000000 ;
    //生成文件
    @Test
    void createDataTxt() throws IOException {

        BufferedWriter bw = null;

        bw = Files.newBufferedWriter(Paths.get("test.txt"), StandardCharsets.UTF_8);

        for (int i = 0 ; i < createDataRowsNum ; ++i) {
            if (i % 100000 == 0) {
                System.out.println(i);
            }
            bw.write(getChineseName());//姓名
            bw.write("|!") ;
            bw.write(name_sex);//性别
            bw.write("|!") ;
            bw.write(getRoad());//住址
            bw.write("|!") ;
            bw.write(getTel());//电话
            bw.write("|!") ;
            bw.write(getEmail(6 , 9));//邮箱
            bw.newLine();

        }

        bw.flush();
        bw.close();

        return ;
    }

    // 自动装配数据源
    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getClass());

        Connection connection = dataSource.getConnection();

        System.out.println(connection);
        connection.close();
    }


    //生成数据
    @Test
    void createData() throws IOException {


        BufferedWriter bw = null;

        bw = Files.newBufferedWriter(Paths.get("test.txt"), StandardCharsets.UTF_8);

        bw.write("hello");//写入单个字符
        bw.newLine();
        bw.write("world");
        bw.newLine();
        bw.write("!!!");
        bw.newLine();
        bw.flush();
        bw.close();

    }

}
