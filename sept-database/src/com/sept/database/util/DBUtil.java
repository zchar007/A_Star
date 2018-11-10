package com.sept.database.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.sept.framework.exception.AppException;
import com.sept.framework.util.data.DataStore;

import oracle.sql.CLOB;

public class DBUtil {
	public static long dbTime = 0; // 数据库时间
	public static long serverStartTime = 0; // 获取数据库时间的开始时间
	/**
	 * 说明：获取数据库时间
	 * 
	 * @throws SqlException
	 */
	public final static Date getDBDate() throws AppException, SqlException {
		String s = null;
		Sql sql = new Sql();
		if (DatabaseSessionUtil.getDBType() == DatabaseSessionUtil.DBTYPE_ORACLE) {
			sql.setSql("select to_char(sysdate,'yyyy-MM-dd') dbdate from dual");
		} else if (DatabaseSessionUtil.getDBType() == DatabaseSessionUtil.DBTYPE_POSTGRESQL) {
			sql.setSql("select to_char(CURRENT_DATE,'yyyy-MM-dd') dbdate from dual");
		} else {
			throw new AppException("框架不签不能识别数据库类型【"
					+ DatabaseSessionUtil.getDBType() + "】");
		}

		DataStore vds = sql.executeQuery();
		if (vds.rowCount() > 0) {
			s = vds.getString(0, "dbdate");
		}

		return new Date(formatStrToDate(s).getTime());
	}

	/**
	 * 获取数据时间：格式为：yyyy-MM-dd hh:mm:ss
	 * 
	 * @throws SqlException
	 */
	public final static Date getDBTime() throws AppException, SqlException {
		synchronized (refreshDBTime) {
			long currentTime = System.currentTimeMillis();
			if(!"true".equals(GlobalNames.getDeploy("sql", "SPRING-DBABLE"))){
				return new Date();
			}

			if (dbTime == 0
					|| (dbTime != 0 && currentTime - serverStartTime > 60000)) {
				String s = null;
				Sql sql = new Sql();
				if (DatabaseSessionUtil.getDBType() == DatabaseSessionUtil.DBTYPE_ORACLE) {
					sql.setSql("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') dbdate from dual");
				} else if (DatabaseSessionUtil.getDBType() == DatabaseSessionUtil.DBTYPE_POSTGRESQL) {
					sql.setSql("select to_char(CURRENT_TIMESTAMP,'yyyy-mm-dd hh24:mi:ss') dbdate from dual");
				} else {
					throw new AppException("框架不签不能识别数据库类型【"
							+ DatabaseSessionUtil.getDBType() + "】");
				}

				DataStore vds = sql.executeQuery();
				if (vds.rowCount() > 0) {
					s = vds.getString(0, "dbdate");
				}

				dbTime = formatStrToDate(s).getTime();
				serverStartTime = System.currentTimeMillis();
			} else {
				dbTime += currentTime - serverStartTime;
				serverStartTime = currentTime;
			}
			return new Date(dbTime);
		}
	}
	
	/**
	 * 将Clob类型的对象转换成字符串 如果Clob是一个空Clob对象或是本身为空 则转换成null
	 * 
	 * @param clob
	 * @return 如果clob为 null 返回 null
	 * @throws AppException
	 * @since 2.0
	 * @author wf 2009-12-2 13:31:05
	 */
	public final static String clob2string(Clob clob) throws AppException {
		if (clob == null) {
			return null;
		}
		Reader reader;
		try {
			if (clob instanceof ClobProxy) {
				ClobProxy clobProxy = (ClobProxy) clob;
				reader = clobProxy.getRawClob().getCharacterStream();
			} else if (clob instanceof javax.sql.rowset.serial.SerialClob) {
				javax.sql.rowset.serial.SerialClob serialClob = (javax.sql.rowset.serial.SerialClob) clob;
				reader = serialClob.getCharacterStream();
			} else {
				reader = clob.getCharacterStream();
			}
			if (reader != null) {
				BufferedReader br = new BufferedReader(reader);
				String lineString = br.readLine();
				StringBuffer sb = new StringBuffer();
				while (lineString != null) {
					if (sb.length() == 0) {
						sb.append(lineString);
					} else {
						sb.append("\n" + lineString);
					}
					lineString = br.readLine();
				}
				return sb.toString();
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new AppException("从Clob对象中获取字符流输出对象时出现异常!");
		} catch (IOException e) {
			throw new AppException("从Clob对象中读取内容是出现 I/O 异常!");
		}
	}

	/**
	 * 将String类型的长文本内容写入到Clob类型的对象当中
	 * 
	 * @param clobObj
	 *            从数据库中获取的Clob列的对象
	 * @param clobString
	 *            要写入的长文本字符串
	 * @throws AppException
	 */
	public final static void writeString2Clob(Clob clobObj, String clobString)
			throws AppException {
		if (clobObj == null) {// 如果没有获取到
			throw new AppException("在向Clob类型中写入数据时，传入的Clob类型为NULL，属于非法对象!");
		}
		try {
			Writer out = null;
			if (clobObj instanceof ClobProxy) {
				ClobProxy clobProxy = (ClobProxy) clobObj;
				out = clobProxy.getRawClob().setCharacterStream(0L);
			} else if (clobObj instanceof javax.sql.rowset.serial.SerialClob) {
				javax.sql.rowset.serial.SerialClob serialClob = (javax.sql.rowset.serial.SerialClob) clobObj;
				out = serialClob.setCharacterStream(0L);
			} else {
				out = ((CLOB) clobObj).setCharacterStream(0L);
			}

			out.write(clobString);
			out.close();
		} catch (IOException e) {
			throw new AppException("在向Clob对象中写入数据时出现I/O异常!", e);
		} catch (SQLException e) {
			throw new AppException("在获取Clob文本输出流时出现SQLException!", e);
		}
	}

}
