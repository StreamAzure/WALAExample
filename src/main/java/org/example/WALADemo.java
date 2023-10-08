package org.example;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;

public class WALADemo {
    private CallGraph callGraph = null;
    private boolean mainEntrypoints = true;

    public static void main(String[] args) throws IOException,
            ClassHierarchyException, IllegalArgumentException,
            CallGraphBuilderCancelException {
        WALADemo dectetor = new WALADemo();
        dectetor.makeCallGraph();
    }

    private void makeCallGraph() throws IOException, ClassHierarchyException,
            IllegalArgumentException, CallGraphBuilderCancelException {
        long start_time = System.currentTimeMillis();
        System.out.println("start to make call graph");
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(
                "C:\\Me\\JavaProjects\\Example\\target\\classes\\org\\example", new File("Java60RegressionExclusions.txt"));
        ClassHierarchy cha = ClassHierarchyFactory.make(scope);
        Iterable<Entrypoint> entryPointIterator = null;
        if (mainEntrypoints) {
            entryPointIterator = Util.makeMainEntrypoints(scope, cha);
        } else {
            entryPointIterator = new AllApplicationEntrypoints(scope, cha);
        }
        AnalysisOptions options = new AnalysisOptions(scope, entryPointIterator);
        // 0-CFA is faster and more precise
        CallGraphBuilder builder = Util.makeZeroCFABuilder(options,
                new AnalysisCacheImpl(), cha, scope);
        callGraph = builder.makeCallGraph(options, null);

        for (IClass c : cha) {
            String cname = c.getName().toString();
            System.out.println("Class:" + cname);
            for (IMethod m : c.getAllMethods()) {
                String mname = m.getName().toString();
                System.out.println("  method:" + mname);
            }
            System.out.println();
        }

        System.out.println(CallGraphStats.getStats(callGraph));
//        System.out.println(CallGraphStats.collectMethods(callGraph));
        System.out.println("Time spent ont building CHA and CG:"
                + (System.currentTimeMillis() - start_time) + "ms");
    }
}