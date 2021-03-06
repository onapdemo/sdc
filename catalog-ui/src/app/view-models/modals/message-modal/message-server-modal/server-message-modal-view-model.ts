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

'use strict';
import {IMessageModalModel, IMessageModalViewModelScope, MessageModalViewModel} from "../message-base-modal-model";

export interface IServerMessageModalModel extends IMessageModalModel {
    status:string;
    messageId:string;
}

export interface IServerMessageModalViewModelScope extends IMessageModalViewModelScope {
    serverMessageModalModel:IServerMessageModalModel;
}

export class ServerMessageModalViewModel extends MessageModalViewModel {

    static '$inject' = ['$scope', '$uibModalInstance', 'serverMessageModalModel'];

    constructor(private $scope:IServerMessageModalViewModelScope,
                private $uibModalInstance:ng.ui.bootstrap.IModalServiceInstance,
                private serverMessageModalModel:IServerMessageModalModel) {

        super($scope, $uibModalInstance, serverMessageModalModel);
    }

}
