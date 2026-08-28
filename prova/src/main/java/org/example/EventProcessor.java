package org.example;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import com.espertech.esperio.csv.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;

import java.io.File;
import java.util.Map;

public class EventProcessor {
    private File csvFile;
    private Configuration configuration;
    private EPRuntime runtime;
    private EPStatement statement;

    //Initialize the Event Processing Runtime using the filepath and the structure of the event
    public EventProcessor(String filePath, String eventName, Map<String, Object> eventProperties) {
        try {
            this.csvFile = new File(filePath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.configuration = new Configuration();
        this.configuration.getCommon().addEventType(eventName, eventProperties);
        this.runtime = EPRuntimeProvider.getDefaultRuntime(this.configuration);
        this.statement = null;

        this.configuration.getCommon().addImport("java.time.*");

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public EPStatement getStatement() {
        return statement;
    }

    //Compile and deploy an EPL query
    public void compileDeploy(String epl) {
        try {
            // Get the compiler
            EPCompiler compiler = EPCompilerProvider.getCompiler();

            // Pass our configuration to the compiler arguments
            CompilerArguments args = new CompilerArguments(this.configuration);

            // Compile the string into bytecode
            EPCompiled compiled = compiler.compile(epl, args);

            // Deploy the bytecode to the runtime
            EPDeployment deployment = this.runtime.getDeploymentService().deploy(compiled);

            // Return the first statement in the deployment
            int totalStatements = deployment.getStatements().length;
            if(totalStatements > 0)
                this.statement =  deployment.getStatements()[totalStatements-1];

        } catch (EPCompileException | EPDeployException e) {
            throw new RuntimeException("Failed to compile or deploy EPL: " + e.getMessage(), e);
        }
    }

    //Start the stream of events
    public void startStream(String name){
        (new CSVInputAdapter(runtime, new AdapterInputSource(csvFile), name)).start();
    }
}
