// Copyright 2026 Deutsche Telekom IT GmbH
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.eni.pandora.horizon.mongo.repository;

import de.telekom.eni.pandora.horizon.mongo.model.SnapshotMongoDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface SnapshotMongoRepo extends MongoRepository<SnapshotMongoDocument, ObjectId> {

    public List<SnapshotMongoDocument> findBySnapshotId(ObjectId snapshotId);

}
