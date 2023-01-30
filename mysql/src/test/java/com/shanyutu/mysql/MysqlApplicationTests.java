package com.shanyutu.mysql;

import com.shanyutu.mysql.dao.PersonMapper;
import com.shanyutu.mysql.model.Person;
import com.shanyutu.mysql.utils.RandomValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

import static com.shanyutu.mysql.utils.RandomValue.*;


@SpringBootTest
class MysqlApplicationTests {

    //生成数量
    private int createDataRowsNum = 100000 ;
    //生成文件
    @Test
    void createDataTxt() throws IOException {

        BufferedWriter bw = null;

        bw = Files.newBufferedWriter(Paths.get("test.txt"), StandardCharsets.UTF_8);

        for (int i = 0 ; i < createDataRowsNum ; ++i) {
            if (i % 10000 == 0) {
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
    //每次导入数量
    private long step = 50000 ;
    private long insertTime = 20 ;
    private long end = begin + step ;

    @Test
    public void insertBigData() {
        //定义连接、statement对象
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            //加载jdbc驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("com.mysql.cj.jdbc.Driver");
            //连接mysql
            conn = dataSource.getConnection();
            //编写sql
            String sql = "INSERT INTO test VALUES (?,?,?,?,?)";
            //预编译sql
            pstm = conn.prepareStatement(sql);
            //开始总计时
            long bTime1 = System.currentTimeMillis();

            //循环10次，每次一万数据，一共10万
            for(int i=0;i<insertTime;i++) {
                //将自动提交关闭
                conn.setAutoCommit(false);
                //开启分段计时，计1W数据耗时
                long bTime = System.currentTimeMillis();
                //开始循环
                while (begin < end) {
                    //赋值
                    pstm.setString(1, RandomValue.getChineseName());
                    pstm.setString(2, RandomValue.name_sex);
                    pstm.setString(3, RandomValue.getEmail(4, 15));
                    pstm.setString(4, RandomValue.getTel());
                    pstm.setString(5, RandomValue.getRoad());
                    //添加到同一个批处理中
                    pstm.addBatch();
                    begin++;
                }
                //执行批处理
                pstm.executeBatch();
                //提交事务
                conn.commit();
                pstm.clearBatch() ;
                //边界值自增10W
                end += step;
                //关闭分段计时
                long eTime = System.currentTimeMillis();
                //输出
                System.out.println("成功插入" + step + "条数据耗时："+(eTime-bTime));
            }
            //关闭总计时
            long eTime1 = System.currentTimeMillis();
            //输出
            System.out.println("插入" + step * insertTime + "数据共耗时："+(eTime1-bTime1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void insertBigData2() {
        //定义连接、statement对象
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            //连接mysql
            conn = dataSource.getConnection();
            //编写sql
            String sql = "INSERT INTO test VALUES (?,?,?,?,?)";
            //预编译sql
            pstm = conn.prepareStatement(sql);
            //开始总计时
            long bTime1 = System.currentTimeMillis();

            //循环10次，每次一万数据，一共10万
            for(int i=0;i<10;i++) {
                //将自动提交关闭
                //conn.setAutoCommit(false);
                //开启分段计时，计1W数据耗时
                long bTime = System.currentTimeMillis();
                //开始循环
                while (begin < end) {
                    //赋值
                    pstm.setString(1, RandomValue.getChineseName());
                    pstm.setString(2, RandomValue.name_sex);
                    pstm.setString(3, RandomValue.getEmail(4, 15));
                    pstm.setString(4, RandomValue.getTel());
                    pstm.setString(5, RandomValue.getRoad());
                    //添加到同一个批处理中
                    pstm.addBatch();
                    begin++;
                }
                //执行批处理
                pstm.executeBatch();
                //提交事务
                //conn.commit();
                //边界值自增10W
                end += 10000;
                //关闭分段计时
                long eTime = System.currentTimeMillis();
                //输出
                System.out.println("成功插入10W条数据耗时："+(eTime-bTime));
            }
            //关闭总计时
            long eTime1 = System.currentTimeMillis();
            //输出
            System.out.println("插入100W数据共耗时："+(eTime1-bTime1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
