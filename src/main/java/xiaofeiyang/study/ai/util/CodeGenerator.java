package xiaofeiyang.study.ai.util;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig.Builder(
                "jdbc:mysql://rm-bp1je2q732020418w6o.mysql.rds.aliyuncs.com:3306/xiaofeiyang-ai?useSSL=false&useUnicode=true&characterEncoding=utf8",
                "xiaofeiyang", "xfy@930112")
                .build();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator(dsc);

        // 全局配置
        GlobalConfig gc = new GlobalConfig.Builder()
                .outputDir(System.getProperty("user.dir") + "/src/main/java")
                .author("xiaofeiyang")
                .disableOpenDir()
                .build();
        mpg.global(gc);

        // 包配置
        PackageConfig pc = new PackageConfig.Builder()
                .moduleName("ai")
                .parent("xiaofeiyang.study")
                .serviceImpl("service")
                .pathInfo(Collections.singletonMap(OutputFile.xml,
                        System.getProperty("user.dir") + "\\src\\main\\resources\\mapper"))
                .build();
        mpg.packageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig.Builder()
                .addTableSuffix("DO")
                .addInclude("models") // 表名
                .entityBuilder().enableLombok().enableFileOverride().idType(IdType.AUTO) // 启用Lombok
                .addTableFills()
                .controllerBuilder().enableRestStyle().enableFileOverride() // RestController风格
                .serviceBuilder().enableFileOverride()
                .disableService()
                .convertServiceImplFileName( (entityName) -> {
                    return entityName + "Service";
                })
                .mapperBuilder().enableFileOverride()
                .build();
        mpg.strategy(strategy);

        // 执行生成
        mpg.execute();
    }
}
