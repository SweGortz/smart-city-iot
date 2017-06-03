package org.gortz.greeniot.smartcityiot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Topic structure entity
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class TopicStructure {

    /**
     * Id of topic structure
     *
     * @return id of topic structure
     */
    private int id;

    /**
     * name of topic structure
     *
     * @return name of topic structure
     */
    private String name;

    /**
     * RegEx to retrieve data from topic structure
     *
     * @return regEx for topic structure
     */
    private String regex;

    /**
     * Location regex group id
     *
     * @return location regex id for topic structure
     */
    private int locationRegexID;

    /**
     * Node name regex group id
     *
     * @return node name regex id for topic structure
     */
    private int nodeNameRegexID;

    /**
     * Organization name regex group id
     *
     * @return Organization name regex id for topic structure
     */
    private int organizationRegexID;

    /**
     * Connection group connected to topic structure
     *
     * @return Connection group for topic structure
     */
    private String groupName;

    public TopicStructure(String name, String regex, int locationRegexID, int nodeNameRegexID, int organizationRegexID, String groupName) {
        this.name = name;
        this.regex = regex;
        this.locationRegexID = locationRegexID;
        this.nodeNameRegexID = nodeNameRegexID;
        this.organizationRegexID = organizationRegexID;
        this.groupName = groupName;
    }
    public TopicStructure(String name){
        this.name = name;
    }

    public TopicStructure(int id) {
        this.id = id;
    }
}
