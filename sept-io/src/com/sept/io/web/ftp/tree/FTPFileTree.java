package com.sept.io.web.ftp.tree;

import com.sept.datastructure.tree.AbstractNode;
import com.sept.datastructure.tree.AbstractTree;
import com.sept.exception.AppException;

public class FTPFileTree extends AbstractTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FTPFileTree(AbstractNode rootNode) throws AppException {
		super(rootNode);
	}

	@Override
	public String getTreeData() throws AppException {
		return null;
	}

}
