

1.需要提前安装的软件
* Java Platform (JDK) >= 8，并且配置环境变量
* Apache Maven 3.0.5;  并且配置环境变量
* PostgreSQL >= 9.5;  官网下载地址https://www.postgresql.org/download/windows/。建议下载installer，比较方便
	配置超级用户postgres的密码为"zenvisage"
* Tomcat

2.配置zenvisage

* clone本项目代码
	
	git clone git@gitee.com:DSM_fudan/VisualTSD.git
              
* 配置postgresql数据库
        
	在postgresql\bin下打开cmd命令行：
		psql -U postgres
        DROP schema public cascade; 
		CREATE schema public; 
		CREATE TABLE zenvisage_metadata (database TEXT, x_max INT, y_min REAL, y_max REAL, numofdata INT);
		\q(退出)

3.运行
	
	方法1，直接编译运行：
		* Build and deploy code. 
        
          sh build.sh
        
		* Run：
            
          sh run.sh
		 
		 * 打开 http://localhost:8080/
	方法2，在eclipse或idea里配置tomcat运行:
		1.import an existing Maven project.
		2.配置tomcat
		3.run in tomcat.
        
  


License
----

MIT


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [prof]: http://web.engr.illinois.edu/~adityagp/#
   [zenvisage-website]: http://zenvisage.github.io/
   [zenvisage-vldb]: http://data-people.cs.illinois.edu/papers/zenvisage-vldb.pdf
   [zenvisage-cidr]: http://data-people.cs.illinois.edu/papers/zenvisage-cidr.pdf
   [postgressite]: https://www.postgresql.org/
   [postgres-installation]: https://chartio.com/resources/tutorials/how-to-start-postgresql-server-on-mac-os-x/
   <sup>1</sup>The smart-fuse optimization algorithms are not part of this release. Instead, we employ a simpler optimization scheme that works well for all but the most complex queries. 
