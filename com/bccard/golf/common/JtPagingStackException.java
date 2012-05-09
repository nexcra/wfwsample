/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JtPagingStackException
*   �ۼ���    : (��)�̵������ ������
*   ����      : �⺻ Exception
*   �������  : golf
*   �ۼ�����  : 2009-04-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*	2008.11.28	2008.12.03	2008.12.03	2008.12.03	hklee	����� ���� ���� ����¡ ���� ����
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.PrintWriter;

/** ****************************************************************************
 * �⺻ Exception.
 * Exception �� ���� �����̳ʷν� �۵��� �� �ִ�.
 * @version   1.0
 * @author    2003 7 31 <A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class JtPagingStackException extends Exception {
    /** ���� Throwable */
    protected Throwable rootCause = null;
    /** ���� BaseExcetion ��� */
    private List exceptionList = new ArrayList();
    /** �޽��� Ű */
    private String key = null;
    /** �޽��� ���� �� */
    private Object[] values = null;

    /** *****************************************************************
     * �⺻ Constructor.
     ***************************************************************** */
    public JtPagingStackException() {
        super();
    }

    /** *****************************************************************
     * �޽��� �Է� Constructor.
     ***************************************************************** */
    public JtPagingStackException(String s) {
        super(s);
    }

    /** *****************************************************************
     * �޽��� �� Throwable �Է� Constructor.
     ***************************************************************** */
    public JtPagingStackException(String s, Throwable rootCause) {
        super(s);
        this.rootCause = rootCause;
    }

    /** *****************************************************************
     * �޽���Ű �� ���ΰ� �Է� Constructor.
     ***************************************************************** */
    public JtPagingStackException(String key, Object[] values) {
        super();
        this.key = key;
        this.values = values;
    }

    /** *****************************************************************
     * �޽���Ű �� ���ΰ� �� Throwable �Է� Constructor.
     ***************************************************************** */
    public JtPagingStackException(String key, Object[] values, Throwable rootCause) {
        super();
        this.rootCause = rootCause;
        this.key = key;
        this.values = values;
    }

    /** *****************************************************************
     * Throwable �Է� Constructor.
     ***************************************************************** */
    public JtPagingStackException(Throwable rootCause) {
        super();
        this.rootCause = rootCause;
    }

    /** *****************************************************************
     * RootCause �Է�.
     * @param anException �߻��� Exception
     ***************************************************************** */
    public void setRootCause(Throwable anException) { this.rootCause = anException; }

    /** *****************************************************************
     * RootCause ���.
     * @return �߻��ߴ� Exception
     ***************************************************************** */
    public Throwable getRootCause() { return this.rootCause; }

    /** *****************************************************************
     * ���� JtPagingStackException �߰�
     * @param ex 
     ***************************************************************** */
    public void addException(JtPagingStackException ex) { this.exceptionList.add(ex); }

    /** *****************************************************************
     * ���� JtPagingStackException ��� ��ȯ.
     * @return JtPagingStackException�� ���� List 
     ***************************************************************** */
    public List getExceptions() { return this.exceptionList; }

    /** *****************************************************************
     * �޽��� Ű ���.
     * @param key �޽���Ű
     ***************************************************************** */
    public void setKey(String key) { this.key = key; }

    /** *****************************************************************
     * �޽��� Ű ��ȯ.
     * @return �޽���Ű
     ***************************************************************** */
    public String getKey() { return this.key; }

    /** *****************************************************************
     * �޽��� ���� �� ���.
     * @param values �޽��� ���� �� �迭
     ***************************************************************** */
    public void setValues(Object[] values) { this.values = values; }

    /** *****************************************************************
     * �޽��� ���� �� ��ȯ.
     * @return �޽��� ���� �� �迭.
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

