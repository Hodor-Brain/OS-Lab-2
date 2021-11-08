package Lab2;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.HashSet;
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {
    private static int minimumRatioIndex(Vector<Double> ratios, Vector<Integer> indexes) {
        int index = indexes.get(0);
        for (int i = 1; i < indexes.size(); i++) {
            int currentIndex = indexes.get(i);
            if (ratios.get(currentIndex) < ratios.get(index)) {
                index = currentIndex;
            }

        }

        return index;
    }

    private static Vector<Double> findRatio(Vector<sProcess> vector, Vector<Integer> indexes) {
        Vector<Double> result = new Vector();

        int sum = 0;
        for (int j = 0; j < indexes.size(); j++) {
                sum += (vector.get(indexes.get(j))).cputime;
        }

        for (int i = 0; i < vector.size(); i++) {
            if(indexes.contains(i)){
                Double currentProcess = 1.0 * vector.get(i).cpudone;
                result.add(currentProcess / (1.0 * sum / indexes.size()));
            }
            else
                result.add(0.0);
        }

        return result;
    }

    public static Results Run(int runtime, Vector processVector, Results result) {
        int comptime = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";

        Vector<Integer> indexes = new Vector<>();
        for (int i = 0; i < processVector.size(); i++) {
            indexes.add(i);
        }
        Vector<Double> ratios = findRatio(processVector, indexes);

        result.schedulingType = "Interactive";
        result.schedulingName = "Guaranteed Scheduling";
        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            int currentMinimumIndex = minimumRatioIndex(ratios, indexes);
            sProcess process = (sProcess) processVector.elementAt(currentMinimumIndex);
            out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
            while (comptime < runtime) {
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + process.id + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }
                    indexes.remove((Object) currentMinimumIndex);

                    ratios = findRatio(processVector, indexes);

                    currentMinimumIndex = minimumRatioIndex(ratios, indexes);
                    process = (sProcess) processVector.elementAt(currentMinimumIndex);

                    out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                }
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + process.id + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                    process.numblocked++;
                    process.ionext = 0;

                    ratios = findRatio(processVector, indexes);

                    currentMinimumIndex = minimumRatioIndex(ratios, indexes);
                    process = (sProcess) processVector.elementAt(currentMinimumIndex);

                    out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                }
                process.cpudone++;
                if (process.ioblocking > 0) {
                    process.ionext++;
                }
                comptime++;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }
}
