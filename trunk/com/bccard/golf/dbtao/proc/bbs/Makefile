WL_HOME=/bccard/bea/wls81/server
GOLF_HOME=/BCWEB/WAS/bcext/golf_new

VPATH=$(GOLF_HOME)/WEB-INF/classes/com/bccard/golf/dbtao/proc/bbs

CLASSPATH=./:/oracle/app/oracle/product/9.2.0/jdbc/lib/ojdbc14.jar:/BCWEB/SOLUTION/initech/lib/INICrypto4Java.jar:/BCWEB/SOLUTION/initech/lib/IniPlugin.jar:/BCWEB/SOLUTION/initech/dbprotector/server.jar:/BCWEB/SOLUTION/VMAN/vman_corp/JAR/VManAppServer.3.1.0.jar:/BCWEB/SOLUTION/VMAN/vman_corp/JAR/jce-jdk13-113.jar:$(WL_HOME)/lib/weblogic.jar:/BCWEB/SOLUTION/BCWAF/lib/joltjse.jar:/BCWEB/SOLUTION/BCWAF/lib/jolt.jar:/BCWEB/SOLUTION/BCWAF/lib/BCWAF_1.5_JDK1.4.jar:/BCWEB/SOLUTION/BCWAF/lib/log4j-1.2.8.jar:$(GOLF_HOME)/WEB-INF/classes:$(GOLF_HOME)/WEB-INF/lib/external-debug.jar:$(GOLF_HOME)/WEB-INF/lib/jakarta-regexp-1.3.jar:$(GOLF_HOME)/WEB-INF/lib/jaxp.jar:$(GOLF_HOME)/WEB-INF/lib/jdom.jar:$(GOLF_HOME)/WEB-INF/lib/jxl.jar:$(GOLF_HOME)/WEB-INF/lib/smartenforcer.jar:/oracle/app/oracle/product/9.2.0/jdbc/lib/classes12.jar:$(GOLF_HOME)/WEB-INF/lib/cos.jar
JAVAC=javac

OBJECTS= \
	GolfMainDetailInqDaoProc.class \
	GolfMainInqCntDaoProc.class \
	GolfMainlnqDaoProc.class \
	GolfMainRepInqDaoProc.class \
	GolfMainSeqnoInqDaoProc.class \
	GolfMainListDaoProc.class \
	GolfMainUpdDaoProc.class \


.SUFFIXES: .java .class

.java.class:
	$(JAVAC) -d $(GOLF_HOME)/WEB-INF/classes -classpath $(CLASSPATH) $<

all: $(OBJECTS)

clean  : 
	rm -rf $(VPATH)/*.class \
