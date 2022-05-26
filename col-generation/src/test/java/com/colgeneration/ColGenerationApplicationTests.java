package com.colgeneration;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ColGenerationApplicationTests {

    @Value("${spring.datasource.driver-class-name}")
    private String driveName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;

    //��Ҫ���ɵı���
    private static String tableName = "t_seckill_goods";
    //��Ҫ���ɵ�·��
    private static String packageDir = "com.generation";

    @Test
    void contextLoads() {
        //��ȡȫ��·��
        String property = System.getProperty("user.dir");

        // 1. ȫ������
        GlobalConfig config = new GlobalConfig();
        config.setActiveRecord(false) // �Ƿ�֧��ARģʽ  ���¼
                .setAuthor("donggl") // ����
                .setOutputDir(property+"/src/main/java") // ����·��
                .setFileOverride(true) // �ļ�����
                .setIdType(IdType.AUTO) // ��������
                .setServiceName("%sService") // Service ����   XXXService
                .setBaseResultMap(true)
                .setBaseColumnList(true);

        //2. ����Դ����
        DataSourceConfig dsConfig  = new DataSourceConfig();
        dsConfig.setDbType(DbType.MYSQL)  // �������ݿ�����
                .setDriverName(driveName)
                .setUrl(url)
                .setUsername(userName)
                .setPassword(password);

        //3. ��������
        StrategyConfig stConfig = new StrategyConfig();
        stConfig.setCapitalMode(true) //ȫ�ִ�д����
                .setDbColumnUnderline(true)  // ָ������ �ֶ����Ƿ�ʹ���»���
                .setNaming(NamingStrategy.underline_to_camel) // ���ݿ��ӳ�䵽ʵ�����������
                .setColumnNaming(NamingStrategy.underline_to_camel)
//									.setTablePrefix("t_")
                .setInclude(tableName);  // ���ɵı�  Ҫ�������ɵı��ǣ�


        //4. ������������
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent(packageDir)
                .setMapper("mapper")
                .setService("service")
                .setServiceImpl("service.impl")
                .setController("controller")
                .setEntity("entity")
                .setXml("mapper.xml");

        //5. ��������
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config)
                .setDataSource(dsConfig)
                .setStrategy(stConfig)
                .setPackageInfo(pkConfig);
        //6. ִ��
        ag.execute();

    }

}
