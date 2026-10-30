// Copyright 2026 Deutsche Telekom IT GmbH
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.eni.pandora.horizon.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.telekom.eni.pandora.horizon.mongo.repository.SnapshotMongoRepo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReplicaService {

    private final CuratorCache prepareCache;

    private final CuratorCache activeCache;

    private final ReplicaMaps replicaMaps;

    private final SnapshotMongoRepo snapshotMongoRepo;

    private final ObjectMapper objectMapper;

    public ReplicaService(CuratorFramework curatorFramework, ReplicaMaps replicaMaps, SnapshotMongoRepo snapshotMongoRepo) {
        this.prepareCache = CuratorCache.build(curatorFramework, Head.ZNODE_PREPARED, CuratorCache.Options.SINGLE_NODE_CACHE);
        this.activeCache = CuratorCache.build(curatorFramework, Head.ZNODE_ACTIVATED, CuratorCache.Options.SINGLE_NODE_CACHE);
        this.replicaMaps = replicaMaps;
        this.snapshotMongoRepo = snapshotMongoRepo;
        this.objectMapper = new ObjectMapper();

        prepareCache.listenable().addListener(
                CuratorCacheListener.builder()
                        .forChanges((oldNode, node) -> {
                            actOnPrepare(node.getData(), node.getStat().getMzxid());
                        }).build()
        );

        activeCache.listenable().addListener(
                CuratorCacheListener.builder()
                        .forChanges((oldNode, node) -> {
                            actOnActive(node.getData(), node.getStat().getMzxid());
                        }).build()
        );
    }

    public void setup() {
        var prepareNode = prepareCache.get(Head.ZNODE_PREPARED);
        if (prepareNode.isPresent()) {
            var data = prepareNode.get().getData();
            var mzxid = prepareNode.get().getStat().getMzxid();
            actOnPrepare(data, mzxid);
        }

        var activeNode = prepareCache.get(Head.ZNODE_ACTIVATED);
        if (activeNode.isPresent()) {
            var data = activeNode.get().getData();
            var mzxid = activeNode.get().getStat().getMzxid();
            actOnActive(data, mzxid);
        }
    }

    public void tearDown() {
        prepareCache.close();
        activeCache.close();
    }

    private void actOnPrepare(byte[] bytes, long mzxid) {
        try {
            var head = objectMapper.readValue(bytes, Head.class);
            var docs = snapshotMongoRepo.findBySnapshotId(head.getSnapshotId());

            var indexedMap = new IndexedMap(docs, mzxid, head.getSnapshotObjectId());
            replicaMaps.prepare(indexedMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actOnActive(byte[] bytes, long mzxid) {
        try {
            var head = objectMapper.readValue(bytes, Head.class);
            if (head.getSnapshotObjectId().equals(replicaMaps.getPrepared().get().getSnapshotId())) {
                replicaMaps.activate();
            } else {
                var docs = snapshotMongoRepo.findBySnapshotId(head.getSnapshotId());

                var indexedMap = new IndexedMap(docs, mzxid, head.getSnapshotObjectId());
                replicaMaps.activate(indexedMap);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
