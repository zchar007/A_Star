package com.sept.exception;

public class BusinessException extends SeptException {
	private static final long serialVersionUID = 1L;
	public static final String exceptionType = "业务";

	public BusinessException() {
		this(ExceptionNames.defaultCode, "程序出现业务异常!");
	}

	public BusinessException(Throwable cause) {
		this(ExceptionNames.defaultCode, cause == null ? "程序出现业务异常!" : cause.toString(), cause);
	}

	public BusinessException(String errtext) {
		this(ExceptionNames.defaultCode, errtext);
	}

	public BusinessException(String errtext, Throwable cause) {
		this(ExceptionNames.defaultCode, errtext, cause);
	}

	public BusinessException(int errcode, String errtext) {
		super(errcode + "_" + errtext);
	}

	public BusinessException(int errcode, String errtext, Throwable cause) {
		super(errcode + "_" + errtext, cause);
	}
}