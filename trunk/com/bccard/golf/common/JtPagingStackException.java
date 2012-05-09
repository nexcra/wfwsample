/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JtPagingStackException
*   작성자    : (주)미디어포스 진현구
*   내용      : 기본 Exception
*   적용범위  : golf
*   작성일자  : 2009-04-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*	2008.11.28	2008.12.03	2008.12.03	2008.12.03	hklee	모바일 적용 관련 페이징 변경 적용
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.PrintWriter;

/** ****************************************************************************
 * 기본 Exception.
 * Exception 을 담은 컨테이너로써 작동할 수 있다.
 * @version   1.0
 * @author    2003 7 31 <A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class JtPagingStackException extends Exception {
    /** 원본 Throwable */
    protected Throwable rootCause = null;
    /** 서브 BaseExcetion 목록 */
    private List exceptionList = new ArrayList();
    /** 메시지 키 */
    private String key = null;
    /** 메시지 매핑 값 */
    private Object[] values = null;

    /** *****************************************************************
     * 기본 Constructor.
     ***************************************************************** */
    public JtPagingStackException() {
        super();
    }

    /** *****************************************************************
     * 메시지 입력 Constructor.
     ***************************************************************** */
    public JtPagingStackException(String s) {
        super(s);
    }

    /** *****************************************************************
     * 메시지 및 Throwable 입력 Constructor.
     ***************************************************************** */
    public JtPagingStackException(String s, Throwable rootCause) {
        super(s);
        this.rootCause = rootCause;
    }

    /** *****************************************************************
     * 메시지키 및 매핑값 입력 Constructor.
     ***************************************************************** */
    public JtPagingStackException(String key, Object[] values) {
        super();
        this.key = key;
        this.values = values;
    }

    /** *****************************************************************
     * 메시지키 및 매핑값 및 Throwable 입력 Constructor.
     ***************************************************************** */
    public JtPagingStackException(String key, Object[] values, Throwable rootCause) {
        super();
        this.rootCause = rootCause;
        this.key = key;
        this.values = values;
    }

    /** *****************************************************************
     * Throwable 입력 Constructor.
     ***************************************************************** */
    public JtPagingStackException(Throwable rootCause) {
        super();
        this.rootCause = rootCause;
    }

    /** *****************************************************************
     * RootCause 입력.
     * @param anException 발생한 Exception
     ***************************************************************** */
    public void setRootCause(Throwable anException) { this.rootCause = anException; }

    /** *****************************************************************
     * RootCause 출력.
     * @return 발생했던 Exception
     ***************************************************************** */
    public Throwable getRootCause() { return this.rootCause; }

    /** *****************************************************************
     * 서브 JtPagingStackException 추가
     * @param ex 
     ***************************************************************** */
    public void addException(JtPagingStackException ex) { this.exceptionList.add(ex); }

    /** *****************************************************************
     * 서브 JtPagingStackException 목록 반환.
     * @return JtPagingStackException을 담은 List 
     ***************************************************************** */
    public List getExceptions() { return this.exceptionList; }

    /** *****************************************************************
     * 메시지 키 등록.
     * @param key 메시지키
     ***************************************************************** */
    public void setKey(String key) { this.key = key; }

    /** *****************************************************************
     * 메시지 키 반환.
     * @return 메시지키
     ***************************************************************** */
    public String getKey() { return this.key; }

    /** *****************************************************************
     * 메시지 매핑 값 등록.
     * @param values 메시지 매핑 값 배열
     ***************************************************************** */
    public void setValues(Object[] values) { this.values = values; }

    /** *****************************************************************
     * 메시지 매핑 값 반환.
     * @return 메시지 매핑 값 배열.
     ***************************************************************** */
    public Object[] getValues() { return this.values; }

    /** *****************************************************************
     * printStackTrace.
     ***************************************************************** */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /** *****************************************************************
     * printStackTrace.
     * @param outStream PrintStream
     ***************************************************************** */
    public void printStackTrace(PrintStream outStream) {
        printStackTrace(new PrintWriter(outStream));
    }

    /** *****************************************************************
     * printStackTrace.
     * @param writer PrintWriter
     ***************************************************************** */
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if ( getRootCause() != null ) {
            getRootCause().printStackTrace(writer);
        }
        writer.flush();
    }


}

