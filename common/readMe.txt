# OpenECOMP Common

---
---

# Introduction

ASDC is required to distribute the Service Definition model to different systems of the OPENECOMP platform. The systems interested in the Service model will subscribe to Artifact Generator. Artifact Generator is responsible for converting its internal Service definition model to the data model required by the different subscribing systems.

In 1707, Artifact Generator will generate artifacts only for A&AI.

# Compiling Common

###Common can be compiled easily with a `mvn clean install` at the following locations :

#### common\openecomp-sdc-artifact-generator-lib
#### common\openecomp-logging-lib

### For artifact generator, the resultant jar files are located at :

#### common\openecomp-sdc-artifact-generator-lib\openecomp-sdc-artifact-generator-api\target
#### common\openecomp-sdc-artifact-generator-lib\openecomp-sdc-artifact-generator-core\target

### For openecomp-logging, the resultant jar files are located at :

#### common\openecomp-logging-lib\openecomp-logging-api\target
#### common\openecomp-logging-lib\openecomp-logging-core\target

### For configuration-management, the resultant jar files are located at :

#### common\openecomp-common-configuration-management\openecomp-configuration-management-api\target
#### common\openecomp-common-configuration-management\openecomp-configuration-management-cli\target
#### common\openecomp-common-configuration-management\openecomp-configuration-management-core\target





# Accessing Common

### Artifact Generator

#### api :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-sdc-artifact-generator-api</artifactId>
    <version>?</version>
 </dependency>

#### core :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-sdc-artifact-generator-core</artifactId>
    <version>?</version>
 </dependency>

### Logging

#### api :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-logging-api</artifactId>
    <version>?</version>
 </dependency>

#### core :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-logging-core</artifactId>
    <version>?</version>
 </dependency>

### Configuration Management

#### api :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-configuration-management-api</artifactId>
    <version>?</version>
 </dependency>

#### cli :

 <dependency>
     <groupId>org.openecomp.sdc.common</groupId>
     <artifactId>openecomp-configuration-management-cli</artifactId>
     <version>?</version>
  </dependency>

#### core :

 <dependency>
    <groupId>org.openecomp.sdc.common</groupId>
    <artifactId>openecomp-configuration-management-core</artifactId>
    <version>?</version>
 </dependency>

### NOTE : Artifact Generator generates artifacts using method 'generateArtifact' that takes the following arguments as input :

#### input(Specifies the list of input files as models)
#### overridingConfiguration(Specifies the configuration data for invoking generators)

The output will be a list of artifacts that are generated by the generators as defined in the API signature.

# Logging

OpenECOMP Common supports EELF Logger, which is of the following types :

### Error
### Debug
### Metrics
### Audit

