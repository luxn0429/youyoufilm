package com.baobao.utils.dbtool.hash;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Ketama consistent hashing implemtation
 * 
 * @see <a href="http://www.last.fm/user/RJ/journal/2007/04/10/392555/">RJ's
 *      blog post</a>
 */
public class DefaultNodeLocator implements ICustomNodeLocator {

	final SortedMap<Long, ICustomNode> ketamaNodes;
	final Collection<ICustomNode> allNodes;

	final HashAlgorithm hashAlg;
	final IHashConfiguration config;

	public DefaultNodeLocator(List<ICustomNode> nodes) {
		this(nodes, HashAlgorithm.KETAMA_HASH, new DefaultHashConfiguration());
	}

	public DefaultNodeLocator(List<ICustomNode> nodes, HashAlgorithm alg,
			IHashConfiguration hashConfiguration) {
		super();
		allNodes = nodes;
		hashAlg = alg;
		ketamaNodes = new TreeMap<Long, ICustomNode>();
		config = hashConfiguration;

		int numReps = config.getNodeRepetitions();
		for (ICustomNode node : nodes) {
			// Ketama does some special work with md5 where it reuses chunks.
			if (alg == HashAlgorithm.KETAMA_HASH) {
				for (int i = 0; i < numReps / 4; i++) {
					byte[] digest = HashAlgorithm.computeMd5(config
							.getKeyForNode(node, i));
					for (int h = 0; h < 4; h++) {
						Long k = ((long) (digest[3 + h * 4] & 0xFF) << 24)
								| ((long) (digest[2 + h * 4] & 0xFF) << 16)
								| ((long) (digest[1 + h * 4] & 0xFF) << 8)
								| (digest[h * 4] & 0xFF);
						ketamaNodes.put(k, node);
					}

				}
			} else {
				for (int i = 0; i < numReps; i++) {

					ketamaNodes.put(
							hashAlg.hash(config.getKeyForNode(node, i)), node);
				}
			}
		}
		assert ketamaNodes.size() == numReps * nodes.size();
	}

	@Override
	public ICustomNode getPrimary(String k) {
		ICustomNode rv = getNodeForKey(hashAlg.hash(k));
		assert rv != null : "Found no node for key " + k;
		return rv;

	}

	private ICustomNode getNodeForKey(long hash) {
		final ICustomNode rv;
		if(!ketamaNodes.containsKey(hash)) {
			// Java 1.6 adds a ceilingKey method, but I'm still stuck in 1.5
			// in a lot of places, so I'm doing this myself.
			SortedMap<Long, ICustomNode> tailMap=ketamaNodes.tailMap(hash);
			if(tailMap.isEmpty()) {
				hash=ketamaNodes.firstKey();
			} else {
				hash=tailMap.firstKey();
			}
		}
		rv=ketamaNodes.get(hash);
		return rv;
	}

}
