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

package org.openecomp.core.migration.loaders;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.openecomp.core.nosqldb.api.NoSqlDb;
import org.openecomp.core.nosqldb.factory.NoSqlDbFactory;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.OrchestrationTemplateCandidateDataEntity;

import java.util.Collection;

public class OrchestrationTemplateCandidateCassandraLoader {

  private static final NoSqlDb noSqlDb = NoSqlDbFactory.getInstance().createInterface();
  private static final OrchestrationTemplateCandidateAccessor accessor =
      noSqlDb.getMappingManager().createAccessor(OrchestrationTemplateCandidateAccessor.class);


  public Collection<OrchestrationTemplateCandidateDataEntity> list() {
    return accessor.list().all();
  }

  @Accessor
  interface OrchestrationTemplateCandidateAccessor {

    @Query(
        "select * from vsp_orchestration_template_candidate ")
    Result<OrchestrationTemplateCandidateDataEntity> list();


  }



}
