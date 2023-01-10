package com.shanyutu.mysql;

import com.shanyutu.utils.createData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.shanyutu.utils.createData.*;


@SpringBootTest
class MysqlApplicationTests {

    //生成数量
    private int createDataRowsNum = 10000 ;
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


    //导入数据库
    @Test
    void LoadIntoDB() throws SQLException, IOException {

        Files.lines(Paths.get("test.txt"), Charset.defaultCharset()).forEach(line -> {
            System.out.println(line);
            //convertToDB(line);
        });

        System.out.println(dataSource.getClass());

        Connection connection = dataSource.getConnection();

        System.out.println(connection);
        connection.close();
    }

    private long begin = 1;//起始id
    private long end = begin+300;//每次循环插入的数据量

    @Value("${spring.datasource.url}")
    private String url ;
    @Value("${spring.datasource.username}")
    private String username ;
    @Value("${spring.datasource.password}")
    private String password ;

    @Test
    public void insertBigData() {
        //定义连接、statement对象
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            //加载jdbc驱动
            Class.forName("com.mysql.jdbc.Driver");
            //连接mysql
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            //编写sql
            String sql = "INSERT INTO test VALUES (?,?,?,?,?)";
            //预编译sql
            pstm = conn.prepareStatement(sql);
            //开始总计时
            long bTime1 = System.currentTimeMillis();

            List<String> lines = Files.readAllLines(Paths.get("test.txt"));
            //开启分段计时，计1W数据耗时
            long bTime = System.currentTimeMillis();
            for (int i = 0 ; i < lines.size() ; ++i) {
                int now = 1 ;
                for (String retval: lines.get(i).split("\\|!")) {
                    pstm.setString(now, retval);
                    now += 1 ;
                }
                pstm.addBatch();

                begin++;
                if (begin == end) {
                    pstm.executeBatch();
                    conn.commit();
                    end += 300;
                    //关闭分段计时
                    long eTime = System.currentTimeMillis();
                    //输出
                    System.out.println("成功插入300条数据耗时："+(eTime-bTime));
                    bTime = System.currentTimeMillis();
                }
            }
            //关闭总计时
            long eTime1 = System.currentTimeMillis();
            //输出
            System.out.println("插入3000数据共耗时："+(eTime1-bTime1));
            return ;
            //}) ;


            ////循环10次，每次一万数据，一共10万
            //for(int i=0;i<10;i++) {
            //    //将自动提交关闭
            //    conn.setAutoCommit(false);
            //    //开启分段计时，计1W数据耗时
            //    long bTime = System.currentTimeMillis();
            //    //开始循环
            //    while (begin < end) {
            //        //赋值
            //        pstm.setString(1, createData.getChineseName());
            //        pstm.setString(2, createData.name_sex);
            //        pstm.setString(3, createData.getEmail(4, 15));
            //        pstm.setString(4, createData.getTel());
            //        pstm.setString(5, createData.getRoad());
            //        //添加到同一个批处理中
            //        pstm.addBatch();
            //        begin++;
            //    }
            //    //执行批处理
            //    pstm.executeBatch();
            //    //提交事务
            //    conn.commit();
            //    //边界值自增10W
            //    end += 300;
            //    //关闭分段计时
            //    long eTime = System.currentTimeMillis();
            //    //输出
            //    System.out.println("成功插入300条数据耗时："+(eTime-bTime));
            //}
            ////关闭总计时
            //long eTime1 = System.currentTimeMillis();
            ////输出
            //System.out.println("插入3000数据共耗时："+(eTime1-bTime1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @Test
    public void insertBigData3() {
        //定义连接、statement对象
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            //加载jdbc驱动
            Class.forName("com.mysql.jdbc.Driver");
            //连接mysql
            conn = DriverManager.getConnection(url, username, password);
            //将自动提交关闭
            conn.setAutoCommit(false);
            //编写sql
            String sql = "INSERT INTO person VALUES (?,?,?,?,?,?,?)";
            //预编译sql
            pstm = conn.prepareStatement(sql);
            //开始总计时
            long bTime1 = System.currentTimeMillis();

            Files.lines(Paths.get("test.txt"), Charset.defaultCharset()).forEach(line -> {
                System.out.println(line);

                for (String retval: line.split("|!")){
                    System.out.println(retval);
                }

            });

            ////循环10次，每次一万数据，一共10万
            //for(int i=0;i<10;i++) {
            //    //开启分段计时，计1W数据耗时
            //    long bTime = System.currentTimeMillis();
            //    //开始循环
            //    while (begin < end) {
            //        //赋值
            //        pstm.setLong(1, begin);
            //        pstm.setString(2, RandomValue.getChineseName());
            //        pstm.setString(3, RandomValue.name_sex);
            //        pstm.setInt(4, RandomValue.getNum(1, 100));
            //        pstm.setString(5, RandomValue.getEmail(4, 15));
            //        pstm.setString(6, RandomValue.getTel());
            //        pstm.setString(7, RandomValue.getRoad());
            //        //执行sql
            //        pstm.execute();
            //        begin++;
            //    }
            //    //提交事务
            //    conn.commit();
            //    //边界值自增10W
            //    end += 10000;
            //    //关闭分段计时
            //    long eTime = System.currentTimeMillis();
            //    //输出
            //    System.out.println("成功插入1W条数据耗时："+(eTime-bTime));
            //}
            ////关闭总计时
            //long eTime1 = System.currentTimeMillis();
            ////输出
            //System.out.println("插入10W数据共耗时："+(eTime1-bTime1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
