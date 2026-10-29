package de.telekom.eni.pandora.horizon.subscription;

import de.telekom.eni.pandora.horizon.mongo.repository.SnapshotMongoRepo;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

public class HeadWatcher implements CuratorWatcher {

    private final SnapshotMongoRepo snapshotMongoRepo;

    private final ReplicaMaps replicaMaps;

    public HeadWatcher(SnapshotMongoRepo snapshotMongoRepo, ReplicaMaps replicaMaps) {
        this.snapshotMongoRepo = snapshotMongoRepo;
        this.replicaMaps = replicaMaps;
    }


    @Override
    public void process(WatchedEvent event) throws Exception {
        switch (event.getType()) {

        }
    }

}
