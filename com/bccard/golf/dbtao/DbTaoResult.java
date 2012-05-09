/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : DbTao ó���� ����� ��ȯ�ϱ� ���� Ŭ����
* ���� :
* ���� :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;


/******************************************************************************
 * DbTao ó���� ����� ��ȯ�ϱ� ���� Ŭ����.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2007.01.04
 *****************************************************************************/
public class DbTaoResult implements TaoResult,Serializable {
    /** ������ �����        */  private Map map;
    /** ������ �ε���        */  private int index;
    /** ������ ������ �ε��� */  private int cnt;
    /** �޽��� ����          */  private final String TITLE;

    /** ***********************************************************************
    * DbTao ó���� ����� ��ȯ�ϱ� ���� Ŭ����.
    ************************************************************************ */
    public DbTaoResult(String title) {
        this.map = new HashMap();
        this.index = 0;
        this.cnt = 0;
        this.TITLE = title;
    }

    /** ***********************************************************************
    * ���ܸ� �߻��ϱ� ���� �޼���.
    * @ param msg ���ܸ޽���
    * @ param t ���ܰ� �߻��� ���� Throwable
    ************************************************************************ */
    protected DbTaoException errorHandler(String msg,Throwable t) {
        MsgEtt msgEtt = new MsgEtt();
        msgEtt.setType( MsgEtt.TYPE_ERROR );
        msgEtt.setTitle(this.TITLE);
        msgEtt.setMessage( msg );
        if ( t == null ) {
            return new DbTaoException(msgEtt);
        } else {
            return new DbTaoException(msgEtt,t);
        }
    }

    /** *****************************************************************
     * �ִ� size ����.
     ***************************************************************** */
    private void resetCount() {
        for(Iterator it = this.map.keySet().iterator(); it.hasNext(); ) {
            ArrayList list = (ArrayList) this.map.get( it.next() );
            int size = list.size();
            if ( size > this.cnt ) {
                this.cnt = size;
            }
        }
    }

    /**************************************************************************
    * Object ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addObject(String field, Object value) throws TaoException {
        ArrayList list = (ArrayList)this.map.get(field.trim());
        if ( list == null ) {
            list = new ArrayList();
            this.map.put(field.trim(),list);
        }
        list.add(value);
        resetCount();
    }

    /**************************************************************************
    * String ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addString(String field, String value) throws TaoException {
        if ( value == null ) {
            addObject(field,"");
        /**
        } else if ( "null".equalsIgnoreCase(value) ) {
            addObject(field,"");
        */
        } else {
            addObject(field,value);
        }
    }

    /**************************************************************************
    * int ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addInt(String field, int value) throws TaoException {
        addObject(field,new Integer(value));
    }

    /**************************************************************************
    * long ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addLong(String field, long value) throws TaoException {
        addObject(field,new Long(value));
    }

    /**************************************************************************
    * short ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addShort(String field, short value) throws TaoException {
        addObject(field,new Short(value));
    }

    /**************************************************************************
    * float ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addFloat(String field, float value) throws TaoException {
        addObject(field,new Float(value));
    }

    /**************************************************************************
    * double ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addDouble(String field, double value) throws TaoException {
        addObject(field,new Double(value));
    }

    /**************************************************************************
    * boolean ������ �߰�.
    * @ param field �ʵ��
    * @ param value �Էµ�����
    **************************************************************************/
    public void addBoolean(String field, boolean value) throws TaoException {
        addObject(field,new Boolean(value));
    }

    /** *****************************************************************
     * ù ���ڵ�� �̵�.
     ***************************************************************** */
    public void first() throws TaoException {
        this.index = 0;
    }

    /** *****************************************************************
     * ������ ���ڵ�� �̵�.
     ***************************************************************** */
    public void last() throws TaoException {
        if ( this.cnt > 0 ) {
            this.index = this.cnt;
        }
    }

    /** *****************************************************************
     * ���� ���ڵ�� �̵�.
     ***************************************************************** */
    public void next() throws TaoException {
        if ( this.cnt > 0 && this.index < this.cnt ) {
            this.index++;
        } else {
            throw errorHandler("next fail!",null);
        }
    }

    /** *****************************************************************
     * ���� ���ڵ�� �̵�.
     ***************************************************************** */
    public void previous() throws TaoException {
        if ( this.index > 0 ) {
            this.index--;
        } else {
            throw errorHandler("previous fail!",null);
        }
    }

    /** *****************************************************************
     * ó�� ���ڵ����� üũ.
     * @return boolean
     ***************************************************************** */
    public boolean isFirst() throws TaoException {
        return ( this.index == 0 );
    }

    /** *****************************************************************
     * ������ ���ڵ����� üũ.
     * @return boolean
     ***************************************************************** */
    public boolean isLast() throws TaoException {
        return ( this.index == this.cnt );
    }

    /** *****************************************************************
     * ���� ���ڵ尡 �ִ��� üũ.
     * @return boolean
     ***************************************************************** */
    public boolean isNext() throws TaoException {
        return ( this.cnt > 0 && this.index < this.cnt );
    }

    /** *****************************************************************
     * ���� ���ڵ尡 �ִ��� üũ.
     * @return boolean
     ***************************************************************** */
    public boolean isPrevious() throws TaoException {
        return ( this.index > 0 );
    }

    /** *****************************************************************
     * �ʵ尡 �ִ��� üũ.
     * @return boolean
     ***************************************************************** */
    public boolean containsKey(String field) {
        return ( this.map.containsKey(field) );
    }

    /** *****************************************************************
     * ���ڵ� ����.
     * @return int
     ***************************************************************** */
    public int size() throws TaoException {
        return this.cnt;
    }

    /** *****************************************************************
     * Object ��ȯ.
     * @param field �ʵ��
     * @return Object
     ***************************************************************** */
    public Object getObject(String field) throws TaoException {
        if ( this.index == 0 ) throw errorHandler("please run TaoResult.next()",null);
        try {
            ArrayList list = (ArrayList) this.map.get(field);
            if ( this.index <= list.size() ) {
                return list.get(this.index-1);
            } else {
                return list.get(list.size()-1);
            }
        } catch ( Throwable t ) {
            throw errorHandler("field \"" + field + "\" is not found!",t);
        }
    }

    /** *****************************************************************
     * String ��ȯ.
     * @param field �ʵ��
     * @return String
     ***************************************************************** */
    public String getString(String field) throws TaoException {
        Object value = getObject(field);
        try {
            return (String) value;
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not String!",t);
        }
    }

    /** *****************************************************************
     * int ��ȯ.
     * @param field �ʵ��
     * @return int
     ***************************************************************** */
    public int getInt(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.intValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not int!",t);
        }
    }

    /** *****************************************************************
     * long ��ȯ.
     * @param field �ʵ��
     * @return long
     ***************************************************************** */
    public long getLong(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.longValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not long!",t);
        }
    }

    /** *****************************************************************
     * short ��ȯ.
     * @param field �ʵ��
     * @return short
     ***************************************************************** */
    public short getShort(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.shortValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not short!",t);
        }
    }

    /** *****************************************************************
     * float ��ȯ.
     * @param field �ʵ��
     * @return float
     ***************************************************************** */
    public float getFloat(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.floatValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not float!",t);
        }
    }

    /** *****************************************************************
     * double ��ȯ.
     * @param field �ʵ��
     * @return double
     ***************************************************************** */
    public double getDouble(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.doubleValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not double!",t);
        }
    }

    /** *****************************************************************
     * boolean ��ȯ.
     * @param field �ʵ��
     * @return boolean
     ***************************************************************** */
    public boolean getBoolean(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Boolean num = (Boolean) value;
            return num.booleanValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not boolean!",t);
        }
    }

    /** *****************************************************************
     * �ʵ忡 ����ִ� Object ��ȯ(���ڵ���� �������).
     * @param field �ʵ��
     * @return Object
     ***************************************************************** */
    public Object getField(String field) throws TaoException {
        return this.map.get(field);
    }

    /** *****************************************************************
     * �ʵ�� Set ����.
     * @return �ʵ�� Set
     ***************************************************************** */
    public Set getFieldSet() throws TaoException {
        return this.map.keySet();
    }

}
