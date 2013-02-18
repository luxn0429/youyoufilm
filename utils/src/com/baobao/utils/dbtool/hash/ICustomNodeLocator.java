package com.baobao.utils.dbtool.hash;

public interface ICustomNodeLocator {
	/**
	 * Get the primary location for the given key.
	 *
	 * @param k the object key
	 * @return the QueueAttachment containing the primary storage for a key
	 */
	ICustomNode getPrimary(String k);
}
