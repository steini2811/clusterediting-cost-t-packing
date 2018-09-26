package de.unmr.bacluster.util;

public class Config {
    public static enum DrStrategy {
        NONE, EVERY_TIME, ONE_TIME, OPTIMIZED
    }

    public static enum OrderType {
        PARTOFP3S(), NODEDEGREE(), SUBGRAPHNBCOUNT(), NONE();
    }

    private static boolean lowerBound = false;
    private static boolean costT = false;
    private static boolean splitBridges = false;
    private static boolean noCalc = false;
    private static boolean simpleBranching = false;
    private static boolean simpleBranchingT = false;
    private static boolean packingPartialSolutionRule = false;
    private static boolean searchForPartialSolutions = false;
    private static OrderType orderType = OrderType.NONE;
    private static boolean ascending = false;
    private static int tValue = 0;
    private static boolean oldNB = false;

    public static OrderType getOrderType() {
        return orderType;
    }

    public static int getTValue() {
        return tValue;
    }

    public static void init(final String[] args) {
        for (final String arg : args) {
            final String[] splitArg = arg.split("=");
            if (splitArg.length == 1) {
                if (splitArg[0].equals("--lb")) {
                    lowerBound = true;
                } else if (splitArg[0].equals("--nocalc")) {
                    noCalc = true;
                } else if (splitArg[0].equals("--debug")) {
                    Debug.setDebug(true);
                } else if (splitArg[0].equals("--simple")) {
                    simpleBranching = true;
                } else if (splitArg[0].equals("--cost")) {
                    lowerBound = true;
                    costT = true;
                } else if (splitArg[0].equals("--packingpartial")) {
                    packingPartialSolutionRule = true;
                } else if (splitArg[0].equals("--startpartial")) {
                    searchForPartialSolutions = true;
                } else if (splitArg[0].equals("--partofp3s")) {
                    orderType = OrderType.PARTOFP3S;
                } else if (splitArg[0].equals("--nodedegree")) {
                    orderType = OrderType.NODEDEGREE;
                } else if (splitArg[0].equals("--neighborcount")) {
                    orderType = OrderType.SUBGRAPHNBCOUNT;
                } else if (splitArg[0].equals("--asc")) {
                    ascending = true;
                } else if (splitArg[0].equals("--old")) {
                    oldNB = true;
                } else {
                    throw new RuntimeException("Unknown option or missing value");
                }
            } else if (splitArg.length == 2) {
                if (splitArg[0].equals("--t")) {
                    lowerBound = true;
                    costT = true;
                    tValue = Integer.parseInt(splitArg[1]);
                } else if (splitArg[0].equals("--simplet")) {
                    lowerBound = true;
                    costT = true;
                    simpleBranchingT = true;
                    tValue = Integer.parseInt(splitArg[1]);
                } else {
                    throw new RuntimeException("Unknown option or redundant value");
                }
            } else {
                throw new RuntimeException("Arguments invalid");
            }

        }
    }

    public static boolean isAscending() {
        return ascending;
    }

    public static boolean isCalc() {
        return !noCalc;
    }

    public static boolean useCostT() {
        return costT;
    }

    public static boolean useLowerBound() {
        return lowerBound;
    }

    public static boolean useOldNB() {
        return oldNB;
    }

    public static boolean usePackingPartialSolutionRule() {
        return packingPartialSolutionRule;
    }

    public static boolean useSimpleBranching() {
        return simpleBranching;
    }

    public static boolean useSimpleBranchingT() {
        return simpleBranchingT;
    }

    public static boolean useSplitBridges() {
        return splitBridges;
    }

    public static boolean useStartPartialSolutionRule() {
        return searchForPartialSolutions;
    }

}
