// Copyright 2026 Deutsche Telekom IT GmbH
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.eni.pandora.horizon.subscription;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

@Service
public class ReplicaService {

    private final CuratorFramework curatorFramework;

    private final ReplicaMaps replicaMaps;

    public ReplicaService(CuratorFramework curatorFramework, ReplicaMaps replicaMaps) {
        this.curatorFramework = curatorFramework;
        this.replicaMaps = replicaMaps;
    }

}
