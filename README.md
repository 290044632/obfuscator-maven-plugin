# obfuscator-maven-plugin

# 插件简介

一款基于maven plugin机制的Jar包文件混淆工具。

本插件工具在开源项目[obfuscator](https://github.com/superblaubeere27/obfuscator/tree/master)基础上构建而成，了解[obfuscator](https://github.com/superblaubeere27/obfuscator/tree/master)请访问：[https://github.com/superblaubeere27/obfuscator/tree/master](https://github.com/superblaubeere27/obfuscator/tree/master)。

# 依赖环境

JDK 8+，Maven 3.5.X+

开发者环境：jdk1.8.0_271，apache-maven-3.6.3

# 使用方式

1.指定maven命令生命周期自动执行。

如下配置将会在package生命周期执行，执行mvn package/install命令均会触发；

```
<plugin>
    <groupId>com.pegg.maven.plugin</groupId>
    <artifactId>obfuscator-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
		<execution>
			<phase>package</phase>
			<goals>
				<goal>obfus</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```
2.命令执行：mvn obfuscator:obfus 。

```
<plugin>
    <groupId>com.pegg.maven.plugin</groupId>
    <artifactId>obfuscator-maven-plugin</artifactId>
    <version>1.0.0</version>
</plugin>
```