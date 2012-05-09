DEV_HOME=/BCWEB/WAS/bcext/golfloung

CLASS_ROOT=$(DEV_HOME)/WEB-INF/classes

LOCAL_LIB=$(DEV_HOME)/WEB-INF/lib

VPATH=$(CLASS_ROOT)/com/bccard/golf/action/admin/tm_member

CLASSPATH=.:$(LOCAL_LIB):/bccard/bea/wls81/server/lib/weblogic.jar:$(LOCAL_LIB)/ojdbc14.jar:$(LOCAL_LIB)/BCWAF_1.5_JDK1.4.jar:$(LOCAL_LIB)/json.jar:$(CLASS_ROOT):$(LOCAL_LIB)/log4j-1.2.8.jar:/BCWEB/SOLUTION/initech/lib/INICrypto4Java.jar:/BCWEB/SOLUTION/initech/dbprotector/lib/dbprotector.jar:/BCWEB/SOLUTION/initech/lib/IniPlugin.jar:/BCWEB/SOLUTION/NPKI:/BCWEB/SOLUTION/NPKI/crosscert:

JAVAC=javac

OBJECTS= \
	GolfLoungTMProc.class \
	GolfLoungTMFeeMemberJoinActn.class \
  
.SUFFIXES: .java .class

.java.class:
	$(JAVAC) -d $(CLASS_ROOT) -classpath $(CLASSPATH) $<

all: $(OBJECTS)

clean  : 
	rm -rf $(VPATH)/*.class
