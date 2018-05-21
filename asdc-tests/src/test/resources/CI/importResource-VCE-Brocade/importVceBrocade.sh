#!/bin/bash

function usage {
	echo "Usage: $0 <hostIp> <hostPort> <userId>"
}

function addResource() {

	ELEMENT_NAME=$1
	echo -e "###################### Adding Element ${ELEMENT_NAME} Start ######################"
	CURRENT_ZIP_FILE=./${ELEMENT_NAME}/${ELEMENT_NAME}.zip
	CURRENT_JSON_FILE=./${ELEMENT_NAME}/${ELEMENT_NAME}.json
	sed -i 's/"userId": ".*",/"userId": "'${ATT_UID}'",/' ${CURRENT_JSON_FILE}
	JSON_CONTENT=`paste -s ${CURRENT_JSON_FILE}`
	http_code=$(curl -s -o /dev/null -w "%{http_code}" -v -F resourceMetadata="${JSON_CONTENT}" -F resourceZip=@${CURRENT_ZIP_FILE} -H USER_ID:${ATT_UID} ${HOST_IP}:${HOST_PORT}/sdc2/rest/v1/catalog/upload/multipart)
	if [ ${http_code} -eq 201  ]; then
		echo -e "\n###################### Adding Element ${ELEMENT_NAME} End ########################\n\n\n"
	elif [ ${http_code} -eq 409 ]; then
		echo -e "\n###################### Already exists Element ${ELEMENT_NAME} status code:${http_code} End ########################\n\n\n"
	elif [ ${http_code} -eq 500 ]; then
		echo -e "\n###################### Failed to add Element ${ELEMENT_NAME} status code:${http_code} End ########################\n\n\n"
		exit 1
	fi
}
if [ $# -lt 3 ]
then
	usage
	exit 2
fi

HOST_IP=$1
HOST_PORT=$2
ATT_UID=$3
NO_CAPS=$4

if [ "$NO_CAPS" = "nocaps" ]; then
	echo "Skipping Caps import..."
else
	#Add The CapabilityTypes
	http_code=$(curl -s -o /dev/null -w "%{http_code}" -v -F capabilityTypeZip=@capabilityTypes.zip -H "USER_ID: jh0003" ${HOST_IP}:${HOST_PORT}/sdc2/rest/v1/catalog/uploadType/capability)
	if [ ${http_code} -eq 201  ]; then
		echo -e "\n###################### Adding The CapabilityTypes status code:${http_code} End ########################\n\n\n"
	elif [ ${http_code} -eq 500 ]; then
		echo -e "\n###################### Failed to add CapabilityTypes status code:${http_code} End ########################\n\n\n"
		exit 1
	else
		echo -e "\n###################### Failed to add CapabilityTypes status code:${http_code} End ########################\n\n\n"
		exit 1
	fi
	#Add The InterfaceLifecycleTypes
	#http_code=$(curl -s -o /dev/null -w "%{http_code}" -v -F interfaceLifecycleTypeZip=@interfaceLifecycleTypes.zip -H "USER_ID: jh0003" ${HOST_IP}:${HOST_PORT}/sdc2/rest/v1/catalog/uploadType/interfaceLifecycle)
	#if [ ${http_code} -eq 201  ]; then
	#	echo -e "\n###################### Adding The InterfaceLifecycleTypes status code:${http_code} End ########################\n\n\n"
	#elif [ ${http_code} -eq 409 ]; then
	#    echo -e "\n###################### Already exists InterfaceLifecycleTypes status code:${http_code} End ########################\n\n\n"
	#elif [ ${http_code} -eq 500 ]; then
	#	echo -e "\n###################### Failed to add InterfaceLifecycleTypes status code:${http_code} End ########################\n\n\n"
	#	exit 1
	#else
	#	echo -e "\n###################### Failed to add InterfaceLifecycleTypes status code:${http_code} End ########################\n\n\n"
	#	exit 1
	#fi
fi

addResource "tosca.nodes.nfv.CP"
addResource "tosca.nodes.nfv.VDU"

addResource "org.openecomp.resource.nfv.CP"
addResource "org.openecomp.resource.nfv.VDU"

addResource "org.openecomp.resource.nfv.vdu.RoutingCategoryVDU"
addResource "org.openecomp.resource.nfv.vdu.VCE_Brocade_VDU"
addResource "org.openecomp.resource.nfv.cp.IPAG_VLAN_Connector"
addResource "org.openecomp.resource.nfv.cp.OAM_VLAN_Connector"
addResource "org.openecomp.resource.nfv.cp.VPE_VLAN_Connector"

#addResource "tosca.nodes.nfv.VL"
#addResource "tosca.nodes.nfv.VNF"
#addResource "org.openecomp.resource.nfv.VL"
#addResource "org.openecomp.resource.nfv.VNF"
#addResource "org.openecomp.resource.nfv.VDU.PortAddrCategoryVDU"
#addResource "org.openecomp.resource.nfv.VNF.VCE_Brocade_VNF"
#addResource "org.openecomp.resource.nfv.VDU.DHCPCategoryVDU"
#addResource "org.openecomp.resource.nfv.VDU.DNSCategoryVDU"
#addResource "org.openecomp.resource.nfv.VDU.FirewallCategoryVDU"
#addResource "org.openecomp.resource.nfv.VDU.NetworkAddrCategoryVDU"


exit 0
