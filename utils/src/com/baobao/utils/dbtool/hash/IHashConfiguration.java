package com.baobao.utils.dbtool.hash;

public interface IHashConfiguration {
	public String getKeyForNode(ICustomNode node, int repetition);

	int getNodeRepetitions();
}
