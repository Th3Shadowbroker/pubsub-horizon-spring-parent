package de.telekom.eni.pandora.horizon.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Head {

    public static final String ZNODE_PREPARED = "/horizon/subscriptions/prepared";

    public static final String ZNODE_ACTIVATED = "/horizon/subscriptions/activated";

    public String snapshotId;

}
