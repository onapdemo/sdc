/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.tosca.datatypes.model;

import java.util.List;
import java.util.Map;



public class ServiceTemplate implements Template {

  private String tosca_definitions_version;
  private Map<String, String> metadata;
  private String description;
  private List<Map<String, Import>> imports;
  private Map<String, ArtifactType> artifact_types;
  private Map<String, DataType> data_types;
  private Map<String, CapabilityType> capability_types;
  private Map<String, Object> interface_types;
  private Map<String, RelationshipType> relationship_types;
  private Map<String, NodeType> node_types;
  private Map<String, GroupType> group_types;
  private Map<String, PolicyType> policy_types;
  private TopologyTemplate topology_template;

  public String getTosca_definitions_version() {
    return tosca_definitions_version;
  }

  public void setTosca_definitions_version(String tosca_definitions_version) {
    this.tosca_definitions_version = tosca_definitions_version;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Map<String, Import>> getImports() {
    return imports;
  }

  public void setImports(
      List<Map<String, Import>> imports) {
    this.imports = imports;
  }

  public Map<String, ArtifactType> getArtifact_types() {
    return artifact_types;
  }

  public void setArtifact_types(Map<String, ArtifactType> artifact_types) {
    this.artifact_types = artifact_types;
  }

  public Map<String, DataType> getData_types() {
    return data_types;
  }

  public void setData_types(Map<String, DataType> data_types) {
    this.data_types = data_types;
  }

  public Map<String, CapabilityType> getCapability_types() {
    return capability_types;
  }

  public void setCapability_types(Map<String, CapabilityType> capability_types) {
    this.capability_types = capability_types;
  }

  public Map<String, RelationshipType> getRelationship_types() {
    return relationship_types;
  }

  public void setRelationship_types(Map<String, RelationshipType> relationship_types) {
    this.relationship_types = relationship_types;
  }

  public Map<String, NodeType> getNode_types() {
    return node_types;
  }

  public void setNode_types(Map<String, NodeType> node_types) {
    this.node_types = node_types;
  }

  public Map<String, GroupType> getGroup_types() {
    return group_types;
  }

  public void setGroup_types(Map<String, GroupType> group_types) {
    this.group_types = group_types;
  }

  public Map<String, Object> getInterface_types() {
    return interface_types;
  }

  public void setInterface_types(Map<String, Object> interface_types) {
    this.interface_types = interface_types;
  }

  public Map<String, PolicyType> getPolicy_types() {
    return policy_types;
  }

  public void setPolicy_types(Map<String, PolicyType> policy_types) {
    this.policy_types = policy_types;
  }

  public TopologyTemplate getTopology_template() {
    return topology_template;
  }

  public void setTopology_template(TopologyTemplate topology_template) {
    this.topology_template = topology_template;
  }
}
