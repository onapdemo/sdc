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

package org.openecomp.sdc.be.model.operations.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openecomp.sdc.be.dao.graph.datatype.GraphEdge;
import org.openecomp.sdc.be.dao.titan.TitanOperationStatus;
import org.openecomp.sdc.be.model.CapabilityDefinition;
import org.openecomp.sdc.be.model.PropertyDefinition;
import org.openecomp.sdc.be.resources.data.CapabilityData;
import org.openecomp.sdc.be.resources.data.CapabilityTypeData;
import org.openecomp.sdc.be.resources.data.PropertyData;

import com.thinkaurelius.titan.core.TitanVertex;

import fj.data.Either;

public interface ICapabilityOperation {

	Either<Map<String, PropertyDefinition>, TitanOperationStatus> getAllCapabilityTypePropertiesFromAllDerivedFrom(String firstParentType);

	Either<List<PropertyDefinition>, TitanOperationStatus> validatePropertyUniqueness(Map<String, PropertyDefinition> propertiesOfCapabilityType, List<PropertyDefinition> properties);

}