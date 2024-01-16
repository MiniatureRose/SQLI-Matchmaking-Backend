package com.sqli.matchmaking.service.teammaking.forms;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.teammaking.TeamMaking;

import lombok.Getter;

public class EvenMaking implements TeamMaking {

    public void make(List<User> players, List<Team> teams) {

        // Map players by rank
        List<Double> ranks = players.stream().map(User::getRank).collect(Collectors.toList());
        // Calculate the number of teams
        int noTeams = teams.size();
        // Calculate the number of players per team
        int playersPerTeam = players.size() / noTeams;
        // get noTeams subList, such that the average of rank of all sublist are approximetively close
    }
}

@Getter
class EvenPartition {

    private List<List<Double>> result;

    public EvenPartition(List<Double> list, int n) {
        this.result = null;
        double minDifference = Double.MAX_VALUE;

        // Generate all possible partitions
        GeneratePartitions allPartitions = new GeneratePartitions(list, n);
        for (List<List<Double>> partition : allPartitions.getResult()) {
            double currentDifference = calculateSumOfDifferences(partition);
            if (currentDifference < minDifference) {
                minDifference = currentDifference;
                this.result = partition;
            }
        }
    }

    private double calculateSumOfDifferences(List<List<Double>> partition) {
        double sum = 0.0;
        for (int i = 0; i < partition.size(); i++) {
            for (int j = i + 1; j < partition.size(); j++) {
                double avg1 = partition.get(i).stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0);
                double avg2 = partition.get(j).stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0);
                sum += Math.abs(avg1 - avg2);
            }
        }
        return sum;
    }

}


@Getter
class GeneratePartitions {

    private final int n;
    private final List<Double> list;
    private List<List<List<Double>>> result;

    public GeneratePartitions(List<Double> list, int n) {
        this.list = list;
        this.n = n;
        ////this.result = new ArrayList<>();
        int partitionSize = list.size() / n;
        this.result = partitionHelper(list, n, partitionSize, 0);
    }

    private List<List<List<Double>>> partitionHelper(List<Double> list, int n, int size, int start) {
        List<List<List<Double>>> result = new ArrayList<>();

        if (n == 1) {
            List<List<Double>> singlePartition = new ArrayList<>();
            singlePartition.add(new ArrayList<>(list.subList(start, list.size())));
            result.add(singlePartition);
            return result;
        }

        for (int i = start; i <= list.size() - size * n; i += size) {
            List<Double> firstPart = list.subList(i, i + size);
            List<List<List<Double>>> subPartitions = partitionHelper(list, n - 1, size, i + size);

            for (List<List<Double>> subPartition : subPartitions) {
                List<List<Double>> newPartition = new ArrayList<>();
                newPartition.add(firstPart);
                newPartition.addAll(subPartition);
                result.add(newPartition);
            }
        }

        return result;
    }

    public void debugg() {
        for (List<List<Double>> poss : this.result) {
            for (List<Double> partition : poss) {
                System.out.println(partition);
            }
            System.out.println("----\n");
        }
    }
}

class Test {

    public static void main(String[] args) {
        // Example list of Double values
        List<Double> list = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0);
        // Number of partitions
        int n = 3;
        GeneratePartitions g = new GeneratePartitions(list, n);
        g.debugg();
        // Create an instance of EvenPartition
        EvenPartition evenPartition = new EvenPartition(list, n);
        // Retrieve and print the result
        List<List<Double>> partitionResult = evenPartition.getResult();
        System.out.println("Optimal partitions with minimal sum of differences between averages:");
        for (List<Double> partition : partitionResult) {
            //System.out.println(partition);
        }
    }
}

