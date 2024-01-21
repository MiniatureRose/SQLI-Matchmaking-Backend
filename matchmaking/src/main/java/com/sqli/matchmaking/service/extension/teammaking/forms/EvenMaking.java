package com.sqli.matchmaking.service.extension.teammaking.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.model.associative.TeamUser;
import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.extension.TeamService;
import com.sqli.matchmaking.service.extension.teammaking.TeamMaking;

import lombok.Getter;

@Service
public class EvenMaking implements TeamMaking {

    @Autowired
    private TeamService teamService;

    @Transactional
    public void make(List<User> players, List<Team> teams) {

        // Map players by rank
        List<Double> ranks = players.stream().map(User::getRank).collect(Collectors.toList());
        // Calculate the number of teams
        int noTeams = teams.size();
        // Find the best partition
        EvenPartition bestPartition = new EvenPartition(ranks, noTeams);
        List<List<Double>> ret = bestPartition.getResult();

        // Copy players 
        List<User> playersClone = new ArrayList<>(players);

        assert ret.size() == noTeams : "WEIRD : bestPartition is not of size teams";
        for (int i = 0; i < noTeams; i++) {
            for (Double rank : ret.get(i)) {
                User player = findPlayerByRank(rank, playersClone);
                // Add player to team
                TeamUser el = TeamUser.builder()
                        .user(player).team(teams.get(i))
                        .build();
                try {
                    teamService.save(el);
                } catch (DataIntegrityViolationException e) {
                    throw new Exceptions.EntityCannotBeSaved("TeamUser");
                }    
            }
        }
        
    }

    private User findPlayerByRank(Double rank, List<User> players) {
        for (User player : players) {
            if (player.getRank().equals(rank)) {
                // Remove player 
                players.remove(player);
                return player;
            }
        }
        return null;
    }
}

@Getter
class EvenPartition {

    private List<List<Double>> result;
    private final List<Double> list;

    public EvenPartition(List<Double> list, int n) {
        this.list = list;

        List<List<Integer>> bestPartition = new ArrayList<>();
        double minDifference = Double.MAX_VALUE;
        // Generate all possible partitions
        List<Integer> indices = IntStream.range(0, list.size())
                                         .boxed()
                                         .collect(Collectors.toList());
        PartitionGenerator allPartitions = new PartitionGenerator(indices, n);
        for (List<List<Integer>> partition : allPartitions.getResult()) {
            double currentDifference = calculateSumOfDifferences(partition);
            if (currentDifference < minDifference) {
                minDifference = currentDifference;
                bestPartition = partition;
            }
        }

        this.result = bestPartition.stream()
                    .map(p -> p.stream()
                        .map(el -> this.list.get(el)).collect(Collectors.toList()))
                    .collect(Collectors.toList());
    }

    private double calculateSumOfDifferences(List<List<Integer>> partition) {
        double sum = 0.0;
        for (int i = 0; i < partition.size(); i++) {
            for (int j = i + 1; j < partition.size(); j++) {
                double avg1 = partition.get(i).stream()
                    .map(el -> this.list.get(el))
                    .mapToDouble(Double::doubleValue).average().orElse(0);
                double avg2 = partition.get(j).stream()
                    .map(el -> this.list.get(el))
                    .mapToDouble(Double::doubleValue).average().orElse(0);
                sum += Math.abs(avg1 - avg2);
            }
        }
        return sum;
    } 


}

@Getter
class PartitionGenerator {

    private List<List<List<Integer>>> result;
    private final int partitionSize;

    public PartitionGenerator(List<Integer> L, int m) {
        int n = L.size();
        if (n % m != 0) {
            throw new IllegalArgumentException("'m' must divide 'n' evenly");
        }
        this.partitionSize = n / m;
        this.result = generateSubPartitions(new ArrayList<>(), new ArrayList<>(L));
    }

    private List<List<List<Integer>>> generateSubPartitions(List<List<Integer>> sublist, List<Integer> remaining) {
        if (remaining.isEmpty()) {
            List<List<List<Integer>>> result = new ArrayList<>();
            result.add(new ArrayList<>(sublist));
            return result;
        }

        List<List<List<Integer>>> partitions = new ArrayList<>();
        for (List<Integer> combo : generateCombinations(remaining, this.partitionSize)) {
            List<List<Integer>> newSublist = new ArrayList<>(sublist);
            newSublist.add(combo);

            List<Integer> newRemaining = new ArrayList<>(remaining);
            newRemaining.removeAll(combo);

            partitions.addAll(generateSubPartitions(newSublist, newRemaining));
        }

        return partitions;
    }

    private List<List<Integer>> generateCombinations(List<Integer> set, int n) {
        if (n == 0) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        if (set.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> list = new ArrayList<>(set);
        Integer head = list.get(0);
        List<Integer> rest = new ArrayList<>(list.subList(1, list.size()));

        List<List<Integer>> combs = new ArrayList<>();
        for (List<Integer> comb : generateCombinations(rest, n - 1)) {
            List<Integer> newComb = new ArrayList<>(comb);
            newComb.add(head);
            combs.add(newComb);
        }
        combs.addAll(generateCombinations(rest, n));

        return combs;
    }

}


class Test {

    public static void main(String[] args) {
        List<Double> L = List.of(1., 2., 0., 1.);
        int m = 2;
        try {
            EvenPartition uniquePartitions = new EvenPartition(L, m);
            List<List<Double>> ret = uniquePartitions.getResult();
            System.out.println("Nombre de partitions uniques : " + ret.size());
            ret.forEach(partition -> System.out.println(partition));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}


