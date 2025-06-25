package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.engine.ScriptEngineExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScriptService {

    private final  AssetService assetService;

    private final ScriptEngineExecutor scriptEngineExecutor;

    public ScriptService(AssetService assetService, ScriptEngineExecutor scriptEngineExecutor) {
        this.assetService = assetService;
        this.scriptEngineExecutor = scriptEngineExecutor;
    }

    public Object executeScript(String script){
//        return scriptEngineExecutor.executeScript(script);
        return  new Object();
    }
}
