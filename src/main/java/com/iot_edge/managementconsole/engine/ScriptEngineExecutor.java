//package com.iotedge.engine;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//
//import org.graalvm.polyglot.Context;
//import org.graalvm.polyglot.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ScriptEngineExecutor {
//
//    public ScriptEngineExecutor() {
//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine scriptEngine = manager.getEngineByName("graal.js");
//        if (scriptEngine == null) {
//            throw new RuntimeException("JavaScript engine not found! Use a compatible JDK or GraalVM.");
//        }
//    }
//
//    public Object executeScript(String script) {
//        try (Context context = Context.create()) {
//            System.out.println("Received script: " + script);
//            Value result = context.eval("js", script);
//            if (result != null) {
//                return result.toString();
//            } else {
//                return "No result returned from script.";
//            }
//        } catch (Exception e) {
//            return "Script execution failed: " + e.getMessage();
//        }
//    }
//
//}


package com.iot_edge.managementconsole.engine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScriptEngineExecutor {

    public Object executeScript(String script, Map<String, Object> mqttData) {
        try (Context context = Context.create()) {
            // Convert MQTT data to JavaScript object
            StringBuilder jsObject = new StringBuilder("let obj = ");
            jsObject.append(toJsonString(mqttData)).append(";\n").append(script);

            System.out.println("Executing script:\n" + jsObject);

            Value result = context.eval("js", jsObject.toString());

            return (result != null) ? result.toString() : "No result returned from script.";
        } catch (Exception e) {
            return "Script execution failed: " + e.getMessage();
        }
    }



    // Convert Java Map to JSON string format for JavaScript execution
    private String toJsonString(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 2); // Remove trailing comma
        }
        json.append("}");
        return json.toString();
    }
}
