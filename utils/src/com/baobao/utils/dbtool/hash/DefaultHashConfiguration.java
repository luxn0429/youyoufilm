package com.baobao.utils.dbtool.hash;


public class DefaultHashConfiguration implements IHashConfiguration {

	final int NUM_REPS = 160;

	@Override
	public String getKeyForNode(ICustomNode node, int repetition) {
		return node.toString() + "-" + repetition;
	}

	@Override
	public int getNodeRepetitions() {
		return NUM_REPS;
	}
}
