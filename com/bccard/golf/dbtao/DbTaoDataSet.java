/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� :  2004.08.02 [���뱹(ykcho@e4net.net)]
* ���� : DbTao ó���� ������ �Ѱ��ֱ� ���� Ŭ����
* �������  : welco
* �ۼ�����  : 2007.01.04
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;


/******************************************************************************
 * DbTao ó���� ������ �Ѱ��ֱ� ���� Ŭ����.
 * @author ���뱹
* �ۼ�����  : 2007.01.04
 **************************************************************************** */
public class DbTaoDataSet implements TaoDataSet,Serializable {
    /** ������ ����� */ private Map map;
    /** �޽��� ����   */ private final String TITLE;

    /** ***********************************************************************
    * DbTao ó���� ������ �Ѱ��ֱ� ���� Ŭ����.
    ************************************************************************ */
    public DbTaoDataSet(String title) {
        this.map = new HashMap();
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
     * �ʵ�� Set ����.
     * @return �ʵ�� Set
     ***************************************************************** */
    public Set getFieldSet() throws TaoException {
        return this.map.keySet();
    }

    /** *****************************************************************
     * Object ��ȯ.
     * @return Object
     ***************************************************************** */
    public Object getObject(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            return this.map.get(field);
        } else {
            return null;
        }
    }

    /** *****************************************************************
     * String ��ȯ.
     * @return String
     ***************************************************************** */
    public String getString(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                return (String) this.map.get(field);
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not String!",t);
            }
        } else {
            return null;
        }
    }

    /** *****************************************************************
     * int ��ȯ.
     * @return int
     ***************************************************************** */
    public int getInt(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.intValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not int!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * long ��ȯ.
     * @return long
     ***************************************************************** */
    public long getLong(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.longValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not long!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * short ��ȯ.
     * @return short
     ***************************************************************** */
    public short getShort(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.shortValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not short!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * float ��ȯ.
     * @return float
     ***************************************************************** */
    public float getFloat(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.floatValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not float!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * double ��ȯ.
     * @return double
     ***************************************************************** */
    public double getDouble(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.doubleValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not double!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * boolean ��ȯ.
     * @return boolean
     ***************************************************************** */
    public boolean getBoolean(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Boolean value = (Boolean)this.map.get(field);
                return value.booleanValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not boolean!",t);
            }
        } else {
            return false;
        }
    }

    /** *****************************************************************
     * Object �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setObject(String field, Object value) throws TaoException {
        this.map.put(field,value);
    }

    /** *****************************************************************
     * Object �Է�.
     * @param field �ʵ��
     * @param ins_list �ʵ尪
     ***************************************************************** */
    public void setString(String field, ArrayList ins_list) throws TaoException {
        this.map.put(field,ins_list);
    }
    /** *****************************************************************
     * Object �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setString(String field, String value) throws TaoException {
        this.map.put(field,value);
    }

    /** *****************************************************************
     * int �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setInt(String field,int value) throws TaoException {
        this.map.put(field, new Integer(value) );
    }

    /** *****************************************************************
     * long �Է�.
     * @param field �ʵ��
     * @param del_list �ʵ尪
     ***************************************************************** */
    public void setLong(String field, long del_list) throws TaoException {
        this.map.put(field, new Long(del_list) );
    }

    /** *****************************************************************
     * short �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setShort(String field, short value) throws TaoException {
        this.map.put(field, new Short(value) );
    }

    /** *****************************************************************
     * float �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setFloat(String field, float value) throws TaoException {
        this.map.put(field, new Float(value) );
    }

    /** *****************************************************************
     * double �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setDouble(String field, double value) throws TaoException {
        this.map.put(field, new Double(value) );
    }

    /** *****************************************************************
     * boolean �Է�.
     * @param field �ʵ��
     * @param value �ʵ尪
     ***************************************************************** */
    public void setBoolean(String field, boolean value) throws TaoException {
        this.map.put(field, new Boolean(value) );
    }
}
