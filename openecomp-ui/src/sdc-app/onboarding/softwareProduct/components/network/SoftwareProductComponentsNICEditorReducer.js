/*!
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import {actionTypes} from './SoftwareProductComponentsNetworkConstants.js';
import {forms} from 'sdc-app/onboarding/softwareProduct/components/SoftwareProductComponentsConstants.js';

export default (state = {}, action) => {
	switch (action.type) {
		case actionTypes.NICEditor.FILL_DATA:
			return {
				...state,
				data: action.nic,
				genericFieldInfo: {
					'description' : {
						isValid: true,
						errorText: '',
						validations: []
					},
					'name' : {
						isValid: true,
						errorText: '',
						validations: [{type: 'required', data : true}]
					},
					'networkDescription' : {
						isValid: true,
						errorText: '',
						validations: []
					}
				},
				formName: forms.NIC_EDIT_FORM
			};
		case actionTypes.NICEditor.CLEAR_DATA:
			return {};
		default:
			return state;
	}
};
