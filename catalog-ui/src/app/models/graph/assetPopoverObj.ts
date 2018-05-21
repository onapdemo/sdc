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

export class AssetPopoverObj {

    nodeId:string;
    displayName:string;
    menuPosition:Cy.Position;
    menuSide:string;
    isViewOnly:boolean;
    VLArray:Array<any>;
    CPArray:Array<any>;

    constructor(nodeId:string, displayName:string, menuPosition:Cy.Position, menuSide:string, isViewOnly?:boolean, VLArray?:Array<any>, CPArray?:Array<any>) {
        this.nodeId = nodeId;
        this.displayName = displayName;
        this.menuPosition = {x: menuPosition.x, y: menuPosition.y};
        this.menuSide = menuSide;
        this.isViewOnly = isViewOnly || false;
        this.VLArray = VLArray || [];
        this.CPArray = CPArray || [];
    }
}


