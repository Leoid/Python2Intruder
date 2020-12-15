package burp;

import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.awt.*;

public class BurpExtender implements IBurpExtender, ITab, IIntruderPayloadProcessor {

    IBurpExtenderCallbacks callbacks;
    IHttpRequestResponsePersisted PerRequestResponse;

    protected String mPluginName = "Python2Intruder";
    PythonInterpreter pi = new PythonInterpreter();

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        BurpExtenderTab.callbacks = callbacks;

        callbacks.setExtensionName("Python2Intruder Extension");
        callbacks.printOutput("Python2Intruder 1.0 loaded");

        BurpExtenderTab.configcomp = new ConfigComponent(callbacks);

        callbacks.registerIntruderPayloadProcessor(this);
        callbacks.addSuiteTab(this);

        //BurpExtenderTab.configcomp.servertypecomobox.getSelectedItem().toString()
    }


    @Override
    public String getTabCaption() {


        return BurpExtenderTab.tabName;
    }

    @Override
    public Component getUiComponent() {
        return BurpExtenderTab.configcomp.$$$getRootComponent$$$();
    }

    @Override
    public String getProcessorName() {

        return "Pythonize Intruder Payload";
    }

    @Override
    public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
        // Set the payloads
        pi.set("baseValue", new PyString(callbacks.getHelpers().bytesToString(baseValue)));
        pi.set("currentPayload", new PyString(callbacks.getHelpers().bytesToString(currentPayload)));

        // Exec Jython
        pi.exec(BurpExtenderTab.configcomp.pythontxtarea.getText().toString());

        // Get the payload
        PyString payload = (PyString)pi.get("retValue");


        byte[] ret = callbacks.getHelpers().stringToBytes(payload.toString());
        return ret;
    }


}
