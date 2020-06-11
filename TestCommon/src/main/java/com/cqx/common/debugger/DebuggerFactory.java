package com.cqx.common.debugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.tools.jdi.SocketAttachingConnector;

import java.util.List;
import java.util.Map;

/**
 * DebuggerFactory
 *
 * @author chenqixu
 */
public class DebuggerFactory {

    public static final String HOST = "hostname";
    public static final String PORT = "port";
    static VirtualMachine vm;
    static EventQueue eventQueue;
    static EventRequestManager eventRequestManager;
    static Process process;
    static EventSet eventSet;
    static boolean vmExit = false;

    public DebuggerFactory() {
    }

    private static void eventLoop() throws Exception {
        eventQueue = vm.eventQueue();
        while (true) {
            if (vmExit == true) {
                break;
            }
            eventSet = eventQueue.remove();
            EventIterator eventIterator = eventSet.eventIterator();
            while (eventIterator.hasNext()) {
                Event event = (Event) eventIterator.next();
                execute(event);
            }
        }
    }

    private static void execute(Event event) throws Exception {
        if (event instanceof VMStartEvent) {
            System.out.println("VM started");
            eventSet.resume();
        } else if (event instanceof BreakpointEvent) {
            System.out.println("Reach Method printHello of test.Test");

            BreakpointEvent breakpointEvent = (BreakpointEvent) event;
            ThreadReference threadReference = breakpointEvent.thread();
            StackFrame stackFrame = threadReference.frame(0);

            stackFrame.visibleVariables();

            // 获取date变量
            LocalVariable localVariable = stackFrame.visibleVariableByName("date");
            Value value = stackFrame.getValue(localVariable);
            String date = ((StringReference) value).value();

            LocalVariable localVariable1 = stackFrame.visibleVariableByName("i");
            Value value1 = stackFrame.getValue(localVariable1);
            int i = ((IntegerValue) value1).intValue();

            System.out.println("Debugger print[" + date + " : " + i + "]");

            eventSet.resume();
        } else if (event instanceof MethodEntryEvent) {
            MethodEntryEvent mee = (MethodEntryEvent) event;
            Method method = mee.method();
            System.out.println(method.name() + " was Entered!");
            eventSet.resume();
        } else if (event instanceof VMDisconnectEvent) {
            vmExit = true;
        } else if (event instanceof ClassPrepareEvent) {
            ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent) event;
            String mainClassName = classPrepareEvent.referenceType().name();
            if (mainClassName.equals("test.Test")) {
                System.out.println("Class " + mainClassName + " is already prepared");
            }
            if (true) {
                // Get location
                ReferenceType referenceType = classPrepareEvent.referenceType();
                List locations = referenceType.locationsOfLine(34);
                Location location = (Location) locations.get(0);

                // Create BreakpointEvent
                BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
                breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                breakpointRequest.enable();
            }
            eventSet.resume();
        } else {
            eventSet.resume();
        }
    }

    public void debugger() throws Exception {
        // 一、取得连接器
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        List<AttachingConnector> connectors = vmm.attachingConnectors();
        SocketAttachingConnector sac = null;
        for (AttachingConnector ac : connectors) {
            if (ac instanceof SocketAttachingConnector) {
                sac = (SocketAttachingConnector) ac;
                break;
            }
        }
        if (sac == null) {
            System.out.println("JDI error");
            return;
        }

        // 二、连接到远程虚拟器
        Map<String, Connector.Argument> arguments = sac.defaultArguments();
        Connector.Argument hostArg = (Connector.Argument) arguments.get(HOST);
        Connector.Argument portArg = (Connector.Argument) arguments.get(PORT);

        // hostArg.setValue("127.0.0.1");
        portArg.setValue(String.valueOf(8800));

        vm = sac.attach(arguments);
        process = vm.process();
        eventRequestManager = vm.eventRequestManager();

        // 三、取得要关注的类和方法
        List<ReferenceType> classesByName = vm.classesByName("test.Test");
        if (classesByName == null || classesByName.size() == 0) {
            System.out.println("No class found");
            return;
        }
        ReferenceType rt = classesByName.get(0);
        List<Method> methodsByName = rt.methodsByName("printHello");
        if (methodsByName == null || methodsByName.size() == 0) {
            System.out.println("No method found");
            return;
        }
        Method method = methodsByName.get(0);

        // 四、注册监听
        vm.setDebugTraceMode(VirtualMachine.TRACE_EVENTS);
        vm.resume();

        List<Location> locations = classesByName.get(0).locationsOfLine(34);
        BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(locations.get(0));
        breakpointRequest.enable();

//        MethodEntryRequest methodEntryRequest = eventRequestManager.createMethodEntryRequest();
//        methodEntryRequest.addClassFilter(rt);
//        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
//        // methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//
//        methodEntryRequest.enable();
//
//        BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(method.location());
//        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//        // breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//        breakpointRequest.enable();

        // ClassPrepareRequest classPrepareRequest = eventRequestManager.createClassPrepareRequest();
        // classPrepareRequest.addClassFilter("test.Test");
        // classPrepareRequest.addCountFilter(1);
        // classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        // classPrepareRequest.enable();

        eventLoop();
    }
}
