/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.daemon.supervisor;

import java.io.IOException;
import java.util.Map;
import org.apache.storm.container.ResourceIsolationInterface;
import org.apache.storm.daemon.supervisor.Container.ContainerType;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.utils.LocalState;

public class RunAsUserContainerLauncher extends ContainerLauncher {
    protected final ResourceIsolationInterface _resourceIsolationManager;
    private final Map<String, Object> _conf;
    private final String _supervisorId;
    private final int _supervisorPort;

    public RunAsUserContainerLauncher(Map<String, Object> conf, String supervisorId, int supervisorPort,
                                      ResourceIsolationInterface resourceIsolationManager) throws IOException {
        _conf = conf;
        _supervisorId = supervisorId;
        _supervisorPort = supervisorPort;
        _resourceIsolationManager = resourceIsolationManager;
    }

    @Override
    public Container launchContainer(int port, LocalAssignment assignment, LocalState state) throws IOException {
        Container container = new RunAsUserContainer(ContainerType.LAUNCH, _conf, _supervisorId, _supervisorPort, port,
                                                     assignment, _resourceIsolationManager, state, null, null, null, null);
        container.setup();
        container.launch();
        return container;
    }

    @Override
    public Container recoverContainer(int port, LocalAssignment assignment, LocalState state) throws IOException {
        return new RunAsUserContainer(ContainerType.RECOVER_FULL, _conf, _supervisorId, _supervisorPort, port,
                                      assignment, _resourceIsolationManager, state, null, null, null, null);
    }

    @Override
    public Killable recoverContainer(String workerId, LocalState localState) throws IOException {
        return new RunAsUserContainer(ContainerType.RECOVER_PARTIAL, _conf, _supervisorId, _supervisorPort, -1, null,
                                      _resourceIsolationManager, localState, workerId, null, null, null);
    }

}
